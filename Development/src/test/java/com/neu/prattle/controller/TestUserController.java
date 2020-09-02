package com.neu.prattle.controller;

import com.neu.prattle.model.User;
import com.neu.prattle.service.IUserService;
import com.neu.prattle.service.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class TestUserController {
    private UserController userController = new UserController();
    private User testUser = new User("dian", "1234", "email", "dian", "wang", "bio");
    private User testUser1 = new User("dian1", "1234", "email1", "dian", "wang", "bio");

    @Before
    public void setUp() throws SQLException {
        UserService.getInstance().testMode();
    }

    @Test
    public void testCreateUserAccount() {
        try (Response response = userController.createUserAccount(testUser)) {
            assertEquals(200, response.getStatus());
        }
        try (Response response = userController.createUserAccount(testUser)) {
            assertEquals(409, response.getStatus());
        }
    }

    @Test
    public void testLogin() {
//        testUser.setPassword("1234");
        Response response = userController.createUserAccount(testUser);
        User validationUser = new User("dian");
        validationUser.setPassword("1234");
        response = userController.loginUserAccount(validationUser);
        assertEquals(200, response.getStatus());

        validationUser.setPassword("12345");
        response = userController.loginUserAccount(validationUser);
        assertEquals(401, response.getStatus());

        validationUser.setUsername("dia");
        response = userController.loginUserAccount(validationUser);
        assertEquals(404, response.getStatus());

    }

    @Test
    public void testSQLException() throws SQLException {
        UserController userControllerException = new UserController();
        IUserService userServiceMock = Mockito.mock(UserService.class);
        Mockito.doThrow(SQLException.class).when(userServiceMock).addUser(any(User.class));
        Mockito.doThrow(SQLException.class).when(userServiceMock).findUserByName(any(String.class));
        Mockito.doThrow(SQLException.class).when(userServiceMock).getAllUsers();
        Mockito.doThrow(SQLException.class).when(userServiceMock).login(any(User.class));
        userControllerException.setAccountService(userServiceMock);

        Response response = userControllerException.createUserAccount(testUser);
        assertEquals(500, response.getStatus());

        response = userControllerException.loginUserAccount(testUser);
        assertEquals(500, response.getStatus());

        response = userControllerException.getAllUsers();
        assertEquals(500, response.getStatus());

    }

    @Test
    public void testGetAll() {
        userController.createUserAccount(testUser);
        userController.createUserAccount(testUser1);
        Response response = userController.getAllUsers();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeactivate() throws SQLException {
        userController.createUserAccount(testUser);
        assertTrue(testUser.getActive());
        Response response = userController.deactivateUser(testUser.getUsername());
        assertEquals(200, response.getStatus());
        assertFalse(testUser.getActive());

        assertTrue(testUser1.getActive());
        response = userController.deactivateUser(testUser1.getUsername());
        assertEquals(404, response.getStatus());
        assertTrue(testUser1.getActive());

        UserController userControllerException = new UserController();
        IUserService userServiceMock = Mockito.mock(UserService.class);
        Mockito.doThrow(SQLException.class).when(userServiceMock).findUserByName(any(String.class));
        userControllerException.setAccountService(userServiceMock);
        testUser.setActive(true);
        assertTrue(testUser.getActive());
        response = userControllerException.deactivateUser(testUser.getUsername());
        assertEquals(500, response.getStatus());
        assertTrue(testUser.getActive());
    }

    @Test
    public void testGetActive() throws SQLException {
        userController.createUserAccount(testUser);
        userController.createUserAccount(testUser1);
        userController.createUserAccount(new User("test", "test", "test", "first", "last", "bio", false));
        Response response = userController.getActiveUsers();
        assertEquals(200, response.getStatus());

        UserController userControllerException = new UserController();
        IUserService userServiceMock = Mockito.mock(UserService.class);
        Mockito.doThrow(SQLException.class).when(userServiceMock).getActiveUsers();
        userControllerException.setAccountService(userServiceMock);
        response = userControllerException.getActiveUsers();
        assertEquals(500, response.getStatus());
    }

    @Test
    public void getUserChannels() throws SQLException {
        userController.createUserAccount(testUser);
        Response response = userController.getUserChannels(testUser.getUsername());
        assertEquals(200, response.getStatus());

        UserController userControllerException = new UserController();
        IUserService userServiceMock = Mockito.mock(UserService.class);
        Mockito.doThrow(SQLException.class).when(userServiceMock).findUserByName(any(String.class));
        userControllerException.setAccountService(userServiceMock);
        response = userControllerException.getUserChannels(testUser.getUsername());
        assertEquals(500, response.getStatus());
    }

    @After
    public void cleanUp() throws SQLException {
        UserService.getInstance().removeAllUsers();
    }
}
