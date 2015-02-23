package com.palme.GroupMeBot.processor.model;

import com.google.common.base.Preconditions;

public class ParsedMessage {
    final String userId;
    final String rawText;
    
    public ParsedMessage(final String userId, final String text) {
        this.userId = Preconditions.checkNotNull(userId, "UserId was null when processing message");
        this.rawText = Preconditions.checkNotNull(text, "Text was null when processing message");
    }
    
    public String getRawText() {
        return rawText;
    }
    
    public String getUserId() {
        return userId;
    }
}
