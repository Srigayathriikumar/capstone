package com.example.TeamResourceAccessManagement.controller;

import com.example.TeamResourceAccessManagement.domain.ChatMessage;
import com.example.TeamResourceAccessManagement.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @GetMapping("/{teamId}/chat/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable Long teamId) {
        List<ChatMessage> messages = chatMessageRepository.findByTeamIdOrderByTimestampAsc(teamId);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/{teamId}/chat/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable Long teamId, @RequestBody Map<String, String> request) {
        ChatMessage message = new ChatMessage();
        message.setTeamId(teamId);
        message.setSender(request.get("sender"));
        message.setMessage(request.get("message"));
        message.setTimestamp(LocalDateTime.now());
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }
}