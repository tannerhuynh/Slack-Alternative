package com.neu.prattle.service;

import com.neu.prattle.database.MessageDBInMemory;
import com.neu.prattle.model.Message;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestMessageService {
    private MessageDBInMemory messageDB = MessageDBInMemory.getInstance();
    IMessageService messageService = MessageService.getInstance();

    @Before
    public void setUp() throws Exception {
        messageDB.testMode();
        messageService.testMode();
    }

    @Test
    public void testMessageService() throws SQLException {
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToUsername("u1").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToUsername("u0").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:01.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToUsername("u2").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:02.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToUsername("u0").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:03.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToUsername("u2").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:04.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToUsername("u1").setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:05.0")).build());

        List<Message> messages = messageService.getMessagesByUsers("u0", "u1");
        assertEquals(2, messages.size());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:00.0"), messages.get(0).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:01.0"), messages.get(1).getTimestamp());

        messages = messageService.getMessagesByFrom("u0");
        assertEquals(2, messages.size());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:00.0"), messages.get(0).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:02.0"), messages.get(1).getTimestamp());

        messages = messageService.getMessagesByTo("u0");
        assertEquals(2, messages.size());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:01.0"), messages.get(0).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:03.0"), messages.get(1).getTimestamp());
    }

    @Test
    public void testChannelMessageService() throws SQLException {
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToChannelId(101).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToChannelId(101).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:01.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u0").setToChannelId(101).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:02.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToChannelId(301).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:03.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u1").setToChannelId(301).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:04.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u2").setToChannelId(301).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:05.0")).build());
        messageService.addMessage(Message.messageBuilder().setFrom("u3").setToChannelId(301).setMessageContent("c").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:06.0")).build());


        List<Message> messages = messageService.getMessagesWithinChannel(101);
        assertEquals(3, messages.size());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:00.0"), messages.get(0).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:01.0"), messages.get(1).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:02.0"), messages.get(2).getTimestamp());


        messages = messageService.getMessagesWithinChannel(301);
        assertEquals(4, messages.size());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:03.0"), messages.get(0).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:04.0"), messages.get(1).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:05.0"), messages.get(2).getTimestamp());
        assertEquals(Timestamp.valueOf("2000-01-01 00:00:06.0"), messages.get(3).getTimestamp());

    }
}
