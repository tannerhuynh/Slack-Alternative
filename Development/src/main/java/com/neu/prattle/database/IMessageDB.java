package com.neu.prattle.database;

import com.neu.prattle.model.Message;

import java.sql.SQLException;
import java.util.Set;

/**
 * Class that acts as a database controller. The controller is responsible for interacting with the
 * database to perform all the CRUD operations on message. There are methods to get, set, add, and
 * remove messages from the database instance.
 *
 * @author Team 9
 * @version 2019-11-9
 */
public interface IMessageDB {
    /**
     * Set test mode for the database interface. Should be called and only be called for test
     * purposes.
     */
    void testMode();

    /**
     * add a message to the database
     *
     * @param message message to be added
     * @throws SQLException if database error occurs
     */
    void addMessage(Message message) throws SQLException;

    /**
     * get all messages between two input users in the database
     *
     * @param user1 the first user's username
     * @param user2 the second user's username
     * @return set of all messages between user1 and user2
     * @throws SQLException if database error occurs
     */
    Set<Message> getMessagesBetweenUsers(String user1, String user2) throws SQLException;

    /**
     * get all messages from input user in the database
     *
     * @param from user's username
     * @return set of all messages the input user sent
     * @throws SQLException if database error occurs
     */
    Set<Message> getMessagesByFrom(String from) throws SQLException;

    /**
     * get all messages to input user in the database
     *
     * @param to user's username
     * @return set of all messages sent to the input user
     * @throws SQLException if database error occurs
     */
    Set<Message> getMessagesByToUser(String to) throws SQLException;

    /**
     * get all messages to the input channel in the database
     *
     * @param to channel id
     * @return set of all messages sent to the input channel
     * @throws SQLException if database error occurs
     */
    Set<Message> getMessagesByToChannel(int to) throws SQLException;

    /**
     * delete the input message from database
     *
     * @param message message to delete
     * @throws SQLException if database error occurs
     */
    void removeMessage(Message message) throws SQLException;

    /**
     * update the input message in the database
     *
     * @param message message to update
     * @throws SQLException if database error occurs
     */
    void updateMessage(Message message) throws SQLException;

    /**
     * Remove all messages in the database.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    void removeAllMessages() throws SQLException;
}
