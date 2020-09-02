package com.neu.prattle.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestUser {
    private String pw;
    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String bio;


    @Before
    public void setUp() {
        this.username = "johnhanle";
        this.pw = "passWORD321";
        this.email = "coo@gmail.com";
        this.username = "MasterClown";
        this.firstname = "John";
        this.lastname = "Hanle";
        this.bio = "Vestibulum id ligula porta felis euismod semper.";
    }

    @Test
    public void testJson() throws IOException {
        String JSON = "{ \"username\" : \"dian\", \"password\": \"1234\", \"email\": \"1234\", \"firstName\": \"dian\", \"lastName\": \"wang\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        User p = objectMapper.readValue(JSON, User.class);
        assertEquals("dian", p.getUsername());
    }

    @Test
    public void setUsernameGoodSimple() {
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        assertEquals(username, myUser.getUsername());
    }

    @Test
    public void testHashCodeEq() {
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        User myUser2 = new User(username, "fweiuhefwef", email, firstname, lastname, bio);
        assertEquals(myUser.hashCode(), myUser2.hashCode());
        assertEquals(myUser2.hashCode(), myUser.hashCode());
    }

    @Test
    public void testHashCodeNotEq() {
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        User myUser2 = new User("duhenwfek", pw, email, firstname, lastname, bio);
        assertNotEquals(myUser.hashCode(), myUser2.hashCode());
        assertNotEquals(myUser2.hashCode(), myUser.hashCode());
    }

    @Test
    public void testEqual() {
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        User myUser2 = new User(username, "fwbeiuf3", email, firstname, lastname, bio);
        assertEquals(myUser, myUser2);
        assertEquals(myUser2, myUser);
    }


    @Test
    public void testNotEqual() {
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        User myUser2 = new User("fwebfkjef", pw, email, firstname, lastname, bio);
        assertNotEquals(myUser, myUser2);
        assertNotEquals(myUser2, myUser);
    }

    @Test
    public void testFirstName() {
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        myUser.setFirstName("Adam");
        assertEquals("Adam", myUser.getFirstName());
    }

    @Test
    public void testLastName() {
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        myUser.setLastName("Hopkins");
        assertEquals("Hopkins", myUser.getLastName());
    }

    @Test
    public void testNotEqualWrongObject() {
        Message message = new Message();
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        assertNotEquals(myUser, message);
    }


    @Test
    public void testBio() {
        String bio = "Michael Weintraub came to Khoury after more than a twenty year career at Verizon/GTE " +
                "Laboratories as both an executive and technologist. Most recently, he worked on data center and cloud " +
                "computing architecture, design, and development.";
        User myUser = new User(username, pw, email, firstname, lastname, bio);
        myUser.setBio(bio);
        assertEquals(bio, myUser.getBio());
    }

    @Test
    public void testMod() {
        User user = new User("test");
        user.setIsMod(true);
        assertTrue(user.getIsMod());
    }

    @Test
    public void testChannelSet() {
        User user = new User("test");
        Set<Channel> channels = new HashSet<>();
        channels.add(new Channel(0, "1"));
        user.setChannelSet(channels);
        assertEquals(channels, user.getChannelSet());
    }

    @Test
    public void testModChannelSet() {
        User user = new User("test");
        Set<Channel> channels = new HashSet<>();
        channels.add(new Channel(0, "1"));
        user.setModChannelSet(channels);
        assertEquals(channels, user.getModChannelSet());
    }

    @Test
    public void testAvatar(){
        User user = new User("test");
        user.setAvatar(1);
        assertEquals(new Integer(1), user.getAvatar());
    }
}
