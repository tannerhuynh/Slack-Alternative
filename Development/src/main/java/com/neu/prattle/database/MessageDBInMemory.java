package com.neu.prattle.database;

import com.neu.prattle.model.Message;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Class that implements IMessageDB to create a database in memory. This acts similar to a remote
 * database, but allows for testing and working with methods before setting up the actual
 * database implementation.
 *
 * @author Team 9
 * @version 2019-10-30
 */
public class MessageDBInMemory implements IMessageDB {

    /**
     * Database instance.
     */
    private static MessageDBInMemory messageDB;

    /**
     * Hash set of messages.
     */
    private Set<Message> messageSet = new HashSet<>();

    /**
     * MessageDBInMemory is a Singleton class. No initialization has to happenb.
     */
    private MessageDBInMemory() {
    }

    static {
        messageDB = new MessageDBInMemory();
    }

    /**
     * Call this method to return an instance (singleton design pattern).
     *
     * @return the instance of this memory.
     */
    public static MessageDBInMemory getInstance() {
        return messageDB;
    }


    /**
     * Set test mode for the database interface. Should be called and only be called for test
     * purposes. Cleans out all current messages in case of interference.
     */
    @Override
    public void testMode() {
        removeAllMessages();
    }



    /**
     * add a message to the database
     *
     * @param message message to be added
     */
    @Override
    public void addMessage(Message message) {

        messageSet.add(message);

    }

    /**
     * get all messages between two input users in the database - commutable
     *
     * @param user1 the first user's username
     * @param user2 the second user's username
     * @return Set of all messages between user1 and user2
     */
    @Override
    public Set<Message> getMessagesBetweenUsers(String user1, String user2) {

        Set<Message> output = new HashSet<>();

        // Iterate through each message. If the 'from' and 'to' fields match the users of interest,
        // then add to output Set.
        for (Message m : messageSet) {

            // User 1 sent to user 2
            // User 2 sent to user 1
            if ( (m.getFromUsername().equals(user1) && m.getToUsername().equals(user2))
                || (m.getToUsername().equals(user1) && m.getFromUsername().equals(user2)) ){
                output.add(m);
            }

        }

        return output;

    }


    /**
     * get all messages from input user in the database
     *
     * @param from user's username
     * @return Set of all messages the input user sent
     */
    @Override
    public Set<Message> getMessagesByFrom(String from)  {
        Set<Message> output = new HashSet<>();

        // Iterate through each message. If the 'from' field matches the user of interest,
        // then add to output Set.
        for (Message m : messageSet) {
            if (m.getFromUsername().equals(from)){
                output.add(m);
            }
        }


        return output;

    }

    /**
     * get all messages to input user in the database
     *
     * @param to user's username
     * @return Set of all messages sent to the input user
     */
    @Override
    public Set<Message> getMessagesByToUser(String to)  {

        Set<Message> output = new HashSet<>();

        // Iterate through each message. If the 'to' field matches the user of interest,
        // then add to output Set.
        for (Message m : messageSet) {
            if (m.getToUsername().equals(to)){
                output.add(m);
            }
        }

        return output;
    }

    /**
     * get all messages to the input channel in the database
     *
     * @param to channel id
     * @return set of all messages sent to the input channel
     */
    @Override
    public Set<Message> getMessagesByToChannel(int to) {
        return messageSet.stream().filter(message -> message.getToChannelId() == to).collect(Collectors.toSet());
    }

    /**
     * delete the input message from database
     *
     * @param message message to delete
     */
    @Override
    public void removeMessage(Message message){
                    this.messageSet.remove(message);
    }

    /**
     * update the input message in the database
     *
     * @param message message to update
     */
    @Override
    public void updateMessage(Message message)  {
        // Implement later? Why would we update a message exactly, and wouldn't it be updated WITH something to change?

    }

    /**
     * Removes all the messages in the database by initializing a new set entirely.
     */
    public void removeAllMessages() {
        messageSet = new HashSet<>();
    }
}
