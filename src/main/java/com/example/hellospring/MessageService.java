package com.example.hellospring;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getLatestMessagesNPlus1() {
        return messageRepository.findTop30ByOrderByCreatedAtDesc().stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getLatestMessagesOptimized() {
        return messageRepository.findTop30ByContentIsNotNullOrderByCreatedAtDesc().stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MessageResponse getMessage(long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message " + id + " not found"));
        return MessageResponse.from(message);
    }

    @Transactional
    public MessageResponse create(CreateMessageRequest request) {
        validateRequest(request);
        String username = request.username().trim();
        User author = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(new User(username)));
        Message saved = messageRepository.save(new Message(request.content().trim(), author));
        return MessageResponse.from(saved);
    }

    private void validateRequest(CreateMessageRequest request) {
        if (request == null || request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Message content must not be empty");
        }
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }
    }
}
