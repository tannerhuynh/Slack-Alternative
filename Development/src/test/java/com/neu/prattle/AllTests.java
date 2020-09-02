package com.neu.prattle;

import com.neu.prattle.controller.TestCMController;
import com.neu.prattle.controller.TestChannelController;
import com.neu.prattle.controller.TestDMController;
import com.neu.prattle.controller.TestUserController;
import com.neu.prattle.database.*;
import com.neu.prattle.model.*;
import com.neu.prattle.service.TestChannelService;
import com.neu.prattle.service.TestMessageService;
import com.neu.prattle.service.TestUserService;
import com.neu.prattle.websocket.TestCMEndpoint;
import com.neu.prattle.websocket.TestChatEndpoint;
import com.neu.prattle.websocket.TestDMEndpoint;
import com.neu.prattle.websocket.TestMessageDecoder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test Suite of all tests.
 */
@RunWith(Suite.class)
@SuiteClasses({
        TestChannelController.class,
        TestUserController.class,
        TestDMController.class,
        TestCMController.class,

        TestChannelDBHibernate.class,
        TestChannelDBInMemory.class,
        TestMessageDBHibernate.class,
        TestMessageDBInMemory.class,
        TestUserDB.class,
        TestUserDBHibernate.class,
        TestUserDBInMemory.class,
        TestUserDBJson.class,

        TestChannel.class,
        TestChannelDTO.class,
        TestMessage.class,
        TestUser.class,

        TestChannelService.class,
        TestMessageService.class,
        TestUserService.class,

        TestMessageDecoder.class,
        TestCMEndpoint.class,
        TestDMEndpoint.class,
        TestMessageDecoder.class
})
public class AllTests {

}

