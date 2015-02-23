package com.palme.GroupMeBot.groupme.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;

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
    
    public Response sendMessage(String message) {
        final String path = "/bots/post";
        
        final Form form = new Form();
        form.param("bot_id", botId);
        form.param("text", message);
        form.param("picture_url", null);

        final Response response = target.path(path).request().post(Entity.form(form));
        System.out.println(response.toString());
        return response;
    }
    
    public Response sendMessage(OutgoingGroupMeMessage message) {
        final String path = "/bots/post";
        
        final Form form = new Form();
        form.param("bot_id", botId);
        form.param("text", message.getText());
        form.param("picture_url", null);

        final Response response = target.path(path).request().post(Entity.form(form));
        System.out.println(response.toString());
        return response;
    }
}
