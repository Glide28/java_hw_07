package com.example.hellospring;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String content,
        String authorUsername,
        LocalDateTime createdAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getAuthor().getUsername(),
                message.getCreatedAt());
    }
}
