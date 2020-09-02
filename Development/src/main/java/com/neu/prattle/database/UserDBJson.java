package com.neu.prattle.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link IUserDB} using json. It stores the user in a json file called user .json
 * in the Development directory.
 *
 * @author Team 9
 * @version 2019-10-30
 */
public class UserDBJson implements IUserDB {

    /**
     * UserDBJson is a Singleton class.
     */
    private UserDBJson() {
    }

    /**
     * The user database in JSON format.
     */
    private static UserDBJson userDB;

    /**
     * Initialization of the DB.
     */
    static {
        userDB = new UserDBJson();
    }

    /**
     * Call this method to return an instance
     *
     * @return this
     */
    public static UserDBJson getInstance() {
        return userDB;
    }

    /**
     * the logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * stores the user in user.json
     */
    private String jsonFile = "user.json";

    /**
     * read all users in the json file
     *
     * @return a set of all users in the json file
     */
    public Set<User> getAllUsers() {
        Set<User> userSet = new HashSet<>();
        Type type = new TypeToken<HashSet<User>>() {
        }.getType();
        try (JsonReader reader = new JsonReader(new FileReader(jsonFile))) {
            Gson gson = new Gson();
            userSet = gson.fromJson(reader, type);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return userSet;
    }

    /**
     * overwrite the user json file
     *
     * @param userSet the user set to write
     */
    private void saveUsers(Set<User> userSet) {
        Gson gson = new Gson();
        String json = gson.toJson(userSet);
        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))
        ) {
            writer.write(json);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * use test_user.json instead of user.json
     */
    @Override
    public void testMode() {
        jsonFile = "test_user.json";
    }

    @Override
    public Optional<User> findUserByName(String name) {
        Set<User> userSet = getAllUsers();
        for (User user : userSet) {
            if (user.getUsername().equals(name)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /** Adds a User to the JSON database.
     * @param user User object to add
     */
    @Override
    public void addUser(User user) {
        Set<User> userSet = getAllUsers();
        if (userSet.contains(user))
            throw new UserAlreadyPresentException(String.format("User already present with name: %s", user.getUsername()));

        userSet.add(user);
        saveUsers(userSet);
    }

    /** Removes a User from the JSON database.
     * @param user User Object to remove
     */
    @Override
    public void removeUser(User user) {
        Set<User> userSet = getAllUsers();
        userSet.remove(user);
        saveUsers(userSet);
    }

    /**
     * Removes all Users from the database by re-initializing a new, empty hashset of Users.
     */
    @Override
    public void removeAllUsers() {
        Set<User> userSet = new HashSet<>();
        saveUsers(userSet);
    }

    /** Updates a User in the database by removing them, then adding them. This can change their bio, avatar,
     * or other non-integral properties (it wouldn't be used to change their username).
     * @param user the user to update on.
     */
    @Override
    public void updateUser(User user) {
        Optional<User> optionalUser = findUserByName(user.getUsername());
        if (!optionalUser.isPresent()) {
            return;
        }
        removeUser(optionalUser.get());
        addUser(user);
    }
}
