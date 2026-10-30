package com.example.hellospring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleDataInitializer implements CommandLineRunner {
    private final MessageService messageService;
    private final MessageRepository messageRepository;

    public SampleDataInitializer(MessageService messageService, MessageRepository messageRepository) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) {
        if (messageRepository.count() == 0) {
            messageService.create(new CreateMessageRequest("Hello, JPA and Hibernate!", "Alice"));
            messageService.create(new CreateMessageRequest("Spring Data creates repositories automatically.", "Bob"));
            messageService.create(new CreateMessageRequest("DTO protects the REST layer from lazy proxies.", "Alice"));
        }
    }
}
