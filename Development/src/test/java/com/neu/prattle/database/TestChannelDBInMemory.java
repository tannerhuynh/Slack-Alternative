package com.neu.prattle.database;

import com.neu.prattle.model.Channel;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestChannelDBInMemory {
    private ChannelDBInMemory channelDB = ChannelDBInMemory.getInstance();
    private Set<Channel> channelSet = new HashSet<>();

    @Before
    public void setUp(){
        channelDB.testMode();
        channelSet.add(new Channel(0, "channel0"));
        channelSet.add(new Channel(1, "channel1"));
        channelSet.add(new Channel(2, "channel1"));
        channelDB.setChannelSet(channelSet);
    }

    @Test
    public void getAllChannels() {
        assertEquals(channelSet, channelDB.getAllChannels());
    }

    @Test
    public void getChannel() {
        assertTrue(channelSet.contains(channelDB.getChannel(0).orElse(null)));
        assertFalse(channelDB.getChannel(10).isPresent());
    }

    @Test
    public void getChannels() {
        assertEquals(1, channelDB.getChannels("channel0").size());
        assertEquals(2, channelDB.getChannels("channel1").size());
        assertEquals(0, channelDB.getChannels("channel2").size());
    }

    @Test
    public void addChannel() {
        channelDB.addChannel(new Channel(3, "channel3"));
        assertEquals(1, channelDB.getChannels("channel3").size());
    }

    @Test
    public void removeChannel() {
        channelDB.removeChannel(channelDB.getChannel(0).orElse(null));
        assertEquals(0, channelDB.getChannels("channel0").size());
    }

    @Test
    public void updateChannel() {
        channelDB.getChannel(0).ifPresent(channel -> channel.setName("new_name"));
        channelDB.updateChannel(channelDB.getChannel(0).orElse(null));
        assertEquals(0, channelDB.getChannels("channel0").size());
        assertEquals(1, channelDB.getChannels("new_name").size());
    }
}
