package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import com.asledz.kancelaria_prawnicza.dto.TypeDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoleControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private String token;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "user2@example.com");
        body.put("password", "testPassword");
        this.mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(body)))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    Map<String, String> map = objectMapper.readValue(content, new TypeReference<>() {
                    });
                    token = map.get("access_token");
                });
    }

    @Test
    void testGetRoles() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        String content = this.mockMvc.perform(get("/roles").headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Map<String, Object> map = objectMapper.readValue(content, new TypeReference<>() {
        });
        List<Map> result = (List<Map>) map.get("data");
        assertEquals(5, result.size());
    }
}
