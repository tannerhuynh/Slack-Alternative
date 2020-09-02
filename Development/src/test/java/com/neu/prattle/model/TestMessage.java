package com.neu.prattle.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;

public class TestMessage {
    @Test
    public void testMessage() {
        Message message = Message.messageBuilder().setMessageContent("test").setFrom("u0").setToUsername("u1").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0")).build();
        assertEquals("test", message.getContent());
        assertEquals("u0", message.getFromUsername());
        assertEquals("u1", message.getToUsername());
        assertEquals("From: u0To: u1Content: testDatetime: 2000-01-01 00:00:00.0", message.toString());

        message.setId(1);
        assertEquals(1, message.getId());
    }

    @Test
    public void fromToUser(){
        Message message = Message.messageBuilder().setMessageContent("test").setFrom("u0").setToUsername("u1").setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0")).build();
        User from = new User("from");
        User to = new User("to");
        message.setFromUser(from);
        assertEquals(from, message.getFromUser());
        message.setToUser(to);
        assertEquals(to, message.getToUser());
    }

    @Test
    public void testToChannel() {
        Message message = Message.messageBuilder().setMessageContent("test").setFrom("u0").build();
        message.setToChannel(new Channel(1, "1"));
        assertEquals(1, message.getToChannel().getId());
    }

    @Test
    public void testAvatars() {
        Message message = Message.messageBuilder().setMessageContent("test").setFrom("u0").build();
        message.setFromAvatar(1);
        message.setToAvatar(2);
        assertEquals(new Integer(1), message.getFromAvatar());
        assertEquals(new Integer(2), message.getToAvatar());
    }
}
