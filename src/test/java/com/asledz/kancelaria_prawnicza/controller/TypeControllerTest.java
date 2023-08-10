package com.asledz.kancelaria_prawnicza.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.asledz.kancelaria_prawnicza.dto.TypeDTO;
import com.asledz.kancelaria_prawnicza.service.TypeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {TypeController.class})
@ExtendWith(SpringExtension.class)
class TypeControllerTest {
    @Autowired
    private TypeController typeController;

    @MockBean
    private TypeService typeService;

    /**
     * Method under test: {@link TypeController#addType(TypeDTO)}
     */
    @Test
    void testAddType() throws Exception {

        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/types")
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        TypeDTO typeDTO = new TypeDTO(null, "Name");
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(typeDTO));

        TypeDTO typeDTO2 = new TypeDTO(2L, "Name");
        given(typeService.addType(typeDTO)).willReturn(typeDTO2);
        
        MockMvcBuilders.standaloneSetup(typeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(typeDTO2)));
    }

    /**
     * Method under test: {@link TypeController#getTypes()}
     */
    @Test
    void testGetTypes() throws Exception {
        when(typeService.getTypes()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/types");
        MockMvcBuilders.standaloneSetup(typeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Method under test: {@link TypeController#getTypes()}
     */
    @Test
    void testGetTypes2() throws Exception {
        when(typeService.getTypes()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/types");
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(typeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }
}

