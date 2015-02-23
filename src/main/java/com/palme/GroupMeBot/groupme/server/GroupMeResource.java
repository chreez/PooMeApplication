package com.palme.GroupMeBot.groupme.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("bot_callback")
public interface GroupMeResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void onMessage(final String message);
    
    @GET
    @Consumes(MediaType.TEXT_HTML)
    public String ping();
}
