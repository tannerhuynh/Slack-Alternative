package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.IMessageService;
import com.neu.prattle.service.IUserService;
import com.neu.prattle.service.UserService;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The abstract end point
 *
 * @author Team 9
 * @version dated 2019-11-20
 */
public abstract class AbstractEndpoint implements IObserver {
    /**
     * The User Service instance - a singleton design pattern.
     */
    protected IUserService userService = UserService.getInstance();
    /**
     * The message service instance - a singleton design pattern.
     */
    protected IMessageService messageService = MessageService.getInstance();
    /**
     * The session. Represents a conversation between two ChatEndpoints.
     */
    protected Session session;
    /**
     * The chat subject being observed
     */
    protected IChatSubject chatSubject;
    /**
     * The map of all chat subjects
     */
    protected static Map<String, IChatSubject> chatSubjectHashMap = new HashMap<>();
    /**
     * The logger
     */
    private static Logger logger = Logger.getLogger(AbstractEndpoint.class.getName());

    /**
     * Setter for user service
     *
     * @param userService an ser service
     */
    void setUserService(IUserService userService) {
        this.userService = userService;
    }

    /**
     * Setter for message service
     *
     * @param messageService a message service
     */
    void setMessageService(IMessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Getter for chat subject
     *
     * @return the chat subject being observed by this
     */
    IChatSubject getChatSubject() {
        return chatSubject;
    }

    /**
     * Setter for chat subject map
     *
     * @param chatSubjectHashMap chat subject map
     */
    static void setChatSubjectHashMap(Map<String, IChatSubject> chatSubjectHashMap) {
        AbstractEndpoint.chatSubjectHashMap = chatSubjectHashMap;
    }

    /**
     * Get the message from chat subject and send to session
     */
    @Override
    public void update() {
        try {
            session.getBasicRemote().sendObject(chatSubject.getMessage());
        } catch (IOException | EncodeException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Add the input message to database and set it to chatSubject
     *
     * @param message incoming message
     * @param username username of the user who sent the message
     */
    void addAndSetMessage(Message message, String username){
        try {
            messageService.addMessage(message);
            Optional<User> optionalUser = userService.findUserByName(username);
            optionalUser.ifPresent(user -> message.setFromAvatar(user.getAvatar()));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        chatSubject.setMessage(message);
    }
}
