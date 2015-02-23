package com.palme.GroupMeBot.client;

import javax.ws.rs.core.Response;

import com.palme.GroupMeBot.groupme.client.GroupMeClient;
import com.palme.GroupMeBot.groupme.client.GroupMeClientImpl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GroupMeClientImplTest extends TestCase {

    static private final String ACCESS_TOKEN = "0d50f452e40c703311be39ac76";

    public void sendMessageTest(String message) {
    }

    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     * @return
     */
    public GroupMeClientImplTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(GroupMeClientImplTest.class);
    }

    public void testPostMessage_Success() {
        final GroupMeClient client = new GroupMeClientImpl(ACCESS_TOKEN);
        final Response response = client.sendMessage("test");
        assertTrue(response.getStatus() == 202);
    }
}
