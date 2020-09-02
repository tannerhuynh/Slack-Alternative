package com.neu.prattle.database;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class TestUserDB {
    private UserDB userDB;
    private List<User> testUserList;

    @Before
    public void setUp() throws SQLException {
        userDB = UserDB.getInstance();
        userDB.testMode();
        testUserList = new ArrayList<>();
        testUserList.add(new User("Test0", "123", "abc0", "first", "last", "bio"));
        testUserList.add(new User("Test1", "123", "abc1", "first", "last", "bio"));
        testUserList.add(new User("Test2", "123", "abc2", "first", "last", "bio"));
    }

    @Test
    public void testUserDB() throws SQLException {
        Connection mockConnection = Mockito.mock(Connection.class);
        userDB.setConnect(mockConnection);
        Statement mockStatement = Mockito.mock(Statement.class);
        PreparedStatement mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        Mockito.doReturn(mockStatement).when(mockConnection).createStatement();
        Mockito.doReturn(mockPreparedStatement).when(mockConnection).prepareStatement(any(String.class));
        ResultSet mockResultSet = Mockito.mock(ResultSet.class);
        Mockito.doReturn(mockResultSet).when(mockStatement).executeQuery(any(String.class));

        userDB.addUser(testUserList.get(0));
        userDB.updateUser(testUserList.get(0));

        Mockito.when(mockResultSet.getString("username")).thenReturn("Test0");
        Mockito.when(mockResultSet.getString("password")).thenReturn("123");
        Mockito.when(mockResultSet.getString("email")).thenReturn("abc0");
        Mockito.when(mockResultSet.getString("first_name")).thenReturn("first");
        Mockito.when(mockResultSet.getString("last_name")).thenReturn("last");
        Mockito.when(mockResultSet.getString("bio")).thenReturn("bio");
        Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);

        assertTrue(userDB.findUserByName(testUserList.get(0).getUsername()).isPresent());
        assertFalse(userDB.findUserByName(testUserList.get(1).getUsername()).isPresent());

        Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        try {
            userDB.addUser(testUserList.get(0));
        } catch (UserAlreadyPresentException e) {
            assertEquals(String.format("User already present with name: %s", testUserList.get(0).getUsername()),
                    e.getMessage());
        }
        userDB.removeUser(testUserList.get(0));
        userDB.removeAllUsers();
    }

    @Test
    public void testThrows() throws SQLException {
        Connection mockConnection = Mockito.mock(Connection.class);
        userDB.setConnect(mockConnection);
        Mockito.doThrow(SQLException.class).when(mockConnection).createStatement();
        Mockito.doThrow(SQLException.class).when(mockConnection).prepareStatement(any(String.class));

        try {
            userDB.getAllUsers();
        } catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }
        try {
            userDB.findUserByName(testUserList.get(0).getUsername());
        } catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }
        try {
            userDB.addUser(testUserList.get(0));
        } catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }
        try {
            userDB.removeUser(testUserList.get(0));
        } catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }
        try {
            userDB.removeAllUsers();
        } catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }
    }
}
