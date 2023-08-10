package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.service.DocumentService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {DocumentController.class})
@ExtendWith(SpringExtension.class)
class DocumentControllerTest {
    @MockBean
    private Converter converter;

    @Autowired
    private DocumentController documentController;

    @MockBean
    private DocumentService documentService;

    /**
     * Method under test: {@link DocumentController#getDocuments(Map)}
     */
    @Test
    void testGetDocuments() throws Exception {
        when(documentService.getDocuments(Mockito.<Map<String, String>>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(converter.convertDataFromPageToMap(Mockito.<Page<Object>>any())).thenReturn(new HashMap<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/documents");
        MockMvcBuilders.standaloneSetup(documentController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    /**
     * Method under test: {@link DocumentController#getDocumentsByParameters(MultiValueMap)}
     */
    @Test
    void testGetDocumentsByParameters() throws Exception {
        when(documentService.getDocumentsByParameters(Mockito.<MultiValueMap<String, String>>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(converter.convertDataFromPageToMap(Mockito.<Page<Object>>any())).thenReturn(new HashMap<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/documents/by");
        MockMvcBuilders.standaloneSetup(documentController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    /**
     * Method under test: {@link DocumentController#updateDocument(Long, DocumentDTO)}
     */
    @Test
    void testUpdateDocument4() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.Instant` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.asledz.kancelaria_prawnicza.dto.DocumentDTO["date"])
        //       at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:77)
        //       at com.fasterxml.jackson.databind.SerializerProvider.reportBadDefinition(SerializerProvider.java:1300)
        //       at com.fasterxml.jackson.databind.ser.impl.UnsupportedTypeSerializer.serialize(UnsupportedTypeSerializer.java:35)
        //       at com.fasterxml.jackson.databind.ser.BeanPropertyWriter.serializeAsField(BeanPropertyWriter.java:728)
        //       at com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeFields(BeanSerializerBase.java:774)
        //       at com.fasterxml.jackson.databind.ser.BeanSerializer.serialize(BeanSerializer.java:178)
        //       at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider._serialize(DefaultSerializerProvider.java:480)
        //       at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:319)
        //       at com.fasterxml.jackson.databind.ObjectMapper._writeValueAndClose(ObjectMapper.java:4568)
        //       at com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString(ObjectMapper.java:3821)
        //   See https://diff.blue/R013 to resolve this issue.

        DocumentService documentService = mock(DocumentService.class);
        when(documentService.updateDocument(Mockito.<DocumentDTO>any(), Mockito.<Long>any())).thenReturn(
                new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d,
                        true, 1L, "Type Name"));
        DocumentController documentController = new DocumentController(documentService, new Converter());
        DocumentDTO documentDTO = new DocumentDTO(1L, "Dr",
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d, true, 1L, "Type Name");

        ResponseEntity<DocumentDTO> actualUpdateDocumentResult = documentController.updateDocument(1L, documentDTO);
        assertEquals(documentDTO, actualUpdateDocumentResult.getBody());
        assertTrue(actualUpdateDocumentResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualUpdateDocumentResult.getStatusCode());
        verify(documentService).updateDocument(Mockito.<DocumentDTO>any(), Mockito.<Long>any());
    }

    /**
     * Method under test: {@link DocumentController#deleteDocument(Long)}
     */
    @Test
    void testDeleteDocument() throws Exception {
        doNothing().when(documentService).deleteDocument(Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/documents/{id}", 1L);
        MockMvcBuilders.standaloneSetup(documentController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
    }

    /**
     * Method under test: {@link DocumentController#deleteDocument(Long)}
     */
    @Test
    void testDeleteDocument2() throws Exception {
        doNothing().when(documentService).deleteDocument(Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/documents/{id}", 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(documentController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
    }

    /**
     * Method under test: {@link DocumentController#getDocument(Long)}
     */
    @Test
    void testGetDocument() throws Exception {
        when(documentService.getDocumentById(Mockito.<Long>any())).thenReturn(new DocumentDTO(1L, "Dr",
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d, true, 1L, "Type Name"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/documents/{id}", 1L);
        MockMvcBuilders.standaloneSetup(documentController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":1,\"title\":\"Dr\",\"date\":0.0,\"cost\":10.0,\"paid\":true,\"typeId\":1,\"typeName\":\"Type Name\"}"));
    }

    /**
     * Method under test: {@link DocumentController#getDocumentsWithoutDate(Long)}
     */
    @Test
    void testGetDocumentsWithoutDate() throws Exception {
        when(documentService.getDocumentsByUserIdWithoutDate(Mockito.<Long>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/documents/user/{id}/withoutDate", 1L);
        MockMvcBuilders.standaloneSetup(documentController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Method under test: {@link DocumentController#getDocumentsWithoutDate(Long)}
     */
    @Test
    void testGetDocumentsWithoutDate2() throws Exception {
        when(documentService.getDocumentsByUserIdWithoutDate(Mockito.<Long>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/documents/user/{id}/withoutDate", 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(documentController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }
}

