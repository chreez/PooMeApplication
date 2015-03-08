package com.palme.GroupMeBot.processor.model;

import java.util.List;

import com.palme.GroupMeBot.groupme.server.model.IncomingGroupMeMessage;

/**
 * A "parsed" message which contains extra information pertaining to the PooProcessor
 *
 */
public class PooMessage extends IncomingGroupMeMessage {
    private boolean reportPooMetrics;
    private boolean requestedPooTypeTable; //requested to view the table
    private boolean requestedLeaderboard; //requested to view the leaderboard
    private Integer consistency;

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

    public Integer getConsistency() {
        return consistency;
    }

    public void setConsistency(final Integer consistency) {
        this.consistency = consistency;
    }


    public boolean isRequestedLeaderboard() {
        return requestedLeaderboard;
    }

    public void setRequestedLeaderboard(final boolean requestedLeaderboard) {
        this.requestedLeaderboard = requestedLeaderboard;
    }

    public boolean isRequestedPooTypeTable() {
        return requestedPooTypeTable;
    }

    public void setRequestedPooTypeTable(boolean requestedPooTypeTable) {
        this.requestedPooTypeTable = requestedPooTypeTable;
    }
}