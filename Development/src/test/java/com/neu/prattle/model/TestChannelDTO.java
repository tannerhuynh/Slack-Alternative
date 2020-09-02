package com.neu.prattle.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestChannelDTO {

    @Test
    public void constructor() {
        ChannelDTO channel = new ChannelDTO();
        assertNull(channel.getName());
        channel = new ChannelDTO(0);
        assertEquals(0, channel.getId());
    }

    @Test
    public void getId() {
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0");
        assertEquals(0, channelDTO.getId());
    }

    @Test
    public void setId() {
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0");
        channelDTO.setId(1);
        assertEquals(1, channelDTO.getId());
    }

    @Test
    public void getName() {
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0");
        assertEquals("channel0", channelDTO.getName());
    }

    @Test
    public void setName() {
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0");
        channelDTO.setName("channel1");
        assertEquals("channel1", channelDTO.getName());
    }

    @Test
    public void getParticipants() {
        List<String> participants = new ArrayList<>();
        participants.add("user0");
        participants.add("user1");
        List<String> mods = new ArrayList<>();
        mods.add("user0");
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0", participants, mods);
        assertTrue(channelDTO.getParticipants().contains("user0"));
        assertTrue(channelDTO.getParticipants().contains("user1"));
    }

    @Test
    public void setParticipants() {
        List<String> participants = new ArrayList<>();
        participants.add("user0");
        participants.add("user1");
        List<String> mods = new ArrayList<>();
        mods.add("user0");
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0", participants, mods);
        channelDTO.setParticipants(new ArrayList<>());
        assertFalse(channelDTO.getParticipants().contains("user0"));
        assertFalse(channelDTO.getParticipants().contains("user1"));
    }

    @Test
    public void getMods() {
        List<String> participants = new ArrayList<>();
        participants.add("user0");
        participants.add("user1");
        List<String> mods = new ArrayList<>();
        mods.add("user0");
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0", participants, mods);
        assertTrue(channelDTO.getMods().contains("user0"));
    }

    @Test
    public void setMods() {
        List<String> participants = new ArrayList<>();
        participants.add("user0");
        participants.add("user1");
        List<String> mods = new ArrayList<>();
        mods.add("user0");
        ChannelDTO channelDTO = new ChannelDTO(0, "channel0", participants, mods);
        channelDTO.setMods(new ArrayList<>());
        assertFalse(channelDTO.getMods().contains("user0"));
    }
}