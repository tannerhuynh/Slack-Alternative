package com.neu.prattle.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A data transfer object for {@link Channel}. Example usage: when creating a channel, it is not possible to let the
 * front end to fill all of the user fields in channel. Instead, the front end can just provide a list of
 * participants/mods in terms of username.
 *
 * @author Team9
 * @version dated 2019-11-13
 */
public class ChannelDTO {
    /**
     * Channel id
     */
    private int id;
    /**
     * Channel name
     */
    private String name;
    /**
     * Channel participants
     */
    private List<String> participants = new ArrayList<>();
    /**
     * Channel mods
     */
    private List<String> mods = new ArrayList<>();

    /**
     * Constructors
     */
    public ChannelDTO() {
    }

    /** Initialization of a channel sets the ID.
     * @param id a unique int
     */
    public ChannelDTO(int id) {
        this.id = id;
    }

    /** Initialization of a channel using an ID number and a natural language name
     * @param id the unique ID integer of the channel
     * @param name the natural language name of the channel
     */
    public ChannelDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Initialization of a channel using several parameters -
     * @param id the unique  ID integer of the channel
     * @param name  the natural language name of the channel
     * @param participants the list of users in the channel (their usernames)
     * @param mods the list of mods in the channel (their usernames)
     */
    public ChannelDTO(int id, String name, List<String> participants, List<String> mods) {
        this.id = id;
        this.name = name;
        this.participants = participants;
        this.mods = mods;
    }

    /**
     * Getter for channel id
     * @return channel id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for channel id
     * @param id channel id
     */
    public void setId(int id) {
        this.id = id;
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

    /**
     * Getter for participants
     * @return channel participants
     */
    public List<String> getParticipants() {
        return participants;
    }

    /**
     * Setter for participants
     * @param participants channel participants
     */
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    /**
     * Getter for channel mods
     * @return channel mods
     */
    public List<String> getMods() {
        return mods;
    }

    /**
     * Setter for channel mods
     * @param mods channel mods
     */
    public void setMods(List<String> mods) {
        this.mods = mods;
    }
}
