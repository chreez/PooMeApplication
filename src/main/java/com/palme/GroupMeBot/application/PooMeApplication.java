package com.palme.GroupMeBot.application;

import java.net.URI;
import java.sql.DriverManager;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.google.common.collect.ImmutableMap;
//import org.glassfish.jersey.server.ResourceConfig;
//import com.sun.jersey.api.core.PackagesResourceConfig;

public class PooMeApplication {
    static private final String tokenId = "";

//    public static final String BASE_URI = "http://192.168.1.98:25565/myapp/";
    public static final String BASE_URI = "http://localhost:8080";

    /**
     * Main method.
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
//        // The port that we should run on can be set into an environment variable
//        // Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        startServer(Integer.valueOf(webPort));
    }

    public static Server startServer(final Integer webPort) throws Exception {
        final Server server = new Server(webPort);
        final WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setParentLoaderPriority(true);

        final String webappDirLocation = "src/main/webapp/";
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        server.setHandler(root);

        server.start();
        server.join();
        return server;
    }


}
