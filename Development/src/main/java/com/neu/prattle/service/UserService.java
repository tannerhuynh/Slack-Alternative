package com.neu.prattle.service;

import com.neu.prattle.database.IUserDB;
import com.neu.prattle.database.UserDBHibernate;
import com.neu.prattle.database.UserDBInMemory;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Implementation of {@link IUserService}. Provides user related services. Users can active or
 * inactive. Active means that a User can still log into the service. Inactive users are
 * considered deleted and can no longer log into the service.
 *
 * @author Team 9
 * @version dated 2019-10-30
 */
public class UserService implements IUserService {
    /**
     * User Service instance.
     */
    private static UserService accountService;

    /**
     * UserDB.
     */
    private IUserDB userDB;

    /**
     * UserService is a Singleton class. Currently if the connection to database failed, the
     * constructor will keep trying to reconnect in a loop.
     */
    private UserService() {
        userDB = UserDBHibernate.getInstance();
    }

    /**
     * Creation of a user service.
     */
    static {
        accountService = new UserService();
    }

    /**
     * Call this method to return an instance of this service.
     *
     * @return this
     */
    public static UserService getInstance() {
        return accountService;
    }

    /**
     * Use the InMemory database instead of the actual database for testing.
     * each call will also clear the instances in the InMemory database to avoid conflicts in different test cases
     */
    @Override
    public void testMode() {
        userDB = UserDBInMemory.getInstance();
        try {
            userDB.removeAllUsers();
        } catch (SQLException e) {
            //do nothing
        }
    }

    /**
     * Find the User by their name.
     *
     * @param name the name of the user.
     * @return An optional wrapper supplying the user.
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public Optional<User> findUserByName(String name) throws SQLException {
        return userDB.findUserByName(name);
    }

    /**
     * Tries to add a User in the system.
     *
     * @param user User object
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public synchronized void addUser(User user) throws SQLException {
        if (userDB.findUserByName(user.getUsername()).isPresent()){
            throw new UserAlreadyPresentException(String.format("User already present with name: %s"
                    , user.getUsername()));
        }
        userDB.addUser(user);
    }

    /**
     * Remove a User in the system by name.
     *
     * @param name the name of the user.
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public synchronized void removeUserByName(String name) throws SQLException {
        Optional<User> optionalIUser = userDB.findUserByName(name);
        if (!optionalIUser.isPresent()) {
            return;
        }
        User user = optionalIUser.get();
        userDB.removeUser(user);
    }

    /**
     * Remove all Users in the system.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public synchronized void removeAllUsers() throws SQLException {
        userDB.removeAllUsers();
    }

    /**
     * Getter for all Users in the system.
     *
     * @return set of Users.
     * @throws SQLException when there is a failure in database.
     */
    @Override
    public List<User> getAllUsers() throws SQLException {
        Set<User> userSet = userDB.getAllUsers();
        List<User> userList = new ArrayList<>();
        for (User user : userSet) {
            userList.add((User) user);
        }
        return userList;
    }

    /**
     * Getter for all active Users in the system.
     *
     * @return set of users.
     * @throws SQLException when there is a failure in database.
     */
    @Override
    public List<User> getActiveUsers() throws SQLException {
        Set<User> userSet = userDB.getAllUsers();
        List<User> userList = new ArrayList<>();
        for (User user : userSet) {
            if (user.getActive()) {
                userList.add((User) user);
            }
        }
        return userList;
    }

    /**
     * Change the User's active status.
     *
     * @param username User to change.
     * @param active   User's active status.
     */
    @Override
    public void setUserActive(String username, boolean active) throws SQLException {
        Optional<User> optionalUser = findUserByName(username);
        if (!optionalUser.isPresent()) {
            return;
        }
        User user = optionalUser.get();
        user.setActive(active);
        userDB.updateUser(user);
    }

    /**
     * Check if the input lockout timestamp is in the lock range of 1 day
     * @param lockoutTimestamp timestamp that a user is locked out
     * @return true if input timestamp is no earlier than one day from now
     */
    boolean checkLockout(Timestamp lockoutTimestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        return lockoutTimestamp.after(timestamp);
    }

    /**
     * Update the login attempt of input user.
     * if the current login attempts is less than 3, +1
     * if the current login attempts is equal or greater than 3, lock out the user and set the login attempt to 0
     * @param user database instance of the user trying to login
     * @throws SQLException when there is a failure in database.
     */
    void updateLoginAttempts(User user) throws SQLException{
        if (user.getLoginAttempts() < 3) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
        }
        else{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
            user.setLockout(timestamp);
            user.setLoginAttempts(0);
        }
        userDB.updateUser(user);
    }

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
    @Override
    public int login(User loginUser) throws SQLException {
        Optional<User> optionalUser = findUserByName(loginUser.getUsername());
        if (!optionalUser.isPresent()) {
            return 0;
        }
        User user = optionalUser.get();
        if (user.getLockout() != null && checkLockout(user.getLockout())) {
            return -1;
        }
        if (!user.getPassword().equals(loginUser.getPassword())){
            updateLoginAttempts(user);
            return -2;
        }
        user.setLoginAttempts(0);
        userDB.updateUser(user);
        return 1;
    }

    /**
     * Updates a User's backend attributed to match the changes made by the frontend user.
     * @param updateUser User's new attributes in the form of a User object.
     * @return an int representing whether the given user exists in the database.
     *  0: user not found
     *  1: login successful
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public int update(User updateUser) throws SQLException{
        Optional<User> optionalUser = findUserByName(updateUser.getUsername());
        if (!optionalUser.isPresent()) {
            return 0;
        }
        User user = optionalUser.get();
        if (updateUser.getFirstName() != null) {
            user.setFirstName(updateUser.getFirstName());
        }
        if (updateUser.getLastName() != null) {
            user.setLastName(updateUser.getLastName());
        }
        if (updateUser.getBio() != null) {
            user.setBio(updateUser.getBio());
        }
        if (updateUser.getAvatar() != null) {
            user.setAvatar(updateUser.getAvatar());
        }
        userDB.updateUser(user);
        return 1;
    }

}
