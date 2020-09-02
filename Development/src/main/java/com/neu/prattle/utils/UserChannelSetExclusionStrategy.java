package com.neu.prattle.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.neu.prattle.model.User;

/**
 * The gson exclusion strategy for excluding User.channelSet
 *
 * @author Team 9
 * @version dated 2019-11-21
 */
public class UserChannelSetExclusionStrategy implements ExclusionStrategy
{
    /**
     * Return false. No specific class is excluded
     *
     * @param clazz class
     * @return false
     */
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    /**
     * Return true if f is User.channelSet or User.modChannelSet
     * @param f input field
     * @return true if f is User.channelSet or User.modChannelSet
     */
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass() == User.class && (f.getName().equals("channelSet") || f.getName().equals("modChannelSet"));
    }
}