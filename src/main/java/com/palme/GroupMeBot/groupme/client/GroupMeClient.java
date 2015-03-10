package com.palme.GroupMeBot.groupme.client;

import javax.ws.rs.core.Response;

import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;

public interface GroupMeClient {
    Response sendMessage(final String message);
    Response sendMessage(final OutgoingGroupMeMessage message);
    Response likeMessage(final IncomingGroupMeMessage message);
}
