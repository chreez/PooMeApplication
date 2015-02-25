package com.palme.GroupMeBot.groupme.server.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.palme.GroupMeBot.application.Domain;
import com.palme.GroupMeBot.groupme.client.GroupMeClient;
import com.palme.GroupMeBot.groupme.client.GroupMeClientImpl;
import com.palme.GroupMeBot.groupme.server.GroupMeResource;
import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;
import com.palme.GroupMeBot.processor.AbstractProcessor;
import com.palme.GroupMeBot.processor.PooProcessor;
import com.palme.GroupMeBot.processor.WillProcessor;
import com.palme.GroupMeBot.processor.WouldYouRatherProcessor;

@Path("bot_callback")
@Singleton
public class GroupMeResourceImpl implements GroupMeResource {
    public static final Map<Domain, String> DOMAIN_TO_TOKEN_ID = ImmutableMap.<Domain, String>builder()
            .put(Domain.TEST, "0d50f452e40c703311be39ac76")
            .put(Domain.PROD, "f4ba8772ffd16d002ccd6394a8")
            .build();

    private List<AbstractProcessor<?>> processors;
    private GroupMeClient client;
////    private final int bootStrapCode;
//    private final String BOT_USER_ID = null;

    public GroupMeResourceImpl() throws SQLException, ClassNotFoundException, URISyntaxException {
        final Domain domain = Domain.valueOf(System.getenv("DOMAIN"));

        if(client == null) {
            System.out.println("init client");
            client = new GroupMeClientImpl(DOMAIN_TO_TOKEN_ID.get(domain));
        }else {
            System.out.println("Client already init");
        }
        if(processors == null) {
            System.out.println(" init processors");
            Class.forName("org.sqlite.JDBC");
//            final Connection connection =   getConnection(domain);
//            new WouldYouRatherProcessor(client),
//            new PooProcessor(client, connection),
            processors = ImmutableList.of(new WillProcessor());
        } else {
            System.out.println("processors already init");
        }

//        this.bootStrapCode = new Random().nextInt(100);
//        client.sendMessage(bootStrapCode + ": Bootstrapping bot by registering this number.. Disregard.");
    }

    public Connection getConnection(final Domain domain) throws URISyntaxException, SQLException {
//        if(Domain.TEST.equals(domain)) {
////            return DriverManager.getConnection("jdbc:sqlite:GroupMeBotDB.db");
//        }
//        final URI dbUri = new URI(System.getenv("DATABASE_URL"));
//
//        final String username = dbUri.getUserInfo().split(":")[0];
//        System.out.println(username);
//        final String password = dbUri.getUserInfo().split(":")[1];
//        System.out.println(password);
//        System.out.println(dbUri.getHost() );
//        System.out.println(dbUri.getPath() );
//        final String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
//
//        return DriverManager.getConnection("jdbc:" + System.getenv("DATABASE_URL"), username, password);

        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        int port = dbUri.getPort();

        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public void onMessage(final String rawMessage) {
        System.out.println(rawMessage);
        final IncomingGroupMeMessage message;
        try{
            final ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(rawMessage, IncomingGroupMeMessage.class);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(message.getText());
        if(isRobot(message.getSender_type())) {
            return;
        }

        final ImmutableMultimap.Builder<String, OutgoingGroupMeMessage> outGoingMessages = ImmutableMultimap.builder();
        for(AbstractProcessor<?> processor : processors) {
            System.out.println(processor.getClass());
            if(processor.isConsumable(message)) {
                final Map<String, OutgoingGroupMeMessage> tempMessages = processor.process(message);
                for(final Entry<String, OutgoingGroupMeMessage> entry : tempMessages.entrySet()) {
                    outGoingMessages.put(entry);
                }
                if(processor.consideredExclusive()) {
                    break;
                }
            }
        }

        for(final Entry<String, OutgoingGroupMeMessage> entry : outGoingMessages.build().entries()) {
            if(OutgoingGroupMeMessage.GENERIC_GROUP_ID.equals(entry.getKey())) {
                client.sendMessage(entry.getValue().getText());
            } else {
                //TODO: Direct message support not avail
            }
        }


    }

    /**
     * is a message from the bot itself
     * @param user_type
     * @return
     */
    private boolean isRobot(final String user_type) {
        return !"user".equals(user_type);
    }



    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Override
    public String ping() {
        return "healthy";
    }
}

