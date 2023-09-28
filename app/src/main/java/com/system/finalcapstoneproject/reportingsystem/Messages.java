package com.system.finalcapstoneproject.reportingsystem;

public class Messages {
    private int id; // Unique identifier for the message
    private String sender;
    private String message;
    private String senderProfileImageUrl; // Add this field

    public Messages(String sender, String message, String senderProfileImageUrl) {
        this.sender = sender;
        this.message = message;
        this.senderProfileImageUrl = senderProfileImageUrl;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderProfileImageUrl() {
        return senderProfileImageUrl;
    }
}
