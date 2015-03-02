package com.palme.GroupMeBot.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.palme.GroupMeBot.application.Domain;
import com.palme.GroupMeBot.application.PooMeApplication;
import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;

public class GroupMeResourceTest {

    private WebTarget target;
    private static Thread serverThread;
    final private static List<String> POO_KEY_WORDS  = ImmutableList.of("🍫", "💩");

    @BeforeClass
    public static void setUp() throws Exception {
        GroupMeResourceTest.serverThread = new Thread(new Runnable() {
            private Server server;
            @Override
            public void run() {
              try {
                server = PooMeApplication.startServer(8080);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
            }
        });
        serverThread.start();
        Thread.sleep(5000);
    }

    @AfterClass
    public static void tearDown() {
        GroupMeResourceTest.serverThread.interrupt();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        Client c = ClientBuilder.newClient();
        target = c.target(PooMeApplication.BASE_URI);


        String responseMsg = target.path("bot_callback").request().get(String.class);
        assertEquals("healthy", responseMsg);
    }


    @Test
    public void postIt() throws IOException, URISyntaxException {
        Client c = ClientBuilder.newClient();
        target = c.target(PooMeApplication.BASE_URI);

        final String entity = Files.toString(new File(getClass().getResource("test.json").toURI()), Charsets.UTF_8);

        final Response response = target.path("bot_callback").request().post(Entity.json(entity));
        assertEquals(204, response.getStatus());
    }

    @Test
    public void postAPoop() throws IOException, URISyntaxException {
        Client c = ClientBuilder.newClient();
        target = c.target(PooMeApplication.BASE_URI);

        IncomingGroupMeMessage message = new IncomingGroupMeMessage();
        message.setUser_id("123");
        message.setSender_type("user");
        message.setName("chris");
        message.setText("" + POO_KEY_WORDS.get(0));

        final String entity = new ObjectMapper().writeValueAsString(message);

        final Response response = target.path("bot_callback").request().post(Entity.json(entity));
        assertEquals(204, response.getStatus());
    }

//    @Test
    public void postItSingleton() throws IOException, URISyntaxException {
        Client c = ClientBuilder.newClient();
        target = c.target(PooMeApplication.BASE_URI);

        final String entity = Files.toString(new File(getClass().getResource("test.json").toURI()), Charsets.UTF_8);

        Response response = target.path("bot_callback").request().post(Entity.json(entity));
        assertEquals(204, response.getStatus());
        response = target.path("bot_callback").request().post(Entity.json(entity));
        assertEquals(204, response.getStatus());
        response = target.path("bot_callback").request().post(Entity.json(entity));
        assertEquals(204, response.getStatus());
    }
}
