package com.asledz.kancelaria_prawnicza.controller;

import static org.mockito.Mockito.when;

import com.asledz.kancelaria_prawnicza.service.RoleService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {RoleController.class})
@ExtendWith(SpringExtension.class)
class RoleControllerTest {
    @MockBean
    private Converter converter;

    @Autowired
    private RoleController roleController;

    @MockBean
    private RoleService roleService;

    /**
     * Method under test: {@link RoleController#getRoles(Integer)}
     */
    @Test
    void testGetRoles() throws Exception {
        when(roleService.getRoles(Mockito.<Integer>any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(converter.convertDataFromPageToMap(Mockito.<Page<Object>>any())).thenReturn(new HashMap<>());
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/roles");
        MockHttpServletRequestBuilder requestBuilder = getResult.param("page", String.valueOf(1));
        MockMvcBuilders.standaloneSetup(roleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }
}

