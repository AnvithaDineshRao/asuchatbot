package com.example.rohit.asuchatbot.model;

/**
 * Created by Rohit on 03-05-2018.
 */

public class ChatMessage {
    private String message;
    private int sender;

    public ChatMessage(String message, int sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
}
