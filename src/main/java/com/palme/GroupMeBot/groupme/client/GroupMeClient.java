package com.palme.GroupMeBot.groupme.client;

import javax.ws.rs.core.Response;

public interface GroupMeClient {
    Response sendMessage(final String message);
}
