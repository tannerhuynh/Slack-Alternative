package com.neu.prattle.websocket;


import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.IMessageService;
import com.neu.prattle.service.IUserService;
import com.neu.prattle.service.UserService;

/**
 * A simple chat client based on websockets. Handles messages that arrive on the server.
 *
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket/ChatEndpoint.java
 * @version dated 2017-03-05
 */
@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {
    /**
     * The User Service instance - a singleton design pattern.
     */
    private IUserService accountService = UserService.getInstance();
    /**
     * The message service instance - a singleton design pattern.
     */
    private IMessageService messageService = MessageService.getInstance();
    /**
     * The session. Represents a conversation between two ChatEndpoints.
     */
    private Session session;
    /**
     * Logger. Stores errors/system information.
     */
    private static Logger logger = Logger.getLogger(ChatEndpoint.class.getName());
    /**
     * The constant chatEndpoints.
     */
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    /**
     * Represents users involved in this ChatEndpoint. It is a map between session IDs and usernames.
     */
    private static HashMap<String, String> users = new HashMap<>();

    /**
     * On open.
     *
     * Handles opening a new session (websocket connection). If the user is a known user (user
     * management), the session added to the pool of sessions and an announcement to that pool is
     * made informing them of the new user.
     *
     * If the user is not known, the pool is not augmented and an error is sent to the originator.
     *
     * @param session  the web-socket (the connection)
     * @param username the name of the user (String) used to find the associated IUserService
     *                 object
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws EncodeException the encode exception
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        try {
            Optional<User> user = accountService.findUserByName(username);

            // If the user isn't present send out an error.
            if (!user.isPresent()) {
                Message error = Message.messageBuilder()
                        .setMessageContent(String.format("User %s could not be found", username))
                        .build();

                session.getBasicRemote().sendObject(error);
                return;
            }

            // Otherwise, broadcast the message to all users within this chatendpoint.
            addEndpoint(session, username);
            Message message = createConnectedMessage(username);
            broadcastChannel(message);
        } catch (SQLException e) {
            Message error = Message.messageBuilder().setMessageContent(
                    String.format("Database error: %s", e.getMessage()))
                    .build();
            session.getBasicRemote().sendObject(error);
        }
    }

    /**
     * Creates a Message that some user is now connected - that is, a Session was opened
     * successfully.
     *
     * @param username the username
     * @return Message
     */
    private Message createConnectedMessage(String username) {
        return Message.messageBuilder()
                .setFrom(username)
                .setMessageContent("Connected!")
                .build();
    }

    /**
     * Adds a newly opened session to the pool of sessions.
     *
     * @param session  the newly opened session.
     * @param username the user who connected.
     */
    private void addEndpoint(Session session, String username) {
        this.session = session;
        chatEndpoints.add(this);
        /* users is a hashmap between session ids and users */
        users.put(session.getId(), username);
    }

    /**
     * Broadcast messages between two users.
     *
     * @param message input message.
     */
    private void broadcastBetweenUsers(Message message) {
        ChatEndpoint endpointToUser = getChatEndpointByUsername(message.getToUsername());
        ChatEndpoint endpointFromUser = getChatEndpointByUsername(message.getFromUsername());

        if (endpointToUser == null || endpointFromUser == null) {
            return;
        }
        try {
            endpointToUser.session.getBasicRemote()
                    .sendObject(message);
            endpointFromUser.session.getBasicRemote()
                    .sendObject(message);
        } catch (IOException | EncodeException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Getter for this session's id.
     *
     * @return this session's id.
     */
    public String getSessionID() {
        return session.getId();
    }

    /**
     * Getter for chat endpoint base on input session id.
     *
     * @param sessionId session's id.
     * @return chat endpoint with input id. null is not found.
     */
    private ChatEndpoint getChatEndpointBySessionID(String sessionId) {
        for (ChatEndpoint chatEndpoint : chatEndpoints) {
            if (chatEndpoint.getSessionID().equals(sessionId)) {
                return chatEndpoint;
            }
        }
        return null;
    }

    /**
     * Getter for chat endpoint based on input username.
     *
     * @param username username.
     * @return Chat endpoint with the input username. Null is not found.
     */
    private ChatEndpoint getChatEndpointByUsername(String username) {
        for (Map.Entry<String, String> entry : users.entrySet()) {
            String sessionId = entry.getKey();
            String userId = entry.getValue();
            if (username.equals(userId)) {
                return getChatEndpointBySessionID(sessionId);
            }
        }
        return null;
    }

    /**
     * On message.
     *
     * When a message arrives, decide if it should go to one user, to all users (broadcast), or to all users within
     * a channel (to be implemented).
     *
     * @param session the session originating the message.
     * @param message the text of the inbound message.
     */
    @OnMessage
    public void onMessage(Session session, Message message) {
        message.setFromUsername(users.get(session.getId()));
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));

        try {
            messageService.addMessage(message);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        if (message.getToUsername().equals("") || message.getToUsername() == null){
            broadcastChannel(message);
        }
        else {
            broadcastBetweenUsers(message);
        }
    }

    /**
     * On close.
     *
     * Closes the session by removing it from the pool of sessions and broadcasting the news to
     * everyone else.
     *
     * @param session the session.
     */
    @OnClose
    public void onClose(Session session) {
        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFromUsername(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcastChannel(message);
    }

    /**
     * On error.
     *
     * Handles situations when an error occurs.  Not implemented.
     *
     * @param session   the session with the problem
     * @param throwable the action to be taken.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    /**
     * Broadcast.
     *
     * Send a Message to each session in the pool of sessions. The Message sending action is
     * synchronized.  That is, if another Message tries to be sent at the same time to the same
     * endpoint, it is blocked until this Message finishes being sent..
     *
     * @param message message.
     */
    private static void broadcastChannel(Message message) {
        chatEndpoints.forEach(endpoint -> {

            // The synchronized block ensures only one thread uses the endpoint at the same time
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote()
                            .sendObject(message);
                } catch (IOException | EncodeException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
            }
        });
    }
}

