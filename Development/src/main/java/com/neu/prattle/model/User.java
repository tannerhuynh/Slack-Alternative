package com.neu.prattle.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/***
 * A User object represents a basic account information for a user.
 *
 * @author Team 9
 * @version dated 2019-11-04
 */
@Entity
@Table(name = "user")
public class User implements Serializable {
    /**
     * The username of the User.
     */
    @Id
    @Column(name = "username")
    private String username;
    /**
     * The User's password.
     */
    @Column(name = "password", nullable = false)
    private String password;
    /**
     * The User's email.
     */
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    /**
     * The User's first name, less than 30 characters.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * The User's last name, less than 30 characters.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;
    /**
     * User's biography. Potentially necessary to set a limit of what the total of amount of
     * characters can be in order to fit into interface.
     */
    @Column(name = "bio", nullable = false)
    private String bio;
    /**
     * Field indicating if this User is a Mod.
     */
    @Column(name = "isMod", nullable = false)
    private boolean isMod = false;
    /**
     * If User is active or not. Active in this sense means they have not deleted their account.
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * The login attempts the user has after the last successful login
     */
    @Column(name = "login_attempts", nullable = false, columnDefinition = "int default 0")
    private int loginAttempts = 0;

    /**
     * The lockout time due to too many login attempts
     */
    @Column(name = "lockout")
    private Timestamp lockout;

    /**
     * The channels that the user is participant of
     */
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "participants")
    private Set<Channel> channelSet = new HashSet<>();

    /**
     * The channels that the user is mod of
     */
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "mods")
    private Set<Channel> modChannelSet = new HashSet<>();

    /**
     * The avatar id of the user
     */
    @Column(name = "avatar")
    private Integer avatar = 0;

    /**
     * Empty constructor should not be called.
     */
    public User() {
        // Nothing
    }

    /**
     * Constructor for the User object.
     *
     * @param username username of the user.
     */
    public User(String username) {
        setUsername(username);
    }

    /**
     * Constructor for the User object.
     *
     * @param username  username of the User.
     * @param password  password for the account.
     * @param email     email of the User.
     * @param firstName the first name of the User.
     * @param lastName  the last name of the User.
     * @param bio       the biography of the User.
     */
    public User(String username,
                String password,
                String email,
                String firstName,
                String lastName,
                String bio) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setBio(bio);
        setActive(true);
    }

    /**
     * Constructor for the User object.
     *
     * @param username  username of the User.
     * @param password  password for the account.
     * @param email     email of the User.
     * @param firstName the first name of the User.
     * @param lastName  the last name of the User.
     * @param bio       the biography of the User.
     * @param active    whether or not the user is active. Active in this sense means the account
     *                  has not been deleted.
     */
    public User(String username,
                String password,
                String email,
                String firstName,
                String lastName,
                String bio,
                boolean active) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setBio(bio);
        setActive(active);
    }

    /***
     * Returns the hashCode of this object. As name can be treated as a sort of identifier for
     * this instance, we can use the hashCode of "name" for the complete object.
     *
     * @return hashCode of "this".
     */
    public int hashCode() {
        return this.getUsername().hashCode();
    }

    /***
     * Makes comparison between two user accounts. Two user objects are equal if their name are
     * equal ( names are case-sensitive ).
     *
     * @param obj Object to compare.
     * @return a predicate value for the comparison.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;

        User user = (User) obj;
        return user.getUsername().equals(getUsername());
    }

    /**
     * A getter for the username of this user.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the user name of the user.
     *
     * @param name user name in string format.
     */
    public void setUsername(String name) {
        this.username = name;
    }

    /**
     * Getter for the password. This will be removed once we become secure :).
     *
     * @return the password of this user.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Setter for this user's password.
     *
     * @param password the chosen password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for this user's email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Setter for this user's email.
     *
     * @param email the chosen email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for the first name of the user.
     *
     * @return first name in string format.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Setter for the first name of the user.
     *
     * @param name first name in string format.
     */
    public void setFirstName(String name) {
        this.firstName = name;
    }

    /**
     * Getter for the last name of the user.
     *
     * @return last name in string format.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Setter for the last name of the user.
     *
     * @param name last name in string format.
     */
    public void setLastName(String name) {
        this.lastName = name;
    }

    /**
     * Getter for the bio of the user.
     *
     * @return bio in string format.
     */
    public String getBio() {
        return this.bio;
    }

    /**
     * Setter for the bio of the user.
     *
     * @param bio bio in string format.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Getter for isMod
     *
     * @return isMod
     */
    public boolean getIsMod() {
        return isMod;
    }

    /**
     * Setter for isMod
     *
     * @param isMod isMod
     */
    public void setIsMod(boolean isMod) {
        this.isMod = isMod;
    }

    /**
     * Getter for the active status of the user.
     *
     * @return if the user is active.
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Setter for the active status of the user.
     *
     * @param active active status of the user.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Getter of login attempts
     *
     * @return login attempts
     */
    public int getLoginAttempts() {
        return loginAttempts;
    }

    /**
     * Setter of login attempts
     *
     * @param loginAttempts login attempts
     */
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    /**
     * Getter of lockout
     *
     * @return lockout
     */
    public Timestamp getLockout() {
        return lockout;
    }

    /**
     * Setter of lockout
     *
     * @param lockout lockout
     */
    public void setLockout(Timestamp lockout) {
        this.lockout = lockout;
    }

    /**
     * Getter for channel set
     *
     * @return channel set
     */
    public Set<Channel> getChannelSet() {
        return channelSet;
    }

    /**
     * Setter for channel set
     *
     * @param channelSet channel set
     */
    public void setChannelSet(Set<Channel> channelSet) {
        this.channelSet = channelSet;
    }

    /**
     * Getter for mod channel set
     *
     * @return mod channel set
     */
    public Set<Channel> getModChannelSet() {
        return modChannelSet;
    }

    /**
     * Setter for mod channel set
     *
     * @param modChannelSet mod channel set
     */
    public void setModChannelSet(Set<Channel> modChannelSet) {
        this.modChannelSet = modChannelSet;
    }

    /**
     * Getter for user avatar
     *
     * @return user avatar
     */
    public Integer getAvatar() {
        return avatar;
    }

    /**
     * Setter for user avatar
     *
     * @param avatar user avatar
     */
    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }
}