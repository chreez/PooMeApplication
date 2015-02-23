package com.palme.GroupMeBot.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.palme.GroupMeBot.application.Domain;
import com.palme.GroupMeBot.application.PooMeApplication;

public class GroupMeResourceTest {

    private WebTarget target;
    private static Thread serverThread;

    @BeforeClass
    public static void setUp() throws Exception {
        GroupMeResourceTest.serverThread = new Thread(new Runnable() {
            private Server server;
            @Override
            public void run() {
              try {
                server = PooMeApplication.startServer(Domain.TEST, 8080);
            } catch (Exception e) {
                throw new RuntimeException();
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
