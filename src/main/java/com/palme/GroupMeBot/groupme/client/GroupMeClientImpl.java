package com.palme.GroupMeBot.groupme.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Iterables;

import org.glassfish.jersey.client.JerseyClientBuilder;

import com.palme.GroupMeBot.groupme.server.model.Attachment;
import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;
import com.palme.GroupMeBot.groupme.server.model.OutgoingGroupMeMessage;

public class GroupMeClientImpl implements GroupMeClient{
    static private final String GROUP_ME_ENDPOINT = "https://api.groupme.com/v3";

    private final WebTarget target;
    private final String botId;

    public GroupMeClientImpl(final String accessToken) {
        this.botId = accessToken;
        final Client client = JerseyClientBuilder.newClient();
        this.target = client.target(GROUP_ME_ENDPOINT);
    }

    @Override
    public Response sendMessage(final OutgoingGroupMeMessage message) {
        final String path = "/bots/post";

        final Form form = new Form();
        form.param("bot_id", botId);
        form.param("text", message.getText());
        if(message.getAttachments() != null && message.getAttachments().size() >0 ) {
            form.param("picture_url", Iterables.getLast(message.getAttachments()).getUrl());
        } else {
            form.param("picture_url", null);
        }

        final Response response = target.path(path).request().post(Entity.form(form));
        System.out.println(response.toString());
        return response;
    }

    @Override
    public Response likeMessage(final IncomingGroupMeMessage message) {
        System.out.println(message);
        final String path = String.format("/messages/%s/%s/like", message.getGroup_id(), message.getId());
        final Form form = new Form();
        form.param("bot_id", botId);
        final Response response = target.path(path).request().post(Entity.form(form));
        System.out.println(response.toString());
        return response;
    }


    @Override
    public Response sendMessage(final String message) {
        final String path = "/bots/post";

        final Form form = new Form();
        form.param("bot_id", botId);
        form.param("text", message);
        form.param("picture_url", null);

        final Response response = target.path(path).request().post(Entity.form(form));
        System.out.println(response.toString());
        return response;
    }
}
