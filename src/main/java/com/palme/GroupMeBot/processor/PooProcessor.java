package com.palme.GroupMeBot.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.palme.GroupMeBot.dao.AchievementsDao;
import com.palme.GroupMeBot.dao.PoopDao;
import com.palme.GroupMeBot.dao.UsersDao;
import com.palme.GroupMeBot.dao.model.PoopInfo;
import com.palme.GroupMeBot.groupme.client.GroupMeClient;
import com.palme.GroupMeBot.groupme.client.UserDetails;
import com.palme.GroupMeBot.groupme.server.model.Attachment;
import com.palme.GroupMeBot.groupme.server.model.AttachmentType;
import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.MessageType;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;
import com.palme.GroupMeBot.handler.PoopHandler;
import com.palme.GroupMeBot.processor.model.PooMessage;

public final class PooProcessor extends AbstractProcessor<PooMessage> {

    final private static List<String> POO_KEY_WORDS  = ImmutableList.of("🍫", "💩");
    private final PoopHandler pooHandler;
    private final GroupMeClient client;

    public PooProcessor(final GroupMeClient client, final Connection jdbcConnection) throws SQLException {
        this.pooHandler = new PoopHandler(new PoopDao(jdbcConnection),
                new AchievementsDao(), new UsersDao(jdbcConnection));
        this.client = client;
    }

    @Override
    public boolean consideredExclusive() {
        return true;
    }

    @Override
    public PooMessage parse(final IncomingGroupMeMessage message) {
        final PooMessage pooMessage = new PooMessage(message);

        final String text = pooMessage.getText();
        String keywordFound = POO_KEY_WORDS.get(1);
        for(final String keyword : POO_KEY_WORDS) {
            if(text.contains(keyword)) {
                keywordFound = keyword;
                break;
            }
        }

        final Iterable<String> parsedResult = Splitter
                .on(keywordFound)
                .omitEmptyStrings()
                .split(text);
        Integer consistency = null;
        for(final String token : parsedResult) {
            try{
                consistency = Integer.valueOf(token.trim());
                if(consistency < 0 || consistency >= 8) {
                    consistency = null;
                }
                break;
            } catch(NumberFormatException | NullPointerException e) {
                continue;
            }
        }

        pooMessage.setConsistency(consistency);
        pooMessage.setReportPooMetrics(text.toUpperCase().contains("STATUS"));
        pooMessage.setRequestedLeaderboard(text.toUpperCase().contains("LEADERBOARD"));
        pooMessage.setRequestedPooTypeTable(text.toUpperCase().contains("HELP"));
        System.out.println("Showing stats?"+ pooMessage.isReportPooMetrics());
        System.out.println("Showing poo type table?"+ pooMessage.isRequestedPooTypeTable());
        System.out.println("Showing leaderboard?"+ pooMessage.isRequestedLeaderboard());
        return pooMessage;
    }

    @Override
    public boolean validate(final PooMessage message) {
        return true;
    }

    @Override
    public Map<String, OutgoingGroupMeMessage> consume(final PooMessage message) {
        final UserDetails userDetails = message.getUserInfoFromMessage();
        client.likeMessage(message);
        try {
            if(message.isReportPooMetrics()) {
                return getMessageToGroup(pooHandler.getUserStatus(userDetails));
            } else if(message.isRequestedPooTypeTable()) {
                final OutgoingGroupMeMessage outgoingMessage = new OutgoingGroupMeMessage(MessageType.POST_TO_GROUP, "In order to register a poop, it is recommended that you use the "
                        + POO_KEY_WORDS.get(1) +
                        " emoji and follow it with a single digit number which counts as consistency. Use this chart as a guide to determine what conistency your poop is. Now fuck off.");
                final Attachment image = new Attachment();
                image.setType(AttachmentType.image);
                image.setUrl("http://i.imgur.com/OVbkCP6.png");
                outgoingMessage.setAttachments(ImmutableList.of(image));
                System.out.println(outgoingMessage);
                return ImmutableMap.of(OutgoingGroupMeMessage.GENERIC_GROUP_ID, outgoingMessage);
            } else if(message.isRequestedLeaderboard()) {
                return getMessageToGroup(Optional.of(pooHandler.getLeaderBoard()));
            } else {
                return getMessageToGroup(pooHandler.handleThePoop(userDetails, new PoopInfo(Integer.valueOf(userDetails.getId()), message.getConsistency(), Instant.now())));
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
    }


}
