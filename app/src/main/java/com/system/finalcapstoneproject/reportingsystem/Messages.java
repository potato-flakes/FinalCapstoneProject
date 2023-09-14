package com.system.finalcapstoneproject.reportingsystem;

public class Messages {
    private int id; // Unique identifier for the message
    private String sender;
    private String message;

    public Messages(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
