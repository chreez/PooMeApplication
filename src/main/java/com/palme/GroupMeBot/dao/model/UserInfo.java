package com.palme.GroupMeBot.dao.model;

import org.joda.time.Instant;

import com.google.common.base.Objects;

public class UserInfo {
    private Integer userId;
    private String login;
    private Instant lastUpdatedDate;
    private Integer lastPooRowIndex;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(final Integer userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public Instant getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(final Instant lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getLastPooRowIndex() {
        return lastPooRowIndex;
    }

    public void setLastPooRowIndex(final Integer lastPooRowIndex) {
        this.lastPooRowIndex = lastPooRowIndex;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", userId).add("login", login)
                .add("lastUpdatedDate", lastUpdatedDate)
                .add("lastPoo", lastPooRowIndex).toString();
    }
}
