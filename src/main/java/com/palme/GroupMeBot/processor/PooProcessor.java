package com.palme.GroupMeBot.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.palme.GroupMeBot.dao.AchievementsDao;
import com.palme.GroupMeBot.dao.PoopDao;
import com.palme.GroupMeBot.dao.UsersDao;
import com.palme.GroupMeBot.dao.model.PoopInfo;
import com.palme.GroupMeBot.groupme.client.GroupMeClient;
import com.palme.GroupMeBot.groupme.client.UserDetails;
import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.MessageType;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;
import com.palme.GroupMeBot.handler.PoopHandler;
import com.palme.GroupMeBot.processor.model.PooMessage;

public final class PooProcessor extends AbstractProcessor<PooMessage> {

    final private static List<String> POO_KEY_WORDS  = ImmutableList.of("🍫", "💩");
    private final GroupMeClient client;
    private final PoopHandler pooHandler;

    public PooProcessor(final GroupMeClient client, final Connection jdbcConnection) throws SQLException {
        this.client = client;
        this.pooHandler = new PoopHandler(new PoopDao(jdbcConnection),
                new AchievementsDao(), new UsersDao(jdbcConnection));
    }

    @Override
    public boolean consideredExclusive() {
        return true;
    }

    @Override
    public PooMessage parse(final IncomingGroupMeMessage message) {
        final PooMessage pooMessage = new PooMessage(message);

        final String text = pooMessage.getText();
        final String keywordFound;
        for(final String keyword : POO_KEY_WORDS) {
            if(text.contains(keyword)) {
                keywordFound = keyword;
                break;
            }
        }
        pooMessage.setReportPooMetrics(text.toUpperCase().contains("STATUS"));
        System.out.println("Showing stats?"+ pooMessage.isReportPooMetrics());
        return pooMessage;
    }

    @Override
    public boolean validate(final PooMessage message) {
        return true;
    }

    @Override
    public Map<String, OutgoingGroupMeMessage> consume(final PooMessage message) {
        final UserDetails userDetails = message.getUserInfoFromMessage();
        try {
            if(message.isReportPooMetrics()) {
                return getMessageToGroup(pooHandler.getUserStatus(userDetails));
            } else {
                return getMessageToGroup(pooHandler.handleThePoop(userDetails, new PoopInfo(Integer.valueOf(userDetails.getId()), 5, Instant.now())));
            }
        } catch (SQLException e) {
            return ImmutableMap.of();
        }
    }

    private Map<String, OutgoingGroupMeMessage> getMessageToGroup(final Optional<String> userStatus) {
        if(userStatus.isPresent()) {
            return ImmutableMap.of(OutgoingGroupMeMessage.GENERIC_GROUP_ID, new OutgoingGroupMeMessage(MessageType.POST_TO_GROUP, userStatus.get()));
        } else {
            System.out.println("No message present, not sending shit");
            return ImmutableMap.of();
        }
    }

    @Override
    public boolean isConsumable(final IncomingGroupMeMessage message) {
        String text = message.getText();
        if(text == null || text.isEmpty()) {
            return false;
        }
        boolean containsKeyWord = false;
        for(final String keyword : POO_KEY_WORDS) {
            if(text.contains(keyword)) {
                containsKeyWord = true;
                break;
            }
        }

        if(containsKeyWord == false) {
            return false;
        }


        return true;

        //TODO confirm a number detailing consistency

//        return POO_KEY_WORDS.contains(message.getText());
    }


}
