package com.neu.prattle.database;

import com.neu.prattle.model.User;

import org.junit.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestUserDBInMemory {
    private IUserDB userDB = UserDBInMemory.getInstance();

    @Test
    public void testUpdateUser() throws SQLException {
        userDB.testMode();
        assertEquals(0, userDB.getAllUsers().size());
        User user = new User("test", "test", "email", "first", "last", "bio");
        userDB.addUser(user);
        assertEquals(1, userDB.getAllUsers().size());
        User user1 = new User("test", "test", "email", "first", "last", "bio", false);
        userDB.updateUser(user1);
        assertEquals(1, userDB.getAllUsers().size());
        Optional<User> optionalIUser = userDB.findUserByName("test");
        assertTrue(optionalIUser.isPresent());
        assertFalse(optionalIUser.get().getActive());

        User user2 = new User("no", "test", "email", "first", "last", "bio", false);
        userDB.updateUser(user2);
        assertEquals(1, userDB.getAllUsers().size());
        optionalIUser = userDB.findUserByName("test");
        assertTrue(optionalIUser.isPresent());
        assertFalse(optionalIUser.get().getActive());
        userDB.removeUser(user1);
    }

}
