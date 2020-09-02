package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.UserService;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * This class tests the chatendpoint class, which creates chat sessions, maintains them, and destroys them.
 */
public class TestChatEndpoint {

    /** Verifies the users connect successfully, and messages are sent successfully.
     * @throws IOException  If any exception happens with the chatendpoint class
     * @throws EncodeException  If the JSON string/Java Message class fields don't match
     * @throws SQLException If the user couldn't be found
     */
    @Test
    public void testChatEndpoint() throws IOException, EncodeException, SQLException {

        // Initialization of a mock session
        Session mockSession = Mockito.mock(Session.class);
        Basic mockBasic = Mockito.mock(Basic.class);
        Mockito.doReturn(mockBasic).when(mockSession).getBasicRemote();
        ArgumentCaptor<Message> msg = ArgumentCaptor.forClass(Message.class);

        // Uses testmode for the mock session so as to not interfere with the real database
        UserService.getInstance().testMode();
        MessageService.getInstance().testMode();

        // Actual testing - add a user to the mock database, and verify the person connects
        ChatEndpoint chatEndpoint = new ChatEndpoint();
        UserService.getInstance().addUser(new User("test", "123", "abc", "first", "last", "bio"));
        chatEndpoint.onOpen(mockSession, "test");
        verify(mockBasic, times(1)).sendObject(msg.capture());
        assertEquals("Connected!", msg.getValue().getContent());

        chatEndpoint.onOpen(mockSession, "no user");
        verify(mockBasic, times(2)).sendObject(msg.capture());
        assertEquals("User no user could not be found", msg.getValue().getContent());

        chatEndpoint.onMessage(mockSession, Message.messageBuilder().setMessageContent("test").setToUsername("").build());
        verify(mockBasic, times(3)).sendObject(msg.capture());
        assertEquals("test", msg.getValue().getContent());

        chatEndpoint.onClose(mockSession);
        chatEndpoint.onError(mockSession, new IOException());

    }

    /** Tests direct message sending functionality by creating three users, and having one user send another a
     * message, and ensuring the third user does not receive it.
     * @throws Exception if there are problems with chatEndpoint
     */
    @Test
    public void testBroadcastBetweenUsers() throws Exception {
        UserService.getInstance().testMode();
        MessageService.getInstance().testMode();
        UserService.getInstance().addUser(new User("u1"));
        UserService.getInstance().addUser(new User("u2"));
        UserService.getInstance().addUser(new User("u3"));

        Session mockSession1 = Mockito.mock(Session.class);
        Session mockSession2 = Mockito.mock(Session.class);
        Session mockSession3 = Mockito.mock(Session.class);
        Basic mockBasic1 = Mockito.mock(Basic.class);
        Basic mockBasic2 = Mockito.mock(Basic.class);
        Basic mockBasic3 = Mockito.mock(Basic.class);

        Mockito.doReturn(mockBasic1).when(mockSession1).getBasicRemote();
        Mockito.doReturn(mockBasic2).when(mockSession2).getBasicRemote();
        Mockito.doReturn(mockBasic3).when(mockSession3).getBasicRemote();

        Mockito.doReturn("1").when(mockSession1).getId();
        Mockito.doReturn("2").when(mockSession2).getId();
        Mockito.doReturn("3").when(mockSession3).getId();

        ArgumentCaptor<Message> msg = ArgumentCaptor.forClass(Message.class);
        ChatEndpoint chatEndpoint1 = new ChatEndpoint();
        ChatEndpoint chatEndpoint2 = new ChatEndpoint();
        ChatEndpoint chatEndpoint3 = new ChatEndpoint();
        chatEndpoint1.onOpen(mockSession1, "u1");
        chatEndpoint2.onOpen(mockSession2, "u2");
        chatEndpoint3.onOpen(mockSession3, "u3");

        reset(mockBasic1);
        reset(mockBasic2);
        reset(mockBasic3);
        chatEndpoint1.onMessage(mockSession1, Message.messageBuilder().setMessageContent("test").setToUsername("u2").build());
        verify(mockBasic1, times(1)).sendObject(msg.capture());
        verify(mockBasic2, times(1)).sendObject(msg.capture());
        verify(mockBasic3, never()).sendObject(msg.capture());

        reset(mockBasic1);
        reset(mockBasic2);
        reset(mockBasic3);
        chatEndpoint1.onMessage(mockSession1, Message.messageBuilder().setMessageContent("test").setToUsername("no").build());
        verify(mockBasic1, never()).sendObject(msg.capture());
        verify(mockBasic2, never()).sendObject(msg.capture());
        verify(mockBasic3, never()).sendObject(msg.capture());

        Mockito.doThrow(IOException.class).when(mockSession1).getBasicRemote();
        try {
            chatEndpoint1.onOpen(mockSession1, "test");
        } catch (Exception e) {
            assertEquals(IOException.class, e.getClass());
        }

        Mockito.doThrow(IOException.class).when(mockSession2).getBasicRemote();
        try {
            chatEndpoint1.onMessage(mockSession1, Message.messageBuilder().setMessageContent("test").setToUsername("u2").build());
        } catch (Exception e) {
            assertEquals(IOException.class, e.getClass());

        }

        Mockito.doReturn(mockBasic1).when(mockSession1).getBasicRemote();
        try {
            chatEndpoint1.onMessage(mockSession1, Message.messageBuilder().setMessageContent("test").setToUsername("").build());
        } catch (Exception e) {
            assertEquals(IOException.class, e.getClass());
        }
    }

//    @Test
//    public void testException() throws Exception{
//        Session mockSession = Mockito.mock(Session.class);
//        Basic mockBasic = Mockito.mock(Basic.class);
//
////        Mockito.doReturn(mockBasic).when(mockSession).getBasicRemote();
//        Mockito.doThrow(SQLException.class).when(mockSession).getBasicRemote();
//
//        ArgumentCaptor<Message> msg = ArgumentCaptor.forClass(Message.class);
//        UserService.getInstance().testMode();
//        ChatEndpoint chatEndpoint = new ChatEndpoint();
////        UserService.getInstance().addUser(new User("test", "123", "abc", "first", "last", "bio"));
//
//        try {
//            chatEndpoint.onOpen(mockSession, "test");
//        }
//        catch (Exception e){
//            assertEquals(SQLException.class, e.getClass());
//        }
//
//
//    }
}
