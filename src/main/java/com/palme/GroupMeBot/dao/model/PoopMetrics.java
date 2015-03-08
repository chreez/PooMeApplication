package com.palme.GroupMeBot.dao.model;

import java.util.SortedSet;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class PoopMetrics implements Comparable<PoopMetrics>{
    private Integer userId;
    private Integer pooCount;
    private Float consistencyAvg;
    private Integer frequencyAvgMillis;

    public PoopMetrics updateMetrics(final SortedSet<PoopInfo> poops) {
        int poopCount = 0;
        int poopCountsWithConsistency = 0;
        float consistencyTotal = 0;
        int totalMillisBetweenEachPoop = 0;
        PoopInfo lastPoop = null;
        for(final PoopInfo poop: poops) {
            poopCount++;
            if(poop.getConsistency() !=null && !Integer.valueOf(0).equals(poop.getConsistency())) {
                poopCountsWithConsistency++;
                consistencyTotal += poop.getConsistency();
            }else {
            }
            if(lastPoop != null) {
                totalMillisBetweenEachPoop += (poop.getCreationDate().getMillis() - lastPoop.getCreationDate().getMillis());
            }
            lastPoop = poop;
        }

        final PoopMetrics result = new PoopMetrics();

        result.setConsistencyAvg(consistencyTotal / poopCountsWithConsistency);
        result.setFrequencyAvgMillis(totalMillisBetweenEachPoop / poopCount);
        result.setPooCount(poopCount);
        result.setUserId(lastPoop.getUserId());
        return result;
    }

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

    @Override
    public int compareTo(final PoopMetrics o) {
        return o.getPooCount().compareTo(this.getPooCount());
    }

  @Override
  public int hashCode() {
      return Objects.hashCode(this.consistencyAvg, this.frequencyAvgMillis, this.pooCount, this.userId);
  }

  @Override
  public boolean equals(final Object obj) {
      if(obj == null) {
          return false;
      }
      if(obj.getClass() != this.getClass()) {
          return false;
      }

      final PoopMetrics other = (PoopMetrics) obj;

      return Objects.equal(other.getConsistencyAvg(), consistencyAvg)
              && Objects.equal(other.getFrequencyAvgMillis(), frequencyAvgMillis)
              && Objects.equal(other.getPooCount(), pooCount)
              && Objects.equal(other.getUserId(), userId);
  }

  @Override
  public String toString() {
      return Objects.toStringHelper(this)
              .add("cons avg", consistencyAvg)
              .add("frequencyAvgMillis", frequencyAvgMillis)
              .add("pooCount", pooCount)
              .add("userId", userId).toString();
  }
}
