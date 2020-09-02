package com.neu.prattle.database;

import com.neu.prattle.model.User;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

/**
 * Class that acts as a database controller. The controller is responsible for interacting with the
 * database to perform all the CRUD operations on user. There are methods to get, set, add, and
 * remove users from the database instance.
 *
 * @author Team 9
 * @version 2019-10-30
 */
public interface IUserDB {
    /**
     * Set test mode for the database interface. Should be called and only be called for test
     * purposes.
     */
    void testMode();

    /**
     * Get all users from the user table.
     *
     * @return set of all users.
     */
    Set<User> getAllUsers() throws SQLException;

    /**
     * Returns an optional object which might be empty or wraps an object if the Database contains a
     * {@link User} object having the same name as the parameter.
     *
     * @param name The name of the user.
     * @return Optional object.
     * @throws SQLException if the connection failed or there is an error.
     */
    Optional<User> findUserByName(String name) throws SQLException;

    /**
     * Add a user in the database.
     *
     * @param user User object.
     * @throws SQLException if the connection failed or there is an error.
     */
    void addUser(User user) throws SQLException;

    /**
     * Remove a user in the database.
     *
     * @param user User Object.
     * @throws SQLException if the connection failed or there is an error.
     */
    void removeUser(User user) throws SQLException;

    /**
     * Remove all users in the database.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    void removeAllUsers() throws SQLException;

    /**
     * Update a user in the database.
     *
     * @param user the user to update on.
     * @throws SQLException if the connection failed or there is an error.
     */
    void updateUser(User user) throws SQLException;
}
