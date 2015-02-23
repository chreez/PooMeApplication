package com.palme.GroupMeBot.groupme.server.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import com.palme.GroupMeBot.groupme.client.UserDetails;

@JsonIgnoreProperties(ignoreUnknown=true)
public class IncomingGroupMeMessage {
    private String id;
    private String source_guid;
    private Long created_at;
    private String user_id;
    private String group_id;
    private String name;
    private String sender_type;
    private String avatar_url;
    private String text;
    private Boolean system;
    private List<String> favorited_by;
    private List<String> attachments;

    public IncomingGroupMeMessage() {}

    public IncomingGroupMeMessage(final String id, final String source_guid,
            final Long created_at, final String user_id, final String group_id, final String name,
            final String avatar_url, final String text, final Boolean system,
            final List<String> favorited_by, final List<String> attachments) {
        super();
        this.id = id;
        this.source_guid = source_guid;
        this.created_at = created_at;
        this.user_id = user_id;
        this.group_id = group_id;
        this.name = name;
        this.avatar_url = avatar_url;
        this.text = text;
        this.system = system;
        this.favorited_by = favorited_by;
        this.attachments = attachments;
    }


    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSource_guid(final String source_guid) {
        this.source_guid = source_guid;
    }

    public void setCreated_at(final Long created_at) {
        this.created_at = created_at;
    }

    public void setUser_id(final String user_id) {
        this.user_id = user_id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setAvatar_url(final String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public void setSystem(final Boolean system) {
        this.system = system;
    }

    public void setAttachments(final List<String> attachments) {
        this.attachments = attachments;
    }

    public String getSource_guid() {
        return source_guid;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getText() {
        return text;
    }

    public Boolean getSystem() {
        return system;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public List<String> getFavorited_by() {
        return favorited_by;
    }

    public void setFavorited_by(final List<String> favorited_by) {
        this.favorited_by = favorited_by;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(final String group_id) {
        this.group_id = group_id;
    }

    public String getSender_type() {
        return sender_type;
    }

    public void setSender_type(final String sender_type) {
        this.sender_type = sender_type;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("id", id)
                .add("source_guid", source_guid).add("created_at", created_at)
                .add("user_id", user_id).add("groupd_id", group_id)
                .add("name", name).add("avatar_url", avatar_url)
                .add("text", text).add("system", system)
                .add("favoritedBy", favorited_by)
                .add("attachments", attachments)
                .add("sender_type", sender_type).toString();
    }

    public UserDetails getUserInfoFromMessage() {
        final UserDetails userDetails = new UserDetails();
        userDetails.setId(this.getUser_id());
        userDetails.setImage_url(this.getAvatar_url());
        userDetails.setName(this.getName());
        return userDetails;
    }
}
