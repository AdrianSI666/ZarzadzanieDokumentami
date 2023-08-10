package com.asledz.kancelaria_prawnicza.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.mapper.RoleDTOMapper;
import com.asledz.kancelaria_prawnicza.repository.RoleRepository;

import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RoleServiceTest {
    @Spy
    private RoleDTOMapper dTOMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    /**
     * Method under test: {@link RoleService#getRoles(Integer)}
     */
    @Test
    void testGetRoles() {
        when(roleRepository.findAll(Mockito.<Pageable>any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertTrue(roleService.getRoles(1).toList().isEmpty());
        verify(roleRepository).findAll(Mockito.<Pageable>any());
    }

    /**
     * Method under test: {@link RoleService#addRole(RoleDTO)}
     */
    @Test
    void testAddRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("Name");
        role.setUsers(new ArrayList<>());
        when(roleRepository.save(Mockito.<Role>any())).thenReturn(role);
        RoleDTO roleDTO = new RoleDTO(1L, "Name");

        assertEquals(roleDTO, roleService.addRole(new RoleDTO(1L, "Name")));
        verify(roleRepository).save(Mockito.<Role>any());
        verify(dTOMapper).map(Mockito.<Role>any());
    }
}

