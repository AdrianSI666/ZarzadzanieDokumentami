package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.NewUserDTO;
import com.asledz.kancelaria_prawnicza.dto.TypeDTO;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
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
class TypeControllerIntegrationTest {

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
    void testGetTypes() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        String content = this.mockMvc.perform(get("/types").headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TypeDTO> result = objectMapper.readValue(content, new TypeReference<>() {
        });
        assertEquals(6, result.size());
    }

    @Test
    void testAddTypes() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = post("/types")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        TypeDTO newTypeDTO = TypeDTO.builder()
                .name("typeTest")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(newTypeDTO));

        String content = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        TypeDTO addedType = objectMapper.readValue(content, TypeDTO.class);
        assertEquals(newTypeDTO.name(), addedType.name());
    }
}
