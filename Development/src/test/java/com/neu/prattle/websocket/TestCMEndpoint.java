package com.neu.prattle.websocket;

import com.neu.prattle.model.ChannelDTO;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.ChannelService;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestCMEndpoint {
    private Session mockSession1;
    private Session mockSession2;
    private RemoteEndpoint.Basic mockBasic1;
    private RemoteEndpoint.Basic mockBasic2;
    private ArgumentCaptor<Message> msg;

    @Before
    public void setUp() throws SQLException{
        UserService.getInstance().testMode();
        ChannelService.getInstance().testMode();
        MessageService.getInstance().testMode();

        mockSession1 = Mockito.mock(Session.class);
        mockSession2 = Mockito.mock(Session.class);
        mockBasic1 = Mockito.mock(RemoteEndpoint.Basic.class);
        mockBasic2 = Mockito.mock(RemoteEndpoint.Basic.class);
        msg = ArgumentCaptor.forClass(Message.class);

        Mockito.doReturn(mockBasic1).when(mockSession1).getBasicRemote();
        Mockito.doReturn(mockBasic2).when(mockSession2).getBasicRemote();

        CMEndpoint.setChatSubjectHashMap(new HashMap<>());
    }

    @Test
    public void onOpen() throws Exception {
        UserService.getInstance().addUser(new User("user1", "123", "abc1", "first", "last", "bio"));
        UserService.getInstance().addUser(new User("user2", "123", "abc2", "first", "last", "bio"));
        ChannelService.getInstance().addChannel(new ChannelDTO(0, "c1"));
        ChannelService.getInstance().addUserToChannel(0, "user1");
        ChannelService.getInstance().addUserToChannel(0, "user2");

        CMEndpoint cmEndpoint1 = new CMEndpoint();
        cmEndpoint1.onOpen(mockSession1, 0, "user1");

        CMEndpoint cmEndpoint2 = new CMEndpoint();
        cmEndpoint2.onOpen(mockSession2, 0, "user2");

        assertEquals(cmEndpoint1.getChatSubject(), cmEndpoint2.getChatSubject());

        cmEndpoint1.onOpen(mockSession1, 1, "user1");
        verify(mockBasic1, times(1)).sendObject(msg.capture());
        assertEquals("Channel 1 could not be found", msg.getValue().getContent());

        cmEndpoint1.onOpen(mockSession1, 0, "user3");
        verify(mockBasic1, times(2)).sendObject(msg.capture());
        assertEquals("User user3 could not be found", msg.getValue().getContent());

        UserService userServiceMock = Mockito.mock(UserService.class);
        Mockito.doThrow(SQLException.class).when(userServiceMock).findUserByName(any(String.class));
        cmEndpoint1.setUserService(userServiceMock);
        cmEndpoint1.onOpen(mockSession1, 1, "user1");
        verify(mockBasic1, times(3)).sendObject(msg.capture());
    }

    @Test
    public void onMessage() throws Exception {
        UserService.getInstance().addUser(new User("user1", "123", "abc1", "first", "last", "bio"));
        UserService.getInstance().addUser(new User("user2", "123", "abc2", "first", "last", "bio"));
        ChannelService.getInstance().addChannel(new ChannelDTO(0, "c1"));
        ChannelService.getInstance().addUserToChannel(0, "user1");
        ChannelService.getInstance().addUserToChannel(0, "user2");

        CMEndpoint cmEndpoint1 = new CMEndpoint();
        cmEndpoint1.onOpen(mockSession1, 0, "user1");

        CMEndpoint cmEndpoint2 = new CMEndpoint();
        cmEndpoint2.onOpen(mockSession2, 0, "user2");

        verify(mockBasic1, times(0)).sendObject(msg.capture());
        verify(mockBasic2, times(0)).sendObject(msg.capture());

        cmEndpoint1.onMessage(mockSession1, Message.messageBuilder().setMessageContent("test").build());

        verify(mockBasic1, times(1)).sendObject(msg.capture());
        verify(mockBasic2, times(1)).sendObject(msg.capture());

        MessageService messageServiceMock = Mockito.mock(MessageService.class);
        Mockito.doThrow(SQLException.class).when(messageServiceMock).addMessage(any(Message.class));
        cmEndpoint1.setMessageService(messageServiceMock);
        cmEndpoint1.onMessage(mockSession1, Message.messageBuilder().setMessageContent("test").build());
    }

    @Test
    public void onClose() throws Exception{
        UserService.getInstance().addUser(new User("user1", "123", "abc1", "first", "last", "bio"));
        UserService.getInstance().addUser(new User("user2", "123", "abc2", "first", "last", "bio"));
        ChannelService.getInstance().addChannel(new ChannelDTO(0, "c1"));
        ChannelService.getInstance().addUserToChannel(0, "user1");
        ChannelService.getInstance().addUserToChannel(0, "user2");

        CMEndpoint cmEndpoint1 = new CMEndpoint();
        cmEndpoint1.onOpen(mockSession1, 0, "user1");

        CMEndpoint cmEndpoint2 = new CMEndpoint();
        cmEndpoint2.onOpen(mockSession2, 0, "user2");

        cmEndpoint1.onClose(mockSession1);
        assertEquals(1, cmEndpoint2.getChatSubject().getObservers().size());
    }
}
