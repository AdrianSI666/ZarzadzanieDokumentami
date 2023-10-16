package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.NewUserDTO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private String token;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "user1@example.com");
        body.put("password", "testMPassword");
        this.mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(body)))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    Map<String, String> map = objectMapper.readValue(content, new TypeReference<>() {
                    });
                    token = map.get("access_token");
                });
    }

    @Test
    void testForbidden() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);

        this.mockMvc.perform(get("/users").headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testCantDoAnythingOnUsersIfNotManager() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "user2@example.com");
        body.put("password", "testPassword");
        AtomicReference<String> userToken = new AtomicReference<>();
        this.mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(body)))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    Map<String, String> map = objectMapper.readValue(content, new TypeReference<>() {
                    });
                    userToken.set(map.get("access_token"));
                });

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(userToken.get());

        this.mockMvc.perform(get("/users").headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testGetUsers() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        String content = this.mockMvc.perform(get("/users").headers(requestHeaders))
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

    @Test
    void testAddUser() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = post("/users")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        NewUserDTO newUserDTO = NewUserDTO.builder()
                .name("addTest")
                .surname("addSurnameTest")
                .email("addEmailTest")
                .password("addPasswordTest")
                .build();

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(newUserDTO));

        String content = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        UserDTO addedUser = objectMapper.readValue(content, UserDTO.class);
        assertEquals(newUserDTO.name(), addedUser.name());
        assertEquals(newUserDTO.surname(), addedUser.surname());
        assertEquals(newUserDTO.email(), addedUser.email());
    }

    @Test
    void testUpdateUser() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();

        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.put("/users/5")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        UserDTO newUserDataDTO = UserDTO.builder()
                .name("changedUserName")
                .surname("changedUserNameSurname")
                .email("changedUserNameEmail")
                .build();

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(newUserDataDTO));

        String content = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        UserDTO changedUser = objectMapper.readValue(content, UserDTO.class);
        assertEquals(newUserDataDTO.name(), changedUser.name());
        assertEquals(newUserDataDTO.surname(), changedUser.surname());
        assertEquals(newUserDataDTO.email(), changedUser.email());
    }

    @Test
    void testUpdateUserButNotEmail() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.put("/users/5")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        UserDTO newUserDataDTO = UserDTO.builder()
                .name("changedUserName")
                .surname("changedUserNameSurname")
                .build();

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(newUserDataDTO));

        String content = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        UserDTO changedUser = objectMapper.readValue(content, UserDTO.class);
        assertEquals(newUserDataDTO.name(), changedUser.name());
        assertEquals(newUserDataDTO.surname(), changedUser.surname());
        assertEquals("user5@example.com", changedUser.email());
    }

    @Test
    void testDeleteUser() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.delete("/users/5")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(contentTypeResult)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
