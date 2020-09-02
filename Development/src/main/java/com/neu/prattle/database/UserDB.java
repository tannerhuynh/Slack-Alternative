package com.neu.prattle.database;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An IUserDB implementation using MySQL database.
 *
 * @author Team 9
 * @version 2019-10-30
 */
public class UserDB implements IUserDB {
    /**
     * UserDB is a singleton.
     */
    private static UserDB userDB;
    /**
     * SQL connection object.
     */
    private Connection connect;
    /**
     * Config for storing database password of Key, Password
     */
    private Map<String, String> config = new HashMap<>();
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(UserDB.class.getName());

    /**
     * Getter for database password from config.
     *
     * @return password of the database.
     */
    private String getPassword() {
        return config.get("db_password");
    }

    /**
     * Constructor to set up connect object to connect to MySQL database.
     */
    @SuppressWarnings("squid:S4925")
    private UserDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            config.put("db_password", "bowshot123");
            connect = DriverManager.getConnection(
                    "jdbc:mysql://bowshot-free.cron68rx3yeb.us-east-1.rds.amazonaws.com:3306/" +
                            "?useSSL=false",
                    "admin", getPassword());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Getter of the User database singleton instance.
     *
     * @return the singleton.
     */
    public static UserDB getInstance() {
        if (userDB == null) {
            userDB = new UserDB();
        }
        return userDB;
    }

    /**
     * Getter for all Users from the User table.
     *
     * @return Set of all Users.
     * @throws SQLException when executeQuery failed.
     */
    public Set<User> getAllUsers() throws SQLException {
        try (Statement statement = connect.createStatement()) {
            ResultSet resultSet = statement
                    .executeQuery("select * from bowshot.user");
            return getUserSet(resultSet);
        }
    }

    /**
     * Transform resultSet into a User set.
     *
     * @param resultSet SQL result set that contains result from a select query.
     * @return set of Users.
     * @throws SQLException when error in resultSet.
     */
    private Set<User> getUserSet(ResultSet resultSet) throws SQLException {
        Set<User> userSet = new HashSet<>();
        while (resultSet.next()) {
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            String email = resultSet.getString("email");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String bio = resultSet.getString("bio");
            boolean active = resultSet.getBoolean("active");
            userSet.add(new User(username, password, email, firstName, lastName, bio, active));
        }
        return userSet;
    }

    /**
     * The test of UserDB is done by Mocking.
     */
    @Override
    public void testMode() {
        // do nothing
    }

    /**
     * Returns an optional object which might be empty or wraps an object if the user table contains
     * a {@link User} object having the same name as the parameter. Currently will get all users in
     * the user table and iterate through them to find the match. May refactor to use a different
     * SQL query
     *
     * @param name The name of the User.
     * @return Optional object.
     * @throws SQLException when query failed.
     */
    @Override
    public Optional<User> findUserByName(String name) throws SQLException {
        Set<User> userSet = getAllUsers();
        for (User user : userSet) {
            if (user.getUsername().equals(name)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Add a User in the User table.
     *
     * @param user User object.
     * @throws SQLException when query failed.
     */
    @Override
    public void addUser(User user) throws SQLException {
        if (findUserByName(user.getUsername()).isPresent()) {
            throw new UserAlreadyPresentException(String.format("User already present with name: %s"
                    , user.getUsername()));
        }
        try (PreparedStatement statement = connect
                .prepareStatement("insert into bowshot.user (username, password, email, " +
                        "first_name, last_name, bio) values (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setString(6, user.getBio());
            statement.executeUpdate();
        }
    }

    /**
     * Remove a User in the User table.
     *
     * @param user User Object.
     * @throws SQLException when query failed.
     */
    @Override
    public void removeUser(User user) throws SQLException {
        try (PreparedStatement statement = connect
                .prepareStatement("delete from bowshot.user where username = ?")) {
            statement.setString(1, user.getUsername());
            statement.executeUpdate();
        }
    }

    /**
     * Remove all Users in the User table.
     *
     * @throws SQLException when query failed.
     */
    @Override
    public void removeAllUsers() throws SQLException {
        try (Statement statement = connect.createStatement()) {
            statement.executeUpdate("delete from bowshot.user");
        }
    }

    /**
     * Update a User in the database. Find the user with the same username in the table and update
     * all other fields.
     *
     * @param user the User to update on.
     */
    @Override
    public void updateUser(User user) throws SQLException {
        try (PreparedStatement statement = connect.prepareStatement("update bowshot.user set " +
                "password = ?, email = ?, first_name = ?, last_name = ?, bio = ?, active = ? " +
                "where username = ?")) {
            statement.setString(7, user.getUsername());
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getBio());
            statement.setBoolean(6, user.getActive());
            statement.executeUpdate();
        }
    }

    /**
     * Setter for the connection.
     *
     * @param connect connection.
     */
    public void setConnect(Connection connect) {
        this.connect = connect;
    }
}
