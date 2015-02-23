package com.palme.GroupMeBot.dao.model;

public class PoopMetrics {
    private Integer userId;
    private Integer pooCount;
    private Float consistencyAvg;
    private Integer frequencyAvgMillis;

    public PoopMetrics updateMetrics(final PoopInfo lastPoop, final PoopInfo newPoop) {
        final PoopMetrics result = new PoopMetrics();
        result.setUserId(userId);
        final Integer newPooCount = this.pooCount +1;
        result.setPooCount(newPooCount);
        result.setConsistencyAvg(((this.consistencyAvg * this.pooCount)+newPoop.getConsistency())/newPooCount);
        final Long timeDifferenceMillis;
        if(lastPoop != null) {
            timeDifferenceMillis = newPoop.getCreationDate().getMillis() - lastPoop.getCreationDate().getMillis();
        } else {
            timeDifferenceMillis = new Long(0);
        }
        final Integer newFrequency = ((this.frequencyAvgMillis * this.pooCount) + timeDifferenceMillis.intValue()  / newPooCount);
        result.setFrequencyAvgMillis(newFrequency);
        return result;
    }
    @Override
    public String toString() {
        return userId + pooCount + consistencyAvg + frequencyAvgMillis + "";
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(final Integer userId) {
        this.userId = userId;
    }

    public Integer getPooCount() {
        return pooCount;
    }

    public void setPooCount(final Integer pooCount) {
        this.pooCount = pooCount;
    }

    public Float getConsistencyAvg() {
        return consistencyAvg;
    }

    public void setConsistencyAvg(final Float consistencyAvg) {
        this.consistencyAvg = consistencyAvg;
    }

    public Integer getFrequencyAvgMillis() {
        return frequencyAvgMillis;
    }

    public void setFrequencyAvgMillis(final Integer frequencyAvgMillis) {
        this.frequencyAvgMillis = frequencyAvgMillis;
    }

}
