package com.neu.prattle.main;

import com.neu.prattle.controller.CMController;
import com.neu.prattle.controller.ChannelController;
import com.neu.prattle.controller.DMController;
import com.neu.prattle.controller.UserController;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/***
 * Sets up the resource classes for handling REST requests.
 * Refer {@link Application}
 *
 * @author CS5500 Fall 2019 Teaching staff, Team 9
 * @version dated 2019-10-06
 */
public class PrattleApplication extends Application {
    /**
     * Hash set of resources.
     */
    private Set<Class<?>> resourceClasses = new HashSet<>();

    @Override
    public Set<Class<?>> getClasses() {
        resourceClasses.add(UserController.class);
        resourceClasses.add(DMController.class);
        resourceClasses.add(CMController.class);
        resourceClasses.add(ChannelController.class);
        return resourceClasses;
    }
}
