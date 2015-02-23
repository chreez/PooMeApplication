package com.palme.GroupMeBot.processor.model;

import java.util.List;

import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;

/**
 * A "parsed" message which contains extra information pertaining to the PooProcessor
 *
 */
public class PooMessage extends IncomingGroupMeMessage {
    private boolean reportPooMetrics;

    public PooMessage(final IncomingGroupMeMessage message) {
        super(message.getId(), message.getGroup_id(), message.getCreated_at(), message.getUser_id(), message.getGroup_id(),
                message.getName(), message.getAvatar_url(), message.getText(), message.getSystem(), message.getFavorited_by(), message.getAttachments());
    }

    public boolean isReportPooMetrics() {
        return reportPooMetrics;
    }

    public void setReportPooMetrics(final boolean reportPooMetrics) {
        this.reportPooMetrics = reportPooMetrics;
    }


}