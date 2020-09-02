package com.neu.prattle.websocket;

import com.neu.prattle.model.Channel;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.ChannelService;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CM chat end point. Handles channel messages
 *
 * @author Team9
 * @version dated 2019-11-20
 */
@ServerEndpoint(value = "/cm/{channel_id}/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class CMEndpoint extends AbstractEndpoint {
    /**
     * The from username and to channel id of the endpoint
     */
    private String username;
    private int channelId;
    /**
     * Logger. Stores errors/system information.
     */
    private static Logger logger = Logger.getLogger(CMEndpoint.class.getName());

    /**
     * Add a newly opened session.
     * If there exists a chat subject for the input channel, set it as this.chatSubject. Otherwise create a new chat subject
     *
     * @param session    the session
     * @param username   the username of this endpoint
     * @param channelId  the channel id of the target channel
     */
    private void addEndpoint(Session session, String username, int channelId) {
        this.session = session;
        this.username = username;
        this.channelId = channelId;
        IChatSubject subject = chatSubjectHashMap.getOrDefault("channel" + channelId, null);
        if (subject == null) {
            subject = new ChatSubject();
            chatSubjectHashMap.put("channel" + channelId, subject);
        }
        this.chatSubject = subject;
        chatSubject.attach(this);
    }

    /**
     * On open
     * Handles opening a new session (websocket connection). If the user is a known user (user
     * management), setup the end point by calling addEndpoint
     * If the user is not known, the pool is not augmented and an error is sent to the originator.
     *
     * @param session    the web-socket (the connection)
     * @param channelId  the id of the target channel
     * @param username   the name of the user (String) used to find the associated IUserService
     *                   object
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws EncodeException the encode exception
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("channel_id") int channelId, @PathParam("username") String username) throws IOException, EncodeException {
        try {
            Optional<User> user = userService.findUserByName(username);

            // If the user isn't present send out an error.
            if (!user.isPresent()) {
                Message error = Message.messageBuilder()
                        .setMessageContent(String.format("User %s could not be found", username))
                        .build();

                session.getBasicRemote().sendObject(error);
                return;
            }

            Optional<Channel> channel = ChannelService.getInstance().getChannelById(channelId);
            if (!channel.isPresent()) {
                Message error = Message.messageBuilder()
                        .setMessageContent(String.format("Channel %d could not be found", channelId))
                        .build();
                session.getBasicRemote().sendObject(error);
                return;
            }

            addEndpoint(session, username, channelId);
        } catch (SQLException e) {
            Message error = Message.messageBuilder().setMessageContent(
                    String.format("Database error: %s", e.getMessage()))
                    .build();
            session.getBasicRemote().sendObject(error);
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * On message
     * When a message arrives, save the message in the database, and sent it to both this endpoint and the target user endpoint
     *
     * @param session the session originating the message.
     * @param message the text of the inbound message.
     */
    @OnMessage
    public void onMessage(Session session, Message message) {
        message.setFromUsername(username);
        message.setToChannelId(channelId);
        message.setToUsername(null);
        message.setTimestamp(Timestamp.valueOf(LocalDateTime.ofInstant(Calendar.getInstance().toInstant(), ZoneId.of("America/New_York"))));
        
        addAndSetMessage(message, username);
    }

    /**
     * On close.
     * Closes the session by detaching it from the chat subject
     *
     * @param session the session.
     */
    @OnClose
    public void onClose(Session session) {
        chatSubject.detach(this);
    }
}
