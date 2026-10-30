package com.example.hellospring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HelloSpringApplicationTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private MessageRepository messageRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void resetDatabase() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createsMessageAndReusesAuthor() throws Exception {
        create("Первое", "Arseniy");
        create("Второе", "Arseniy");

        org.junit.jupiter.api.Assertions.assertEquals(1, userRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(2, messageRepository.count());
    }

    @Test
    void optimizedEndpointReturnsDtoWithAuthor() throws Exception {
        create("Привет, JPA!", "Arseniy");

        mockMvc.perform(get("/api/messages/optimized").contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Привет, JPA!"))
                .andExpect(jsonPath("$[0].authorUsername").value("Arseniy"));
    }

    @Test
    void dtoEndpointDoesNotExposeLazyEntity() throws Exception {
        create("Безопасный DTO", "Arseniy");
        long id = messageRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/messages/fail/{id}", id).contextPath("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorUsername").value("Arseniy"))
                .andExpect(jsonPath("$.author").doesNotExist());
    }

    @Test
    void validatesPayloadAndMissingMessage() throws Exception {
        mockMvc.perform(post("/api/messages").contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\" \",\"username\":\"Arseniy\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/messages/fail/999999").contextPath("/api"))
                .andExpect(status().isNotFound());
    }

    private void create(String content, String username) throws Exception {
        mockMvc.perform(post("/api/messages").contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"" + content + "\",\"username\":\"" + username + "\"}"))
                .andExpect(status().isCreated());
    }
}
