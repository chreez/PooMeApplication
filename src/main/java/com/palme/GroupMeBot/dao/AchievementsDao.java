package com.palme.GroupMeBot.dao;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.palme.GroupMeBot.dao.model.PoopInfo;
import com.palme.GroupMeBot.dao.model.PoopMetrics;

public class AchievementsDao {
    final private static List<String> congratsTerms = ImmutableList.of("Woot! ", "Congrats! ", "Good work! ", "Nice shitting bro! ", "Good movement! ");
    final private static List<String> sorryTerms = ImmutableList.of("Bad shitting bro.. ", "My condolences. ", "I feel bad for that toilet. ",
            "You sick bro? ", "Have you seen a doctor about this? ", "It is people like you that make me sick. ");
    final private static Random random = new Random();

    public String getAchievementForPoopMetrics(final PoopInfo lastPoop, final PoopMetrics poopMetrics) {
        if(poopMetrics.getPooCount() == 1) {
            return getCongratsTerm() + "That was your first poop ever recorded!";
        }

        if(poopMetrics.getPooCount() % 5  == 0) {
            return getCongratsTerm() + String.format("You've pooped %d times so far!", poopMetrics.getPooCount());
        }

        System.out.println(lastPoop + "asdasd");
        if(lastPoop.getConsistency() == 7) {
            return getNotCongratsTerm() + String.format("Maybe you should try more fibrez in your diet.");
        }

        if(lastPoop.getConsistency() == 1) {
            return getNotCongratsTerm() + String.format("Maybe you should try some laxatives.");
        }

        return null;
    }

    private String getNotCongratsTerm() {
        return sorryTerms.get(random.nextInt(sorryTerms.size()));
    }

    final String getCongratsTerm() {
        return congratsTerms.get(random.nextInt(congratsTerms.size()));
    }

}
