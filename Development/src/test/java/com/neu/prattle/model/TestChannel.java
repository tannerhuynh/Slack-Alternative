package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests for channel class.
 */
public class TestChannel {
    private String pw;
    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String bio;
    private Set<User> users = new HashSet<>();
    private Set<User> mods = new HashSet<>();

    @Before
    public void setUp() {
        this.username = "johnhanle";
        this.pw = "passWORD321";
        this.email = "coo@gmail.com";
        this.firstname = "John";
        this.lastname = "Hanle";
        this.bio = "Vestibulum id ligula porta felis euismod semper.";
        User john = new User(username, pw, email, firstname, lastname, bio, true);
        users.add(john);
        mods.add(john);
        this.username = "metierqwjfdjb";
        this.pw = "passWORwefewfD321";
        this.email = "wefewf@gmail.com";
        this.firstname = "Meredith";
        this.lastname = "Harrison";
        this.bio = "Nullam id dolor id nibh ultricies vehicula ut id elit. Donec id elit non mi " +
                "porta gravida at eget metus. Sed posuere consectetur est at lobortis.";
        User meri = new User(username, pw, email, firstname, lastname, bio, true);
        users.add(meri);
        this.username = "Bobbarker";
        this.pw = "wqqweqwdewf";
        this.email = "qwdqwfqwd@gmail.com";
        this.firstname = "Bob";
        this.lastname = "Barker";
        this.bio = "Venenatis Aenean Adipiscing.";
        User bob = new User(username, pw, email, firstname, lastname, bio, true);
        users.add(bob);
    }

    @Test
    public void addMod() {
        Channel channel = new Channel(214387214, mods, users);
        User bob = new User(username, pw, email, firstname, lastname, bio, true);
        channel.addMod(bob);
        assertEquals(2, channel.getMods().size());
        assertTrue("Bobbarker", channel.getMods().contains(bob));
    }

    @Test
    public void getMods() {
        Channel channel = new Channel(214387214, mods, users);
        assertEquals(1, channel.getMods().size());
    }

    @Test
    public void setMods() {
        Channel channel = new Channel();
        channel.setMods(mods);
        assertEquals(mods, channel.getMods());
    }

    @Test
    public void addParticipant() {
        Channel channel = new Channel(214387214, mods, users);
        User aaron = new User("Aarondasd", pw, email, firstname, lastname, bio, true);
        channel.addParticipant(aaron);
        assertTrue(channel.getParticipants().contains(aaron));
    }

    @Test
    public void getParticipants() {
        Channel channel = new Channel(214387214, mods, users);
        assertEquals(3, channel.getParticipants().size());
    }

    @Test
    public void setParticipants(){
        Channel channel = new Channel();
        channel.setParticipants(users);
        assertEquals(users, channel.getParticipants());
    }

    @Test
    public void getChannelID() {
        Channel channel = new Channel(214387214, mods, users);
        assertEquals(214387214, channel.getId());
    }

    @Test
    public void channelName(){
        Channel channel = new Channel(214387214, "channel", mods, users);
        assertEquals("channel", channel.getName());
        channel.setName("channel1");
        assertEquals("channel1", channel.getName());
    }

    @Test
    public void testEquals(){
        Channel channel1 = new Channel();
        channel1.setId(1);
        Channel channel2 = new Channel();
        channel2.setId(2);
        Channel channel3= new Channel();
        channel3.setId(1);
        assertEquals(channel1, channel3);
        assertNotEquals(channel1, channel2);
        assertNotEquals(channel1, new Object());
        assertEquals(1, channel1.hashCode());
    }
}