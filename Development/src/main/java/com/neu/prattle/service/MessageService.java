package com.neu.prattle.service;

import com.neu.prattle.database.IMessageDB;
import com.neu.prattle.database.MessageDBHibernate;
import com.neu.prattle.database.MessageDBInMemory;
import com.neu.prattle.model.Message;

import java.sql.SQLException;
import java.util.*;

/**
 * Implementation of {@link IMessageService}. Provides message related services.
 *
 * @author Team 9
 * @version dated 2019-10-29
 */
public class MessageService implements IMessageService {

    /**
     * Message service instance.
     */
    private static MessageService messageService;

    /**
     * Message database.
     */
    private IMessageDB messageDB;

    /**
     * MessageService is a Singleton class. Currently if the connection to database failed, the
     * constructor will keep trying to reconnect in a loop.
     */
    private MessageService() {
        messageDB = MessageDBHibernate.getInstance();
    }



    /**
     * Creation of a Message service.
     */
    static {
        messageService = new MessageService();
    }


    /**
     * Call this method to return an instance of this service.
     *
     * @return this
     */
    public static MessageService getInstance() {
        return messageService;
    }

    /**
     * clear the instances in the InMemory database to avoid conflicts in different test cases
     */
    @Override
    public void testMode() throws SQLException {

        messageDB = MessageDBInMemory.getInstance();
        messageDB.removeAllMessages();

    }

    /**
     * Tries to add a Message in the system. I don't believe there is a need to throw any already
     * present exception. -AT
     *
     * @param message Message object
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public synchronized void addMessage(Message message) throws SQLException {
        messageDB.addMessage(message);
    }


    /**
     * Getter for all Message objects between two input users. This contains both ways of from and
     * to.
     *
     * @param user1 the first user.
     * @param user2 the second user.
     * @return list of Messages between those two input users.
     * @throws SQLException if users don't exist or there is some issue
     */
    @Override
    public List<Message> getMessagesByUsers(String user1, String user2) throws SQLException {

        List<Message> messages = new ArrayList<>(messageDB.getMessagesBetweenUsers(user1, user2));
        return sortMessagesByTimestamp(messages);
    }



    /**
     * Getter for all Message objects within a channel.
     *
     * @param channel the channel
     * @return list of Messages sent within the channel.
     * @throws SQLException if channel don't exist or there is some issue
     */
    @Override
    public List<Message> getMessagesWithinChannel(int channel) throws SQLException {

        List<Message> messages = new ArrayList<>(messageDB.getMessagesByToChannel(channel));
        return sortMessagesByTimestamp(messages);
    }

    /**
     * Getter for all Message objects based on the input from user.
     *
     * @param from the from user to search message on.
     * @return list of messages from the input user.
     * @throws SQLException when the user doesn't exist/database error
     */
    @Override
    public List<Message> getMessagesByFrom(String from) throws SQLException {

        List<Message> messages = new ArrayList<>(messageDB.getMessagesByFrom(from));
        return sortMessagesByTimestamp(messages);
    }

    /**
     * Getter for all Message objects based on the input to user.
     *
     * @param to the to user to search message on.
     * @return list of messages to the input user.
     * @throws SQLException when the user doesn't exist/database error
     */
    @Override
    public List<Message> getMessagesByTo(String to) throws SQLException {
        List<Message> messages = new ArrayList<>(messageDB.getMessagesByToUser(to));
        return sortMessagesByTimestamp(messages);
    }


    /**
     * Remove all Messages in the system.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public synchronized void removeAllMessages() throws SQLException {
        messageDB.removeAllMessages();
    }


    /**
     * Sorts the list of Message objects by the earliest to latest timestamp.
     *
     * @param messages List of messages.
     * @return A sorted list of messages by timestamp.
     */
    private List<Message> sortMessagesByTimestamp(List<Message> messages) {
        messages.sort((m1, m2) -> {
            if (m1.getTimestamp() == null || m2.getTimestamp() == null)
                return 0;
            return m1.getTimestamp().compareTo(m2.getTimestamp());
        });
        return messages;
    }








}
