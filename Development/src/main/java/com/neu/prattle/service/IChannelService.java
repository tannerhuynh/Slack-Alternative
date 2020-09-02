package com.neu.prattle.service;

import com.neu.prattle.model.Channel;
import com.neu.prattle.model.ChannelDTO;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

/**
 * Acts as an interface between the data layer and the servlet controller. The controller is
 * responsible for interfacing with this instance to perform all the CRUD operations on channel
 *
 * @author Team 9
 * @version dated 2019-11-13
 */
public interface IChannelService {
    /**
     * Set test mode for the service. Should be called and only be called for test purposes.
     */
    void testMode();

    /**
     * Get all channels in the system
     * @return Set of all channels
     * @throws SQLException if there is an error in the database.
     */
    Set<Channel> getAllChannels() throws SQLException;

    /**
     * Get channel with the input id
     * @param id channel id
     * @return Optional channel
     * @throws SQLException if there is an error in the database.
     */
    Optional<Channel> getChannelById(int id) throws SQLException;

    /**
     * Add a channel to the system
     * @param channelDTO channel data transfer object
     * @throws SQLException if there is an error in the database
     */
    void addChannel(ChannelDTO channelDTO) throws SQLException;

    /**
     * Remove a channel from the system
     * @param channelId channel id to remove
     * @return int representing the result:
     *  1: success
     *  0: channel not found
     * @throws SQLException if there is an error in the database
     */
    int removeChannel(int channelId) throws SQLException;

    /**
     * Add a user to a channel
     * @param channelId channel id to add user
     * @param username username to add to channel
     * @return int representing the result:
     *  1: success
     *  0: channel not found
     *  -1: user not found
     * @throws SQLException if there is an error in the database
     */
    int addUserToChannel(int channelId, String username) throws SQLException;

    /**
     * Remove a user from a channel
     * @param channelId channel id to remove user
     * @param username username to remove
     * @return int representing the result:
     *  1: success
     *  0: channel not found
     *  -1: user not found
     * @throws SQLException if there is an error in the database
     */
    int removeUserFromChannel(int channelId, String username) throws SQLException;

    /**
     * Promote a user to be the mod in a channel
     * @param channelId channel id to promote user
     * @param username username to promote
     * @return int representing the result:
     *  1: success
     *  0: channel not found
     *  -1: user not found
     * @throws SQLException if there is an error in the database
     */
    int promoteUserInChannel(int channelId, String username) throws SQLException;

    /**
     * Demote a user from mod in a channel
     * @param channelId channel id to demote user
     * @param username username to demote
     * @return int representing the result:
     *  1: success
     *  0: channel not found
     *  -1: user not found
     * @throws SQLException if there is an error in the database
     */
    int demoteUserInChannel(int channelId, String username) throws SQLException;
}
