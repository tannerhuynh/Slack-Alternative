package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.neu.prattle.model.Message;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.IMessageService;

import com.neu.prattle.utils.GsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class TestDMController {
    private DMController dmController = new DMController();
    private IMessageService messageService = MessageService.getInstance();
    private Gson gson = GsonUtil.getGson();
    private Type msgListType = new TypeToken<ArrayList<Message>>() {
    }.getType();

    private MessageService messageServiceMock = Mockito.mock(MessageService.class);
    private DMController dmControllerException = new DMController();

    @Before
    public void setUp() throws SQLException {
        Mockito.doThrow(SQLException.class).when(messageServiceMock).getMessagesByFrom(any(String.class));
        Mockito.doThrow(SQLException.class).when(messageServiceMock).getMessagesByTo(any(String.class));
        Mockito.doThrow(SQLException.class).when(messageServiceMock).getMessagesByUsers(any(String.class), any(String.class));

        dmControllerException.setMessageService(messageServiceMock);
    }

    private void populateMsg() throws SQLException {
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToUsername("u1").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToUsername("u0").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:01.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToUsername("u2").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:02.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToUsername("u0").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:03.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToUsername("u2").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:04.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToUsername("u1").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:05.0")).build());
    }

    @Test
    public void testGetDMFrom() throws SQLException {
        messageService.testMode();
        populateMsg();
        Response response = dmController.getDMFrom("u0");
        assertEquals(200, response.getStatus());
        List<Message> messageList = gson.fromJson((String) response.getEntity(), msgListType);
        assertEquals(2, messageList.size());
        assertEquals("u0", messageList.get(0).getFromUsername());
        assertEquals("u1", messageList.get(0).getToUsername());
        assertEquals("2000-01-01 00:00:00.0", messageList.get(0).getTimestamp().toString());
        assertEquals("u0", messageList.get(1).getFromUsername());
        assertEquals("u2", messageList.get(1).getToUsername());
        assertEquals("2000-01-01 00:00:02.0", messageList.get(1).getTimestamp().toString());

        response = dmControllerException.getDMFrom("u0");
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetDMTo() throws SQLException {
        messageService.testMode();
        populateMsg();
        Response response = dmController.getDMTo("u0");
        assertEquals(200, response.getStatus());
        List<Message> messageList = gson.fromJson((String) response.getEntity(), msgListType);
        assertEquals(2, messageList.size());
        assertEquals("u1", messageList.get(0).getFromUsername());
        assertEquals("u0", messageList.get(0).getToUsername());
        assertEquals("2000-01-01 00:00:01.0", messageList.get(0).getTimestamp().toString());
        assertEquals("u2", messageList.get(1).getFromUsername());
        assertEquals("u0", messageList.get(1).getToUsername());
        assertEquals("2000-01-01 00:00:03.0", messageList.get(1).getTimestamp().toString());

        response = dmControllerException.getDMTo("u0");
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetDMBetween() throws SQLException {
        messageService.testMode();
        populateMsg();
        Response response = dmController.getDMBetween("u0", "u1");
        assertEquals(200, response.getStatus());
        List<Message> messageList = gson.fromJson((String) response.getEntity(), msgListType);
        assertEquals(2, messageList.size());
        assertEquals("u0", messageList.get(0).getFromUsername());
        assertEquals("u1", messageList.get(0).getToUsername());
        assertEquals("2000-01-01 00:00:00.0", messageList.get(0).getTimestamp().toString());
        assertEquals("u1", messageList.get(1).getFromUsername());
        assertEquals("u0", messageList.get(1).getToUsername());
        assertEquals("2000-01-01 00:00:01.0", messageList.get(1).getTimestamp().toString());

        response = dmControllerException.getDMBetween("u0", "u1");
        assertEquals(500, response.getStatus());
    }
}
