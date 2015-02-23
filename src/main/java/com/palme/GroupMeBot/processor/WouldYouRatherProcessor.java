package com.palme.GroupMeBot.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.palme.GroupMeBot.apps.WouldYouRatherApp;
import com.palme.GroupMeBot.groupme.client.GroupMeClient;
import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;

public class WouldYouRatherProcessor extends AbstractProcessor<IncomingGroupMeMessage> {
    private static final Logger logger = LoggerFactory.getLogger(WouldYouRatherProcessor.class);
    private WouldYouRatherApp app;
    private Thread workerThread;

    public WouldYouRatherProcessor(final GroupMeClient client) {
        try {
            this.app = new WouldYouRatherApp(client, false, 1);
            this.workerThread = new Thread(app);
            this.workerThread.start();
        } catch (IOException | URISyntaxException e) {
//            this.app = null;
            System.out.println("Failed to start would you rather app.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConsumable(final IncomingGroupMeMessage message) {
        return true;
    }

    @Override
    public boolean consideredExclusive() {
        return false;
    }

    @Override
    IncomingGroupMeMessage parse(final IncomingGroupMeMessage message) {
        return message;
    }

    @Override
    boolean validate(final IncomingGroupMeMessage message) {
        return true;
    }

    @Override
    Map<String, OutgoingGroupMeMessage> consume(final IncomingGroupMeMessage message) {
        logger.info("Refreshing heartbeat of message {}");
        if(this.app == null) {
            logger.warn("APP IS NULL");
        }
        this.app.refreshHeartbeat();
        return ImmutableMap.of();
    }

}
