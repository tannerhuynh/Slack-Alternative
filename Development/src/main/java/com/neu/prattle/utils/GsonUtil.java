package com.neu.prattle.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A GsonUtil class providing a static gson object.
 *
 * @author Team 9
 * @version dated 2019-11-21
 */
public abstract class GsonUtil {
    /**
     * Static gson
     */
    private static Gson gson;

    /**
     * apply UserChannelSetExclusionStrategy to gson
     */
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies( new UserChannelSetExclusionStrategy() );
        gson = builder.create();
    }

    /**
     * Get gson object
     * @return the static gson object
     */
    public static Gson getGson(){
        return gson;
    }

    private GsonUtil(){
    }
}
