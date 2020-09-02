package com.neu.prattle.database;

import com.neu.prattle.model.Message;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class TestMessageDBInMemory {

    private IMessageDB messageDB = MessageDBInMemory.getInstance();
    private List<Message> messageSet = new ArrayList<>();

    @Before
    public void setUp() throws SQLException {

        messageDB.testMode();
        messageSet.add(Message.messageBuilder().setFrom("dian").setToUsername("alice").setMessageContent("123").build());
        messageDB.addMessage(messageSet.get(0));

    }

    @Test
    public void testGetBetweenUsers() throws SQLException {


        assertEquals(0, messageDB.getMessagesBetweenUsers("david", "tanner").size());
        assertEquals(1, messageDB.getMessagesBetweenUsers("dian", "alice").size());
        assertEquals(0, messageDB.getMessagesBetweenUsers("dian", "tanner").size());

        // commutable
        assertEquals(0, messageDB.getMessagesBetweenUsers("tanner", "david").size());
        assertEquals(1, messageDB.getMessagesBetweenUsers("alice", "dian").size());
        assertEquals(0, messageDB.getMessagesBetweenUsers("tanner", "dian").size());
    }

    @Test
    public void testGetFromUsers() throws SQLException {

        assertEquals(0, messageDB.getMessagesByFrom("david").size());
        assertEquals(0, messageDB.getMessagesByFrom("tanner").size());
        assertEquals(1, messageDB.getMessagesByFrom("dian").size());
        assertEquals(0, messageDB.getMessagesByFrom("alice").size());


    }

    @Test
    public void testGetToUsers() throws SQLException {
        assertEquals(0, messageDB.getMessagesByToUser("david").size());
        assertEquals(0, messageDB.getMessagesByToUser("tanner").size());
        assertEquals(0, messageDB.getMessagesByToUser("dian").size());
        assertEquals(1, messageDB.getMessagesByToUser("alice").size());
    }

    @Test
    public void testGetToChannel() throws SQLException {
        messageDB.addMessage(Message.messageBuilder().setFrom("dian").setToChannelId(1).setMessageContent("123").build());
        assertEquals(1, messageDB.getMessagesByToChannel(1).size());
    }


    @Test
    public void testAddMessage() throws SQLException {

        messageSet.add(Message.messageBuilder().setFrom("david").setToUsername("tanner").setMessageContent("123").build());
        messageDB.addMessage(messageSet.get(1));

        assertEquals(1, messageDB.getMessagesBetweenUsers("david", "tanner").size());
        assertEquals(1, messageDB.getMessagesBetweenUsers("dian", "alice").size());
        assertEquals(0, messageDB.getMessagesBetweenUsers("dian", "tanner").size());

        assertEquals(1, messageDB.getMessagesByFrom("david").size());
        assertEquals(0, messageDB.getMessagesByFrom("tanner").size());
        assertEquals(1, messageDB.getMessagesByFrom("dian").size());
        assertEquals(0, messageDB.getMessagesByFrom("alice").size());

        assertEquals(0, messageDB.getMessagesByToUser("david").size());
        assertEquals(1, messageDB.getMessagesByToUser("tanner").size());
        assertEquals(0, messageDB.getMessagesByToUser("dian").size());
        assertEquals(1, messageDB.getMessagesByToUser("alice").size());

    }

    @Test
    public void testAddAndRemoveMessage() throws SQLException {

        messageSet.add(Message.messageBuilder().setFrom("david").setToUsername("tanner").setMessageContent("123").build());
        messageDB.addMessage(messageSet.get(1));
        messageDB.removeMessage(messageSet.get(0));

        assertEquals(1, messageDB.getMessagesBetweenUsers("david", "tanner").size());
        assertEquals(0, messageDB.getMessagesBetweenUsers("dian", "alice").size());
        assertEquals(0, messageDB.getMessagesBetweenUsers("dian", "tanner").size());

        assertEquals(1, messageDB.getMessagesByFrom("david").size());
        assertEquals(0, messageDB.getMessagesByFrom("tanner").size());
        assertEquals(0, messageDB.getMessagesByFrom("dian").size());
        assertEquals(0, messageDB.getMessagesByFrom("alice").size());

        assertEquals(0, messageDB.getMessagesByToUser("david").size());
        assertEquals(1, messageDB.getMessagesByToUser("tanner").size());
        assertEquals(0, messageDB.getMessagesByToUser("dian").size());
        assertEquals(0, messageDB.getMessagesByToUser("alice").size());

    }

    @Test
    public void testUpdateMessage() throws SQLException {

        messageSet.add(Message.messageBuilder().setFrom("david").setToUsername("tanner").setMessageContent("123").build());
        messageDB.addMessage(messageSet.get(1));
        messageDB.updateMessage(messageSet.get(0));
        messageDB.updateMessage(messageSet.get(1));


        assertEquals(1, messageDB.getMessagesBetweenUsers("david", "tanner").size());
        assertEquals(1, messageDB.getMessagesBetweenUsers("dian", "alice").size());
        assertEquals(0, messageDB.getMessagesBetweenUsers("dian", "tanner").size());

        assertEquals(1, messageDB.getMessagesByFrom("david").size());
        assertEquals(0, messageDB.getMessagesByFrom("tanner").size());
        assertEquals(1, messageDB.getMessagesByFrom("dian").size());
        assertEquals(0, messageDB.getMessagesByFrom("alice").size());

        assertEquals(0, messageDB.getMessagesByToUser("david").size());
        assertEquals(1, messageDB.getMessagesByToUser("tanner").size());
        assertEquals(0, messageDB.getMessagesByToUser("dian").size());
        assertEquals(1, messageDB.getMessagesByToUser("alice").size());

    }

}
