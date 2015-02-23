package com.palme.GroupMeBot.dao;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.palme.GroupMeBot.dao.model.PoopMetrics;

public class AchievementsDao {
    final private static List<String> congratsTerms = ImmutableList.of("Woot! ", "Congrats! ", "Good work! ", "Nice shitting bro! ", "Good movement! ");
    final private static Random random = new Random();

    public String getAchievementForPoopMetrics(final PoopMetrics poopMetrics) {
        if(poopMetrics.getPooCount() == 1) {
            return getCongratsTerm() + "That was your first poop ever recorded!";
        }

        if(poopMetrics.getPooCount() % 10  == 0) {
            return getCongratsTerm() + String.format("You pooped %d times so far!", poopMetrics.getPooCount());
        }

        return null;
    }

    final String getCongratsTerm() {
        return congratsTerms.get(random.nextInt(congratsTerms.size()));
    }

}
