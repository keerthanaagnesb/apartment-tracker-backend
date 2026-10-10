package com.apartment.tracker.controller;

import com.apartment.tracker.model.ChatMessagePayload;
import com.apartment.tracker.model.User;
import com.apartment.tracker.service.ChatService;
import com.apartment.tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    /** Initial history load (the live feed afterwards comes over the WebSocket). Any logged-in role. */
    @GetMapping("/api/chat/messages")
    public List<ChatMessagePayload> history() {
        return chatService.getRecentMessages().stream().map(ChatMessagePayload::from).toList();
    }

    /** Client publishes to /app/chat.send; the broadcast goes to /topic/public. */
    @MessageMapping("/chat.send")
    public void send(Map<String, String> payload, Principal principal) {
        User sender = userService.getByEmail(principal.getName());
        String content = payload.get("content");
        if (content == null || content.isBlank()) {
            return;
        }
        var saved = chatService.saveMessage(sender, content.trim());
        messagingTemplate.convertAndSend("/topic/public", ChatMessagePayload.from(saved));
    }
}
