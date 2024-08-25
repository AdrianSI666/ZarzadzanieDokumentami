package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.utilis.Zipper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FileControllerIntegrationTest {
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
    void testGetFile() throws Exception {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        byte[] bytes = "Hello, World!".getBytes();

        try (MockedStatic<Zipper> zipperMockedStatic = Mockito.mockStatic(Zipper.class)) {
            zipperMockedStatic.when(() -> Zipper.decompress(any()))
                    .thenReturn(bytes);

            this.mockMvc.perform(get("/files/2").headers(requestHeaders))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("application/msword")));
        }
    }

    /**
     * This test will fail if Gotenberg Service isn't running at port 3000
     *
     * @throws Exception
     */
    @Test
    void testAddFile() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.pdf",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.multipart(Path.FILE_VALUE + "s/{id}",
                                2)
                        .file(file)
                        .headers(requestHeaders);

        String content = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated()) //This test will fail if Gotenberg Service isn't running at localhost port 3000
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        assertEquals("Success", content);
    }
}
