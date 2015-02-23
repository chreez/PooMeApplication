package com.palme.GroupMeBot.processor;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.palme.GroupMeBot.apps.QuestionType;
import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.MessageType;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;

public class WillProcessor extends AbstractProcessor<IncomingGroupMeMessage> {
    final static List<String> derogatoryComments = ImmutableList.of("%s, please shut the fuck up.",
            "Shut the fuck up, %s.",
            "How many times do I have to tell you to shut the fuck up, %s?",
            "SHUT UP!",
            "%s, shadup pls.",
            "I kind of wish you would just stop talking forever %s.",
            "Kill yourself, %s.",
            "%s... I am going to murder you in your sleep.",
            "Don't sleep tonight, %s.",
            "I hate no one else as much as I hate %s.");

    @Override
    public boolean isConsumable(final IncomingGroupMeMessage message) {
        if(message != null && message.getName() != null) {
            return message.getName().toUpperCase().contains("WILL");
        }
        return false;
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
        return isConsumable(message);
    }

    @Override
    Map<String, OutgoingGroupMeMessage> consume(final IncomingGroupMeMessage message) {
        final ImmutableMap.Builder<String, OutgoingGroupMeMessage>  result = ImmutableMap.builder();
        if(shouldInsultUser()) {
            final Random rand = new Random();
            int randomId = rand.nextInt(derogatoryComments.size());
            result.put(OutgoingGroupMeMessage.GENERIC_GROUP_ID,
                    new OutgoingGroupMeMessage(MessageType.POST_TO_GROUP, String.format(derogatoryComments.get(randomId), message.getName())));
        }
        return result.build();
    }

    private boolean shouldInsultUser() {
        final Random rand = new Random();
        int randomId = rand.nextInt(14);
        return randomId < 1;
    }

}
