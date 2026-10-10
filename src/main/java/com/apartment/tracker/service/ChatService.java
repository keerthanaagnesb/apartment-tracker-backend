package com.apartment.tracker.service;

import com.apartment.tracker.model.ChatMessage;
import com.apartment.tracker.model.User;
import com.apartment.tracker.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(User sender, String content) {
        ChatMessage message = new ChatMessage(sender, content);
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getRecentMessages() {
        return chatMessageRepository.findTop100ByOrderBySentAtAsc();
    }
}
