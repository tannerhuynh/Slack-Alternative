package com.neu.prattle.service;

import com.neu.prattle.database.ChannelDBInMemory;
import com.neu.prattle.database.UserDBInMemory;
import com.neu.prattle.model.Channel;
import com.neu.prattle.model.ChannelDTO;
import com.neu.prattle.model.User;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class TestChannelService {

    private ChannelService channelService = ChannelService.getInstance();
    private UserDBInMemory userDB = UserDBInMemory.getInstance();
    private ChannelDBInMemory channelDB = ChannelDBInMemory.getInstance();

    @Before
    public void setUp() throws Exception {
        channelService.testMode();
    }

    @Test
    public void getAllChannels() throws SQLException {
        assertEquals(0, channelService.getAllChannels().size());
        channelDB.addChannel(new Channel(0, "channel0"));
        channelDB.addChannel(new Channel(1, "channel1"));
        assertEquals(2, channelService.getAllChannels().size());
    }

    @Test
    public void getChannelById() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        channelDB.addChannel(new Channel(1, "channel1"));
        assertEquals("channel0", Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getName());
    }

    @Test
    public void addChannel() throws SQLException{
        userDB.addUser(new User("user0"));
        userDB.addUser(new User("user1"));
        List<String> userList = new ArrayList<>();
        userList.add("user0");
        userList.add("user1");
        List<String> modList = new ArrayList<>();
        modList.add("user0");
        channelService.addChannel(new ChannelDTO(0, "channel0", userList, modList));
        Channel channel = Objects.requireNonNull(channelService.getChannelById(0).orElse(null));
        assertEquals(2, channel.getParticipants().size());
        assertEquals(1, channel.getMods().size());
    }

    @Test
    public void removeChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        channelDB.addChannel(new Channel(1, "channel1"));
        assertEquals(1, channelService.removeChannel(0));
        assertEquals(0, channelService.removeChannel(2));
    }

    @Test
    public void addUserToChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        userDB.addUser(new User("user1"));
        assertEquals(0, channelService.addUserToChannel(1, "user0"));
        assertEquals(-1, channelService.addUserToChannel(0, "user2"));
        assertEquals(1, channelService.addUserToChannel(0, "user0"));
        assertEquals(1, channelService.addUserToChannel(0, "user1"));
        assertEquals(1, channelService.promoteUserInChannel(0, "user1"));
        assertEquals(1, channelService.addUserToChannel(0, "user1"));
        assertEquals(1, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getParticipants().size());
    }

    @Test
    public void removeUserFromChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        channelService.addUserToChannel(0, "user0");
        assertEquals(0, channelService.removeUserFromChannel(1, "user0"));
        assertEquals(-1, channelService.removeUserFromChannel(0, "user1"));
        assertEquals(1, channelService.removeUserFromChannel(0, "user0"));
        assertEquals(0, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getParticipants().size());
    }

    @Test
    public void promoteUserInChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        userDB.addUser(new User("user1"));
        channelService.addUserToChannel(0, "user0");
        assertEquals(0, channelService.promoteUserInChannel(1, "user0"));
        assertEquals(-1, channelService.promoteUserInChannel(0, "user1"));
        assertEquals(-1, channelService.promoteUserInChannel(0, "user2"));
        assertEquals(1, channelService.promoteUserInChannel(0, "user0"));
        assertEquals(1, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getMods().size());
    }

    @Test
    public void demoteUserInChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        userDB.addUser(new User("user1"));
        channelService.addUserToChannel(0, "user0");
        channelService.promoteUserInChannel(0, "user0");
        assertEquals(0, channelService.demoteUserInChannel(1, "user0"));
        assertEquals(-1, channelService.demoteUserInChannel(0, "user1"));
        assertEquals(-1, channelService.demoteUserInChannel(0, "user2"));
        assertEquals(1, channelService.demoteUserInChannel(0, "user0"));
        assertEquals(0, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getMods().size());
    }
}