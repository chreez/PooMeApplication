package com.palme.GroupMeBot.application;

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
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
//    public static HttpServer startServer(final Domain domain) throws ClassNotFoundException, SQLException {
//
//        final GroupMeClient client = new GroupMeClientImpl(DOMAIN_TO_TOKEN_ID.get(domain));
//
//        // in com.palme.GroupMeBot package
//        final ResourceConfig rc = new ResourceConfig().packages("com.palme.GroupMeBot.server");
//        rc.register(new GroupMeResourceImpl(client, processors));
//        // create and start a new instance of grizzly http server
//        // exposing the Jersey application at BASE_URI
//        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
//    }

    /**
     * Main method.
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        final Domain domain;
        if(args.length > 0) {
            System.out.println("!!!!!!!!!!!!!!!!!" + args[0]);
            domain = Domain.valueOf(args[0].toUpperCase());
        } else {
            domain = Domain.TEST;
        }

        System.setProperty("DOMAIN", domain.toString());

//        // The port that we should run on can be set into an environment variable
//        // Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        startServer(domain,Integer.valueOf(webPort));
    }

    public static Server startServer(final Domain domain, final Integer webPort) throws Exception {
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
