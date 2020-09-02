package com.neu.prattle.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A class for representing Channels not limited by number of participants.
 *
 * @author Team 9
 * @version dated 2019-11-12
 */
@Entity
@Table(name = "channel")
public class Channel implements Serializable {
    /**
     * Channel ID.
     */
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Channel name
     */
    @Column(name = "name")
    private String name;

    /**
     * This Channel's mods.
     */
    @ManyToMany(fetch=FetchType.EAGER, cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "channel_mod",
            joinColumns = {@JoinColumn(name = "channel_id")},
            inverseJoinColumns = { @JoinColumn(name = "username")}
    )
    private Set<User> mods = new HashSet<>();

    /**
     * Set of users.
     */
    @ManyToMany(fetch=FetchType.EAGER, cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "channel_user",
            joinColumns = {@JoinColumn(name = "channel_id")},
            inverseJoinColumns = { @JoinColumn(name = "username")}
    )
    private Set<User> participants = new HashSet<>();

    /**
     * Constructs a Channel for any-sized group.
     *
     * @param id    the unique name that has been chosen for this channel.
     * @param mods         the specified mod's username.
     * @param participants the initial User participants.
     */
    public Channel(int id, Set<User> mods, Set<User> participants) {
        this.id = id;
        this.mods = mods;
        this.participants = participants;
    }

    /**
     * Constructs a Channel for any-sized group.
     *
     * @param id    the unique name that has been chosen for this channel.
     * @param name  the name of the channel
     * @param mods         the specified mod's username.
     * @param participants the initial User participants.
     */
    public Channel(int id, String name, Set<User> mods, Set<User> participants) {
        this.id = id;
        this.name = name;
        this.mods = mods;
        this.participants = participants;
    }

    /**
     * Constructs a Channel for any-sized group.
     *
     * @param id    the unique name that has been chosen for this channel.
     * @param name  the name of the channel
     */
    public Channel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * default constructor
     */
    public Channel() {

    }

    /**
     * Getter for the channel ID.
     *
     * @return channel ID.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setter for the channel ID
     * @param id channel ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the set of mods of this Channel.
     *
     * @return the usernames of the Users who are mods of this channel.
     */
    public Set<User> getMods() {
        return this.mods;
    }

    /**
     * Setter for the set of mods of this channel
     * @param mods the usernames of the Users who are mods of this channel.
     */
    public void setMods(Set<User> mods) {
        this.mods = mods;
    }

    /**
     * Add a mod to the channel.
     *
     * @param mod user with moderator permissions.
     */
    public void addMod(User mod) {
        this.mods.add(mod);
    }

    /**
     * Getter for the set of participants.
     *
     * @return Set of participants.
     */
    public Set<User> getParticipants() {
        return this.participants;
    }

    /**
     * Setter for the set of participants
     * @param participants Set of participants.
     */
    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    /**
     * Adds a participant to this Channel.
     *
     * @param user User to be added to this Channel.
     */
    public void addParticipant(User user) {
        this.participants.add(user);
    }

    /**
     * Getter for channel name
     * @return channel name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for channel name
     * @param name channel name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** The unique hashcode for a channel is the channel's ID, an int.
     * @return the int of this channel
     */
    @Override
    public int hashCode() {
        return id;
    }

    /** A channel is equal to another channel if the IDs are identical, and they're both Channel objects.
     * @param obj the object to test equality with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Channel)) {
            return false;
        }
        return ((Channel) obj).getId() == getId();
    }
}