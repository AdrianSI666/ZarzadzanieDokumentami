package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.enums.FilterAndSortEnum;
import com.asledz.kancelaria_prawnicza.enums.SortEnum;
import com.asledz.kancelaria_prawnicza.search.Reindexer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DocumentControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private String token;
    private static List<DocumentDTO> documents;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault());
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");

        String stringDate1 = "2023-01-15 14:30:00";
        String stringDate2 = "2023-02-20 09:45:00";
        String stringDate3 = "2023-03-10 17:15:00";
        String stringDate4 = "2023-04-05 11:00:00";
        String stringDate5 = "2023-05-22 13:20:00";

        documents = List.of(
                DocumentDTO.builder().id(1L).cost(19.99)
                        .date(LocalDateTime.parse(stringDate1, dateTimeFormatter).atZone(zoneId).toInstant())
                        .paid(true).title("Document 1").typeId(1L).build(),
                DocumentDTO.builder().id(2L).cost(24.99)
                        .date(LocalDateTime.parse(stringDate2, dateTimeFormatter).atZone(zoneId).toInstant())
                        .paid(false).title("Document 2").typeId(2L).build(),
                DocumentDTO.builder().id(3L).cost(14.99)
                        .date(LocalDateTime.parse(stringDate3, dateTimeFormatter).atZone(zoneId).toInstant())
                        .paid(true).title("Document 3").typeId(3L).build(),
                DocumentDTO.builder().id(4L).cost(29.99)
                        .date(LocalDateTime.parse(stringDate4, dateTimeFormatter).atZone(zoneId).toInstant())
                        .paid(false).title("Document 4").typeId(4L).build(),
                DocumentDTO.builder().id(5L).cost(9.99)
                        .date(LocalDateTime.parse(stringDate5, dateTimeFormatter).atZone(zoneId).toInstant())
                        .paid(true).title("Document 5").typeId(5L).build());
    }

    @BeforeEach
    public void setup() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "user2@example.com");
        body.put("password", "testPassword");
        ObjectMapper objectMapper = new ObjectMapper();
        this.mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(body)))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    Map<String, String> map = objectMapper.readValue(content, new TypeReference<>() {
                    });
                    token = map.get("access_token");
                });
    }

    @Test
    void testGetDocumentsSortedByPaidAndCost() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        String content = this.mockMvc.perform(get("/documents")
                        .param(SortEnum.SORT_PAID.value, "false")
                        .param(SortEnum.SORT_COST.value, "false")
                        .headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> map = objectMapper.readValue(content, new TypeReference<>() {
        });
        List<LinkedHashMap> result = (List<LinkedHashMap>) map.get("data");
        assertEquals(5, result.size());
        assertEquals(documents.get(1).cost(), result.get(0).get("cost"));
        assertEquals(false, result.get(0).get("paid"));
        assertEquals(documents.get(3).cost(), result.get(1).get("cost"));
        assertEquals(false, result.get(1).get("paid"));
        assertEquals(documents.get(4).cost(), result.get(2).get("cost"));
        assertEquals(true, result.get(2).get("paid"));
        assertEquals(documents.get(2).cost(), result.get(3).get("cost"));
        assertEquals(true, result.get(3).get("paid"));
        assertEquals(documents.get(0).cost(), result.get(4).get("cost"));
        assertEquals(true, result.get(4).get("paid"));
    }

    @Test
    void testGetDocumentById() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        String content = this.mockMvc.perform(get("/documents/2")
                        .headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        DocumentDTO document = objectMapper.readValue(content, DocumentDTO.class);
        assertEquals(documents.get(1).cost(), document.cost());
        assertEquals(documents.get(1).paid(), document.paid());
        assertEquals(documents.get(1).title(), document.title());
        assertEquals(documents.get(1).date(), document.date());
    }

    /**
     * This test requires build index which cannot be built during tests. This can be achieved by running
     * {@link Reindexer reindexer} on test database, but beware that this will erase index for running database, so it
     * will need to be reindex too.
     *
     * @throws Exception
     */
    @Test
    void testGetDocumentsByParameters() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        String content = this.mockMvc.perform(get("/documents/by")
                        .param(FilterAndSortEnum.FILTER_COST_TO.value, "15.00")
                        .param(FilterAndSortEnum.FILTER_PAID.value, "true")
                        .param(SortEnum.SORT_COST.value, "false")
                        .headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> map = objectMapper.readValue(content, new TypeReference<>() {
        });
        List<LinkedHashMap> result = (List<LinkedHashMap>) map.get("data");
        //This assertion will fail. To make it work read note above test name.
        assertEquals(2, result.size());
        assertEquals(documents.get(4).cost(), result.get(0).get("cost"));
        assertEquals(true, result.get(0).get("paid"));
        assertEquals(documents.get(2).cost(), result.get(1).get("cost"));
        assertEquals(true, result.get(1).get("paid"));
    }

    @Test
    void testGetUserDocumentsWithoutDate() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        String content = this.mockMvc.perform(get("/documents/user/{id}/withoutDate", 2)
                        .headers(requestHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<DocumentDTO> result = objectMapper.readValue(content, new TypeReference<>() {
        });
        assertEquals(0, result.size());
    }

    @Test
    void testUpdateDocument() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.put("/documents/2")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        DocumentDTO newDocumentDataDTO = DocumentDTO.builder()
                .title("testTitle")
                .paid(true)
                .cost(500.00)
                .build();

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(newDocumentDataDTO));

        String content = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        DocumentDTO changedDocument = objectMapper.readValue(content, DocumentDTO.class);
        assertEquals(newDocumentDataDTO.title(), changedDocument.title());
        assertEquals(newDocumentDataDTO.paid(), changedDocument.paid());
        assertEquals(newDocumentDataDTO.cost(), changedDocument.cost());
        assertEquals(documents.get(1).date(), changedDocument.date());
        assertEquals(documents.get(1).typeId(), changedDocument.typeId());
    }

    @Test
    void testUpdateDocumentThrowsForbiddenException() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.put("/documents/3")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        DocumentDTO newDocumentDataDTO = DocumentDTO.builder()
                .title("testTitle")
                .paid(true)
                .cost(500.00)
                .build();

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(newDocumentDataDTO));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testDeleteDocument() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.delete("/documents/2")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(contentTypeResult)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteDocumentThrowsForbiddenException() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.setBearerAuth(token);

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.delete("/documents/3")
                .headers(requestHeaders)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(contentTypeResult)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
