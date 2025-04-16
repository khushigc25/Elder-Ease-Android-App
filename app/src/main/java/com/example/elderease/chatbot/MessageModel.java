package com.example.elderease.chatbot;

public class MessageModel {
    private String message;
    private boolean isBot; // true for bot messages, false for user messages

    public MessageModel(String message, boolean isBot) {
        this.message = message;
        this.isBot = isBot;
    }

    public String getMessage() {
        return message;
    }

    public boolean isBot() {
        return isBot;
    }
}
