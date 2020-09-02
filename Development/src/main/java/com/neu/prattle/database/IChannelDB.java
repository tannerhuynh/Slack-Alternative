package com.neu.prattle.database;

import com.neu.prattle.model.Channel;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

/**
 * Class that acts as a database controller. The controller is responsible for interacting with the
 * database to perform all the CRUD operations on channel. There are methods to get, set, add, and
 * remove channel from the database instance.
 *
 * @author Team 9
 * @version 2019-10-30
 */
public interface IChannelDB {
    /**
     * Set test mode for the database interface. Should be called and only be called for test
     * purposes.
     */
    void testMode();

    /**
     * Get all channels in the database
     * @return all channels in the database
     * @throws SQLException if the connection failed or there is an error.
     */
    Set<Channel> getAllChannels() throws SQLException;

    /**
     * Get channel with id in the database
     * @param id id of the channel
     * @return optional channel with input id
     * @throws SQLException if the connection failed or there is an error.
     */
    Optional<Channel> getChannel(int id) throws SQLException;

    /**
     * Get channel with name in the database
     * @param name name of the channel
     * @return set of channels with input name
     * @throws SQLException if the connection failed or there is an error.
     */
    Set<Channel> getChannels(String name) throws SQLException;

    /**
     * Add a channel to the database
     * @param channel channel to add
     * @throws SQLException if the connection failed or there is an error.
     */
    void addChannel(Channel channel) throws SQLException;

    /**
     * Remove a channel from the database
     * @param channel channel to remove
     * @throws SQLException if the connection failed or there is an error.
     */
    void removeChannel(Channel channel) throws SQLException;

    /**
     * Update the changes in the input channel to the database
     * @param channel channel to change
     * @throws SQLException if the connection failed or there is an error.
     */
    void updateChannel(Channel channel) throws SQLException;
}
