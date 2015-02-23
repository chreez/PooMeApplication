package com.palme.GroupMeBot.processor;

import java.util.Map;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;

/**
 * Abstract processor class, should take in a string that is parsable by the
 * processor
 *
 * @author Chris
 *
 */
public abstract class AbstractProcessor <T extends IncomingGroupMeMessage> {

    /**
     * Given a message from the user, parse the message and return an optional
     * response
     *
     * @param message
     * @return
     */
    public final Map<String, OutgoingGroupMeMessage> process(final IncomingGroupMeMessage message) {
        final T parsedMessage = this.parse(message);
        if(validate(parsedMessage)) {
            return consume(parsedMessage);
        }
        return ImmutableMap.of();
    }

    public abstract boolean isConsumable(final IncomingGroupMeMessage message);
    public abstract boolean consideredExclusive();

    abstract T parse(IncomingGroupMeMessage message);
    abstract boolean validate(T message);
    abstract Map<String, OutgoingGroupMeMessage> consume(T message);
}
