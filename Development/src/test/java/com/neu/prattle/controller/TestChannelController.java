package com.neu.prattle.controller;

import com.neu.prattle.database.ChannelDBInMemory;
import com.neu.prattle.database.UserDBInMemory;
import com.neu.prattle.model.Channel;
import com.neu.prattle.model.ChannelDTO;
import com.neu.prattle.model.User;
import com.neu.prattle.service.ChannelService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class TestChannelController {
    private ChannelController channelController = new ChannelController();
    private ChannelService channelService = ChannelService.getInstance();
    private UserDBInMemory userDB = UserDBInMemory.getInstance();
    private ChannelDBInMemory channelDB = ChannelDBInMemory.getInstance();

    @Before
    public void setUp() throws Exception {
        channelService.testMode();
    }

    @Test
    public void getAllChannels() {
        channelDB.addChannel(new Channel(0, "channel0"));
        channelDB.addChannel(new Channel(1, "channel1"));
        assertEquals(200, channelController.getAllChannels().getStatus());
    }

    @Test
    public void getChannelById() {
        channelDB.addChannel(new Channel(0, "channel0"));
        assertEquals(200, channelController.getChannelById(0).getStatus());
        assertEquals(404, channelController.getChannelById(1).getStatus());

    }

    @Test
    public void removeChannel() {
        channelDB.addChannel(new Channel(0, "channel0"));
        assertEquals(200, channelController.removeChannel(0).getStatus());
        assertEquals(404, channelController.removeChannel(0).getStatus());
    }

    @Test
    public void addChannel() throws SQLException {
        userDB.addUser(new User("user0"));
        userDB.addUser(new User("user1"));
        List<String> userList = new ArrayList<>();
        userList.add("user0");
        userList.add("user1");
        List<String> modList = new ArrayList<>();
        modList.add("user0");
        channelController.addChannel(new ChannelDTO(0, "channel0", userList, modList));
        Channel channel = Objects.requireNonNull(channelService.getChannelById(0).orElse(null));
        assertEquals(2, channel.getParticipants().size());
        assertEquals(1, channel.getMods().size());
    }

    @Test
    public void addUserToChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        assertEquals("channel not found", channelController.addUserToChannel(1, "user0").getEntity());
        assertEquals("user not found", channelController.addUserToChannel(0, "user1").getEntity());
        assertEquals(200, channelController.addUserToChannel(0, "user0").getStatus());
        assertEquals(1, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getParticipants().size());
    }

    @Test
    public void removeUserFromChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        channelService.addUserToChannel(0, "user0");
        assertEquals("channel not found", channelController.removeUserFromChannel(1, "user0").getEntity());
        assertEquals("user not found", channelController.removeUserFromChannel(0, "user1").getEntity());
        assertEquals(200, channelController.removeUserFromChannel(0, "user0").getStatus());
        assertEquals(0, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getParticipants().size());
    }

    @Test
    public void promoteUserInChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        channelService.addUserToChannel(0, "user0");
        assertEquals("channel not found", channelController.promoteUserInChannel(1, "user0").getEntity());
        assertEquals("user not found", channelController.promoteUserInChannel(0, "user1").getEntity());
        assertEquals(200, channelController.promoteUserInChannel(0, "user0").getStatus());
        assertEquals(1, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getMods().size());
    }

    @Test
    public void demoteUserInChannel() throws SQLException{
        channelDB.addChannel(new Channel(0, "channel0"));
        userDB.addUser(new User("user0"));
        channelService.addUserToChannel(0, "user0");
        channelService.promoteUserInChannel(0, "user0");
        assertEquals("channel not found", channelController.demoteUserInChannel(1, "user0").getEntity());
        assertEquals("user not found", channelController.demoteUserInChannel(0, "user1").getEntity());
        assertEquals(200, channelController.demoteUserInChannel(0, "user0").getStatus());
        assertEquals(0, Objects.requireNonNull(channelService.getChannelById(0).orElse(null)).getMods().size());
    }

    @Test
    public void exceptions() throws SQLException{
        ChannelService channelServiceMock = Mockito.mock(ChannelService.class);
        Mockito.doThrow(SQLException.class).when(channelServiceMock).getAllChannels();
        Mockito.doThrow(SQLException.class).when(channelServiceMock).getChannelById(any(Integer.class));
        Mockito.doThrow(SQLException.class).when(channelServiceMock).removeChannel(any(Integer.class));
        Mockito.doThrow(SQLException.class).when(channelServiceMock).addChannel(any(ChannelDTO.class));
        Mockito.doThrow(SQLException.class).when(channelServiceMock).addUserToChannel(any(Integer.class), any(String.class));
        Mockito.doThrow(SQLException.class).when(channelServiceMock).removeUserFromChannel(any(Integer.class), any(String.class));
        Mockito.doThrow(SQLException.class).when(channelServiceMock).promoteUserInChannel(any(Integer.class), any(String.class));
        Mockito.doThrow(SQLException.class).when(channelServiceMock).demoteUserInChannel(any(Integer.class), any(String.class));
        channelController.setChannelService(channelServiceMock);

        assertEquals(500, channelController.getAllChannels().getStatus());
        assertEquals(500, channelController.getChannelById(0).getStatus());
        assertEquals(500, channelController.removeChannel(0).getStatus());
        assertEquals(500, channelController.addChannel(new ChannelDTO()).getStatus());
        assertEquals(500, channelController.addUserToChannel(0, "0").getStatus());
        assertEquals(500, channelController.removeUserFromChannel(0, "0").getStatus());
        assertEquals(500, channelController.promoteUserInChannel(0, "0").getStatus());
        assertEquals(500, channelController.demoteUserInChannel(0, "0").getStatus());
    }
}