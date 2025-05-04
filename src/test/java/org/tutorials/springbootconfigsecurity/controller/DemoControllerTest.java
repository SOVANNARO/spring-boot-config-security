package org.tutorials.springbootconfigsecurity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void testSayHello() throws Exception {
        mockMvc.perform(get("/api/v1/demo-controller"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from secured endpoint"));
    }

    @Test
    void testSayHelloUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/demo-controller"))
                .andExpect(status().isUnauthorized());
    }
}