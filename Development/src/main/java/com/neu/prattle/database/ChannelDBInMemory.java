package com.neu.prattle.database;

import com.neu.prattle.model.Channel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that implements {@link IChannelDB} to create a database in memory. This acts similar to a remote
 * database, but allows for testing and working with methods before setting up the actual
 * database implementation.
 *
 * @author Team 9
 * @version 2019-11-12
 */
public class ChannelDBInMemory implements IChannelDB {
    /**
     * Singleton design pattern initialization
     */
    private static ChannelDBInMemory channelDBInMemory = new ChannelDBInMemory();
    /**
     * Singleton design pattern initialization
     */
    public static ChannelDBInMemory getInstance(){
        return channelDBInMemory;
    }
    /**
     * Singleton design pattern initialization
     */
    private ChannelDBInMemory(){}

    /**
     * Store channels in memory
     */
    private Set<Channel> channelSet = new HashSet<>();

    /**
     * Setter for channelSet
     * @param channelSet channelSet
     */
    public void setChannelSet(Set<Channel> channelSet) {
        this.channelSet = channelSet;
    }

    /**
     * Set test mode for the database interface. Should be called and only be called for test
     * purposes.
     * remove all instances in memory
     */
    @Override
    public void testMode() {
        channelSet = new HashSet<>();
    }

    /**
     * Get all channels in the database
     *
     * @return all channels in the database
     */
    @Override
    public Set<Channel> getAllChannels() {
        return channelSet;
    }

    /**
     * Get channel with id in database
     *
     * @param id id of the channel
     * @return channel with input id
     */
    @Override
    public Optional<Channel> getChannel(int id) {
        return channelSet.stream().filter(channel -> channel.getId()==(id)).findAny();
    }

    /**
     * Get channel with name in the database
     *
     * @param name name of the channel
     * @return set of channels with input name
     */
    @Override
    public Set<Channel> getChannels(String name) {
        return channelSet.stream().filter(channel -> channel.getName().equals(name)).collect(Collectors.toSet());
    }

    /**
     * Add a channel to the database
     *
     * @param channel channel to add
     */
    @Override
    public void addChannel(Channel channel) {
        channelSet.add(channel);
    }

    /**
     * Remove a channel from the database
     *
     * @param channel channel to remove
     */
    @Override
    public void removeChannel(Channel channel) {
        channelSet.remove(channel);
    }

    /**
     * Update the changes in the input channel to the database
     *
     * @param channel channel to change
     */
    @Override
    public void updateChannel(Channel channel) {
        channelSet.remove(channel);
        channelSet.add(channel);
    }
}
