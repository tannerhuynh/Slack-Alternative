package com.neu.prattle.service;

import com.neu.prattle.model.Message;

import java.sql.SQLException;
import java.util.List;

/**
 * Acts as an interface between the data layer and the servlet controller. The controller is
 * responsible for interfacing with this instance to perform all the CRUD operations on messages.
 *
 * @author Team 9
 * @version dated 2019-10-29
 */
public interface IMessageService {
    /**
     * Set test mode for the service. Should be called and only be called for test purposes.
     */
    void testMode() throws SQLException;

    /**
     * Add a message in the system.
     *
     * @param message message to add.
     * @throws SQLException if the connection failed or there is an error.
     */
    void addMessage(Message message) throws SQLException;

    /**
     * Getter for all messages between two input users. this contains both ways of from and to.
     *
     * @param user1 the first user.
     * @param user2 the second user.
     * @return list of messages between those two input users.
     * @throws SQLException if the connection failed or there is an error.
     */
    List<Message> getMessagesByUsers(String user1, String user2) throws SQLException;

    /**
     * Getter for all messages base on the input from user.
     *
     * @param from the from user to search message on.
     * @return list of messages from the input user.
     * @throws SQLException if the connection failed or there is an error.
     */
    List<Message> getMessagesByFrom(String from) throws SQLException;

    /**
     * Getter for all messages base on the input to user.
     *
     * @param to the to user to search message on.
     * @return list of messages to the input user.
     * @throws SQLException if the connection failed or there is an error.
     */
    List<Message> getMessagesByTo(String to) throws SQLException;

    /**
     * Remove all messages in the system.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    void removeAllMessages() throws SQLException;

    /**
     * Getter for all Message objects within a channel.
     *
     * @param channel the channel ID
     * @return list of Messages sent within the channel.
     * @throws SQLException if channel don't exist or there is some issue
     */
    List<Message> getMessagesWithinChannel(int channel) throws SQLException;

}
