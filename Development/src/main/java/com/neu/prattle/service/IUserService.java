package com.neu.prattle.service;

import com.neu.prattle.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Acts as an interface between the data layer and the servlet controller. The controller is
 * responsible for interfacing with this instance to perform all the CRUD operations on user
 * accounts.
 *
 * @author Team 9, CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-30
 */
public interface IUserService {
    /**
     * Set test mode for the service. Should be called and only be called for test purposes.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    void testMode() throws SQLException;

    /**
     * Returns an optional object which might be empty or wraps an object if the System contains a
     * {@link User} object having the same name as the parameter.
     *
     * @param name The name of the user.
     * @return Optional object.
     * @throws SQLException if the connection failed or there is an error.
     */
    Optional<User> findUserByName(String name) throws SQLException;

    /**
     * Tries to add a user in the system.
     *
     * @param user User object
     * @throws SQLException if the connection failed or there is an error.
     */
    void addUser(User user) throws SQLException;

    /**
     * Remove a user in the system by name.
     *
     * @param name the name of the user.
     * @throws SQLException if the connection failed or there is an error.
     */
    void removeUserByName(String name) throws SQLException;

    /**
     * Remove all users in the system.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    void removeAllUsers() throws SQLException;

    /**
     * Getter for all users in the system.
     *
     * @return set of users.
     * @throws SQLException when there is a failure in database.
     */
    List<User> getAllUsers() throws SQLException;

    /**
     * Getter for all active users in the system.
     *
     * @return set of users.
     * @throws SQLException when there is a failure in database.
     */
    List<User> getActiveUsers() throws SQLException;

    /**
     * Change the user's active status.
     *
     * @param username user to change.
     * @param active   user's active status.
     * @throws SQLException if the connection failed or there is an error.
     */
    void setUserActive(String username, boolean active) throws SQLException;

    /**
     * Login input user
     * @param loginUser login user
     * @return int representing the state of the login attempt:
     *  1: login successful
     *  0: user not found
     *  -1: user locked out
     *  -2: incorrect password
     * @throws SQLException if the connection failed or there is an error.
     */
    int login(User loginUser) throws SQLException;

    /**
     * Updates a User's backend attributed to match the changes made by the frontend user.
     * @param updateUser User's new attributes in the form of a User object.
     * @return an int representing whether the given user exists in the database.
     *  0: user not found
     *  1: login successful
     * @throws SQLException if the connection failed or there is an error.
     */
    int update(User updateUser) throws SQLException;
}
