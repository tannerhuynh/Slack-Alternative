package com.neu.prattle.service;

import com.neu.prattle.database.*;
import com.neu.prattle.model.Channel;
import com.neu.prattle.model.ChannelDTO;
import com.neu.prattle.model.User;

import java.sql.SQLException;
import java.util.*;

/**
 * Implementation of {@link IChannelService}. Provides channel related services.
 *
 * @author Team 9
 * @version dated 2019-11-13
 */
public class ChannelService implements IChannelService {
    /**
     * Singleton
     */
    private static ChannelService channelService = new ChannelService();

    /**
     * Singleton design pattern to use a channel - everyone who uses the channel service, uses the same one.
     * @return the instance of this channel service
     */
    public static ChannelService getInstance(){
        return channelService;
    }

    /**
     * Initializes the channel database - in memory.
     */
    private IChannelDB channelDB = ChannelDBHibernate.getInstance();
    /**
     * Initializes the user database using Hibernate.
     */
    private IUserDB userDB = UserDBHibernate.getInstance();

    /**
     * Singleton design pattern initialization - use getInstance to use this object
     */
    private ChannelService(){

    }

    /**
     * Get a list of users base on the input username list
     * @param usernameList list of usernames
     * @return list of users with the input usernames
     * @throws SQLException when there is an error in the database
     */
    private List<User> getUsers(List<String> usernameList) throws SQLException{
        List<User> userList = new ArrayList<>();
        for (String username: usernameList){
            Optional<User> optionalUser = userDB.findUserByName(username);
            optionalUser.ifPresent(userList::add);
        }
        return userList;
    }

    /**
     * Use the InMemory database instead of the actual database for testing.
     * each call will also clear the instances in the InMemory database to avoid conflicts in different test cases
     */
    @Override
    public void testMode() {
        channelDB = ChannelDBInMemory.getInstance();
        userDB = UserDBInMemory.getInstance();
        channelDB.testMode();
        userDB.testMode();
    }

    /**
     * Get all channels in the system
     * @return Set of all channels
     * @throws SQLException if there is an error in the database.
     */
    @Override
    public Set<Channel> getAllChannels() throws SQLException {
        return channelDB.getAllChannels();
    }

    /**
     * Get channel with the input id
     * @param id channel id
     * @return Optional channel
     * @throws SQLException if there is an error in the database.
     */
    @Override
    public Optional<Channel> getChannelById(int id) throws SQLException {
        return channelDB.getChannel(id);
    }

    /**
     * Add a channel to the system
     * @param channelDTO channel data transfer object
     * @throws SQLException if there is an error in the database
     */
    @Override
    public void addChannel(ChannelDTO channelDTO) throws SQLException {
        Channel channel = new Channel();
        channel.setName(channelDTO.getName());
        channel.setParticipants(new HashSet<>(getUsers(channelDTO.getParticipants())));
        channel.setMods(new HashSet<>(getUsers(channelDTO.getMods())));
        channelDB.addChannel(channel);
    }

    /**
     * Remove a channel from the system
     * @param channelId channel id to remove
     * @return int representing the result:
     *  1: success
     *  0: channel not found
     * @throws SQLException if there is an error in the database
     */
    @Override
    public int removeChannel(int channelId) throws SQLException {
        Optional<Channel> optionalChannel = channelDB.getChannel(channelId);
        if (optionalChannel.isPresent()){
            channelDB.removeChannel(optionalChannel.get());
            return 1;
        }
        return 0;
    }

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
    @Override
    public int addUserToChannel(int channelId, String username) throws SQLException {
        Optional<Channel> optionalChannel = channelDB.getChannel(channelId);
        Optional<User> optionalUser = userDB.findUserByName(username);
        if (!optionalChannel.isPresent()){
            return 0;
        }
        if (!optionalUser.isPresent()){
            return -1;
        }
        Channel channel = optionalChannel.get();
        channel.addParticipant(optionalUser.get());
        removeUserDuplication(channel);
        channelDB.updateChannel(channel);
        return 1;
    }

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
    @Override
    public int removeUserFromChannel(int channelId, String username) throws SQLException {
        Optional<Channel> optionalChannel = channelDB.getChannel(channelId);
        Optional<User> optionalUser = userDB.findUserByName(username);
        if (!optionalChannel.isPresent()){
            return 0;
        }
        if (!optionalUser.isPresent()){
            return -1;
        }
        Channel channel = optionalChannel.get();
        channel.getParticipants().remove(optionalUser.get());
        removeUserDuplication(channel);
        channelDB.updateChannel(channel);
        return 1;
    }

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
    @Override
    public int promoteUserInChannel(int channelId, String username) throws SQLException {
        Optional<Channel> optionalChannel = channelDB.getChannel(channelId);
        Optional<User> optionalUser = userDB.findUserByName(username);
        if (!optionalChannel.isPresent()){
            return 0;
        }
        if (!optionalUser.isPresent()){
            return -1;
        }
        Channel channel = optionalChannel.get();
        User user = optionalUser.get();
        if (!channel.getParticipants().contains(user)){
            return -1;
        }
        channel.getParticipants().remove(user);
        channel.addMod(user);
        removeUserDuplication(channel);
        channelDB.updateChannel(channel);
        return 1;
    }

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
    @Override
    public int demoteUserInChannel(int channelId, String username) throws SQLException {
        Optional<Channel> optionalChannel = channelDB.getChannel(channelId);
        Optional<User> optionalUser = userDB.findUserByName(username);
        if (!optionalChannel.isPresent()){
            return 0;
        }
        if (!optionalUser.isPresent()){
            return -1;
        }
        Channel channel = optionalChannel.get();
        User user = optionalUser.get();
        if (!channel.getMods().contains(user)){
            return -1;
        }
        channel.getMods().remove(user);
        channel.addParticipant(user);
        removeUserDuplication(channel);
        channelDB.updateChannel(channel);
        return 1;
    }

    /**
     * Remove user in participant list if he is in mod list inside the input channel
     *
     * @param channel the channel to remove duplicated user
     */
    private void removeUserDuplication(Channel channel) {
        for (User user : channel.getMods()){
            channel.getParticipants().remove(user);
        }
    }
}
