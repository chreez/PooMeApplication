package com.palme.GroupMeBot.apps;

import java.util.Random;

public enum QuestionType {
    FULL_QUESTION(1),
    TWO_ITEMS(2);
    
    final int id;
    
    private QuestionType(final int id) {
        this.id = id;
    }
    
    public static QuestionType getRandomQuestionType() {
        final Random rand = new Random();
        int randomId = rand.nextInt(QuestionType.values().length) +1;
        return QuestionType.getEnum(randomId);
    }
    
    public static QuestionType getEnum(final int id) {
        for(final QuestionType type: QuestionType.values()) {
            if(type.getId() == id) {
                return type;
            } 
        }
        throw new IllegalArgumentException("Bad id of " + id);
    }

    private int getId() {
        return this.id;
    }
}
