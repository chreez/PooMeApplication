package com.palme.GroupMeBot.groupme.server.model;

import java.util.List;

import com.google.common.base.Preconditions;


public class OutgoingGroupMeMessage {
    public static final String GENERIC_GROUP_ID = "-1000120";  //Use this to denote that you want to send to the group, not to particular person
    private final MessageType type;
    
    private final String text;
    
    private String source_guid = "GUID";//TODO: figure out what this is.
    private String recipient_id;
    private List<Attachment> attachments;

    public OutgoingGroupMeMessage(final MessageType type, final String text) {
        this.type = Preconditions.checkNotNull(type);
        this.text = Preconditions.checkNotNull(text);
    }
    
    public MessageType getType() {
        return type;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(String recipient_id) {
        this.recipient_id = recipient_id;
    }

    public String getText() {
        return text;
    }

    public String getSource_guid() {
        return source_guid;
    }

    public void setSource_guid(String source_guid) {
        this.source_guid = source_guid;
    }
}
