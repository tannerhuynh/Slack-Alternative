package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.neu.prattle.model.Message;
import com.neu.prattle.service.IMessageService;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.utils.GsonUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class TestCMController {
    private CMController cmController = new CMController();
    private IMessageService messageService = MessageService.getInstance();
    private Gson gson = GsonUtil.getGson();
    private Type msgListType = new TypeToken<ArrayList<Message>>() {
    }.getType();

    private MessageService messageServiceMock = Mockito.mock(MessageService.class);
    private CMController cmControllerException = new CMController();

    @Before
    public void setUp() throws SQLException {
        Mockito.doThrow(SQLException.class).when(messageServiceMock).getMessagesWithinChannel(any(Integer.class));


        cmControllerException.setMessageService(messageServiceMock);
    }

    private void populateMsg() throws SQLException {
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToChannelId(1).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToChannelId(2).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:01.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToChannelId(2).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:02.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToChannelId(4).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:03.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToChannelId(4).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:04.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToChannelId(4).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:05.0")).build());
    }

    @Test
    public void testGetCM() throws SQLException {
        messageService.testMode();
        populateMsg();
        Response response = cmController.getAllMessages("1");
        assertEquals(200, response.getStatus());
        List<Message> messageList = gson.fromJson((String) response.getEntity(), msgListType);
        assertEquals(1, messageList.size());
        assertEquals("u0", messageList.get(0).getFromUsername());
        assertEquals("2000-01-01 00:00:00.0", messageList.get(0).getTimestamp().toString());


        response = cmControllerException.getAllMessages("1");
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetCM2() throws SQLException {
        messageService.testMode();
        populateMsg();
        Response response = cmController.getAllMessages("2");
        assertEquals(200, response.getStatus());
        List<Message> messageList = gson.fromJson((String) response.getEntity(), msgListType);
        assertEquals(2, messageList.size());
        assertEquals("u1", messageList.get(0).getFromUsername());
        assertEquals("2000-01-01 00:00:01.0", messageList.get(0).getTimestamp().toString());


        response = cmControllerException.getAllMessages("2");
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetCMEmpty() throws SQLException {
        messageService.testMode();
        populateMsg();
        Response response = cmController.getAllMessages("3");
        assertEquals(200, response.getStatus());
        List<Message> messageList = gson.fromJson((String) response.getEntity(), msgListType);
        assertEquals(0, messageList.size());

        response = cmControllerException.getAllMessages("3");
        assertEquals(500, response.getStatus());
    }


}
