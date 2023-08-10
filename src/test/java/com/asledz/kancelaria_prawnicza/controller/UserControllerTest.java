package com.asledz.kancelaria_prawnicza.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.asledz.kancelaria_prawnicza.dto.NewUserDTO;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.asledz.kancelaria_prawnicza.service.UserService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @MockBean
    private Converter converter;

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    /**
     * Method under test: {@link UserController#getUsers(Map)}
     */
    @Test
    void testGetUsers() throws Exception {
        when(userService.getUsers(Mockito.<Map<String, String>>any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(converter.convertDataFromPageToMap(Mockito.<Page<Object>>any())).thenReturn(new HashMap<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    /**
     * Method under test: {@link UserController#updateUser(Long, UserDTO)}
     */
    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(Mockito.<UserDTO>any(), Mockito.<Long>any()))
                .thenReturn(new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>()));
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.put("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult.content(
                objectMapper.writeValueAsString(new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>())));
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":1,\"name\":\"Name\",\"surname\":\"Doe\",\"email\":\"jane.doe@example.org\",\"roles\":[]}"));
    }

    /**
     * Method under test: {@link UserController#addUser(NewUserDTO)}
     */
    @Test
    void testAddUser() throws Exception {
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON);

        NewUserDTO newUserDTO = new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou");
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(newUserDTO));

        UserDTO userDTO = new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>());

        given(userService.addUser(newUserDTO)).willReturn(userDTO);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userDTO)));
    }

    /**
     * Method under test: {@link UserController#deleteUser(Long)}
     */
    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/users/{id}", 1L);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Method under test: {@link UserController#deleteUser(Long)}
     */
    @Test
    void testDeleteUser2() throws Exception {
        doNothing().when(userService).deleteUser(Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/users/{id}", 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

