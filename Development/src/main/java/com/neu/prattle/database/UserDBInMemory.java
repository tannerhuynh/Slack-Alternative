package com.neu.prattle.database;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Class that implements IUserDB to create a database in memory. This acts similar to a remote
 * database, but allows for testing and working with methods before setting up the actual
 * database implementation.
 *
 * @author Team 9
 * @version 2019-10-30
 */
public class UserDBInMemory implements IUserDB {

    /**
     * Database instance.
     */
    private static UserDBInMemory userDB;
    /**
     * Hash set of users.
     */
    private Set<User> userSet = new HashSet<>();

    /**
     * UserDBInMemory is a Singleton class.
     */
    private UserDBInMemory() {
    }

    /**
     * Constructor for the UseDBInMemory.
     */
    static {
        userDB = new UserDBInMemory();
    }

    /**
     * Call this method to return an instance.
     *
     * @return userDB.
     */
    public static UserDBInMemory getInstance() {
        return userDB;
    }

    /**
     * Remove all instances in the memory. UserDBInMemory is a singleton so there might be conflicts in different test
     * cases because of the existing instances
     */
    @Override
    public void testMode() {
        removeAllUsers();
    }

    /**
     * Returns an optional object which might be empty or wraps an object if the Database
     * contains a {@link User} object having the same name as the parameter.
     *
     * @param name The name of the user.
     * @return Optional object, empty if the object does not exist.
     */
    @Override
    public Optional<User> findUserByName(String name) {
        for (User user:userSet){
            if (user.getUsername().equals(name)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Add a user in the database.
     *
     * @param user User object.
     *
     */
    @Override
    public void addUser(User user) {
        if (userSet.contains(user))
            throw new UserAlreadyPresentException(String.format("User already present with name: %s"
                    , user.getUsername()));
        userSet.add(user);
    }

    /**
     * Remove a user in the database.
     *
     * @param user User Object.
     */
    @Override
    public void removeUser(User user) {
        userSet.remove(user);
    }

    /**
     * Remove all users in the database.
     */
    @Override
    public void removeAllUsers() {
        userSet = new HashSet<>();
    }

    /**
     * Getter for all users from the user table.
     *
     * @return set of all users.
     */
    @Override
    public Set<User> getAllUsers() {
        return userSet;
    }

    /**
     * Update a user in the database.
     *
     * @param user the user to update on.
     */
    @Override
    public void updateUser(User user) {
        Optional<User> optionalUser = findUserByName(user.getUsername());
        if (!optionalUser.isPresent()){
            return;
        }
        removeUser(optionalUser.get());
        addUser(user);
    }
}
