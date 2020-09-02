package com.neu.prattle.database;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestUserDBJson {
    private IUserDB userDB;
    private List<User> testUserList;

    private void removeAllTestUsers() throws SQLException {
        for (User user : testUserList) {
            userDB.removeUser(user);
        }
    }

    @Before
    public void setUp() throws SQLException {
        userDB = UserDBJson.getInstance();
        userDB.testMode();
        testUserList = new ArrayList<>();
        testUserList.add(new User("Test0"));
        testUserList.add(new User("Test1"));
        testUserList.add(new User("Test2"));
    }

    @Test
    public void setUserTest() throws SQLException {
        userDB.addUser(testUserList.get(0));
        try {
            userDB.addUser(testUserList.get(0));
        } catch (UserAlreadyPresentException e) {
            assertEquals(String.format("User already present with name: %s", testUserList.get(0).getUsername()),
                    e.getMessage());
        }
    }

    @Test
    public void findUserByNameTest() throws SQLException {
        userDB.addUser(testUserList.get(0));
        User user = userDB.findUserByName(testUserList.get(0).getUsername()).orElse(new User("null"));
        assertEquals(testUserList.get(0).getUsername(), user.getUsername());
        Optional<User> optionalIUser = userDB.findUserByName(testUserList.get(1).getUsername());
        assertTrue(!optionalIUser.isPresent());
    }

    @Test
    public void removeAllUsers() throws SQLException {
        userDB.addUser(testUserList.get(0));
        userDB.addUser(testUserList.get(1));
        userDB.removeAllUsers();
        assertFalse(userDB.findUserByName(testUserList.get(0).getUsername()).isPresent());
        assertFalse(userDB.findUserByName(testUserList.get(1).getUsername()).isPresent());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = new User("test", "test", "email", "first", "last", "bio");
        userDB.addUser(user);
        User user1 = new User("test", "test", "email", "first", "last", "bio", false);
        userDB.updateUser(user1);
        Optional<User> optionalIUser = userDB.findUserByName("test");
        assertTrue(optionalIUser.isPresent());
        assertFalse(optionalIUser.get().getActive());

        user1.setUsername("no");
        userDB.updateUser(user1);
        optionalIUser = userDB.findUserByName("test");
        assertTrue(optionalIUser.isPresent());
        assertFalse(optionalIUser.get().getActive());
        userDB.removeUser(user1);
    }

    @After
    public void cleanUp() throws SQLException {
        userDB.removeAllUsers();
    }
}
