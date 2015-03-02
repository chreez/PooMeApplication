package com.palme.GroupMeBot.dao.model;

import org.joda.time.Instant;

import com.google.common.base.Objects;

public class PoopInfo implements Comparable<PoopInfo> {
    private Integer userId;
    private Integer consistency;
    private Instant creationDate;

    public PoopInfo() {
    }
    public PoopInfo(final Integer userId, final Integer consistency, final Instant creationDate) {
        super();
        this.userId = userId;
        this.consistency = consistency;
        this.creationDate = creationDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getConsistency() {
        return consistency;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setUserId(final Integer userId) {
        this.userId = userId;
    }

    public void setConsistency(final Integer consistency) {
        this.consistency = consistency;
    }

    public void setCreationDate(final Instant creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(final PoopInfo other) {
        return this.creationDate.compareTo(other.getCreationDate());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("uid", userId).add("consist", consistency).add("creationdate", creationDate).toString();
    }

}
