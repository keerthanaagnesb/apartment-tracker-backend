package com.apartment.tracker.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Lightweight, Jackson-friendly view of a ChatMessage sent over WebSocket / REST. */
public class ChatMessagePayload {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    public String senderName;
    public String senderFlat;
    public String content;
    public String time;

    public ChatMessagePayload() {
    }

    public ChatMessagePayload(String senderName, String senderFlat, String content, LocalDateTime sentAt) {
        this.senderName = senderName;
        this.senderFlat = senderFlat;
        this.content = content;
        this.time = sentAt.format(TIME_FORMAT);
    }

    public static ChatMessagePayload from(ChatMessage message) {
        User sender = message.getSender();
        String flat = sender.getRole() == Role.RESIDENT ? sender.getFlatNumber() : sender.getRole().name();
        return new ChatMessagePayload(sender.getName(), flat, message.getContent(), message.getSentAt());
    }
}
