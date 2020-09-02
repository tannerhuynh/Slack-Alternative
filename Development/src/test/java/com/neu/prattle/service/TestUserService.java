package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestUserService {

    private UserService as;
    private User mike = new User("Mike", "mike", "mike", "mike", "mike", "mike");

    @Before
    public void setUp() throws SQLException {
        as = UserService.getInstance();
        as.testMode();
    }

    // This method just tries to add
    @Test
    public void setUserTest() throws SQLException {
        as.addUser(mike);
        try {
            as.addUser(mike);
        } catch (UserAlreadyPresentException e) {
            assertEquals("User already present with name: Mike", e.getMessage());
        }
    }

    // This method just tries to add
    @Test
    public void getUserTest() throws SQLException {
        as.addUser(mike);
        Optional<User> user = as.findUserByName("Mike");
        assertTrue(user.isPresent());
        assertEquals(1, as.getAllUsers().size());
        Optional<User> user1 = as.findUserByName("No");
        assertFalse(user1.isPresent());
    }

    @Test
    public void removeUserByName() throws SQLException {
        as.addUser(mike);
        as.removeUserByName("Mike");
        as.removeUserByName("Mike");
        assertFalse(as.findUserByName("Mike").isPresent());
    }

    @Test
    public void testSetUserActive() throws SQLException {
        as.addUser(mike);
        as.setUserActive("mike", false);
        assertTrue(mike.getActive());
        as.setUserActive("Mike", false);
        Optional<User> optionalIUser = as.findUserByName("Mike");
        assertTrue(optionalIUser.isPresent());
        assertFalse(optionalIUser.get().getActive());
    }

    @Test
    public void testGetActive() throws SQLException {
        as.addUser(mike);
        as.addUser(new User("test", "test", "test", "first", "last", "bio", false));
        assertEquals(2, as.getAllUsers().size());
        assertEquals(1, as.getActiveUsers().size());
    }

    @Test
    public void checkLockout() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        assertTrue(as.checkLockout(timestamp));

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Timestamp timestamp1 = new Timestamp(calendar.getTime().getTime());
        assertFalse(as.checkLockout(timestamp1));
    }

    @Test
    public void updateLoginAttempts() throws SQLException, InterruptedException{
        User user = new User("test");
        as.addUser(user);
        assertEquals(0, user.getLoginAttempts());
        assertNull(user.getLockout());
        as.updateLoginAttempts(user);
        assertEquals(1, user.getLoginAttempts());
        assertNull(user.getLockout());
        as.updateLoginAttempts(user);
        assertEquals(2, user.getLoginAttempts());
        assertNull(user.getLockout());
        as.updateLoginAttempts(user);
        assertEquals(3, user.getLoginAttempts());
        assertNull(user.getLockout());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date());
        Timestamp timestamp1 = new Timestamp(calendar1.getTime().getTime());
        as.updateLoginAttempts(user);
        assertEquals(0, user.getLoginAttempts());
        assertNotNull(user.getLockout());
        assertTrue(user.getLockout().after(timestamp1) || user.getLockout().equals(timestamp1));
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        Timestamp timestamp2 = new Timestamp(calendar2.getTime().getTime());
        assertTrue(user.getLockout().before(timestamp2) || user.getLockout().equals(timestamp2));
    }

    @Test
    public void login() throws SQLException{
        User user = new User("test");
        user.setPassword("1234");
        as.addUser(user);

        User loginUser = new User("test1");
        assertEquals(0, as.login(loginUser));

        loginUser.setUsername("test");
        loginUser.setPassword("123");
        assertEquals(-2, as.login(loginUser));
        assertEquals(1, user.getLoginAttempts());

        loginUser.setPassword("1234");
        assertEquals(1, as.login(loginUser));
        assertEquals(0, user.getLoginAttempts());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        user.setLockout(timestamp);
        assertEquals(1, as.login(loginUser));

        calendar.setTime(new Date());
        timestamp = new Timestamp(calendar.getTime().getTime());
        user.setLockout(timestamp);
        assertEquals(-1, as.login(loginUser));
    }

    @After
    public void cleanUp() throws SQLException {
        as.removeAllUsers();
    }
}
