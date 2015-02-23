package com.palme.GroupMeBot.processor;

import java.util.Map;

import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;

public class WillProcessor extends AbstractProcessor<IncomingGroupMeMessage> {

    @Override
    public Boolean isConsumable(final IncomingGroupMeMessage message) {
        return null;
    }

    @Override
    public Boolean consideredExclusive() {
        return false;
    }

    @Override
    IncomingGroupMeMessage parse(final IncomingGroupMeMessage message) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    Boolean validate(final IncomingGroupMeMessage message) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    Map<String, OutgoingGroupMeMessage> consume(final IncomingGroupMeMessage message) {
        // TODO Auto-generated method stub
        return null;
    }

}
