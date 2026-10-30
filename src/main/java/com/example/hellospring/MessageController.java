package com.example.hellospring;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping("/nplus1")
    public List<MessageResponse> getMessagesNPlus1() {
        return service.getLatestMessagesNPlus1();
    }

    @GetMapping("/optimized")
    public List<MessageResponse> getMessagesOptimized() {
        return service.getLatestMessagesOptimized();
    }

    @GetMapping("/fail/{id}")
    public MessageResponse getMessageSafely(@PathVariable long id) {
        return service.getMessage(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(@RequestBody CreateMessageRequest request) {
        return service.create(request);
    }
}
