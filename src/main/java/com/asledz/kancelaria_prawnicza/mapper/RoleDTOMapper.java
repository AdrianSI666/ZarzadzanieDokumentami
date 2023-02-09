package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import org.springframework.stereotype.Component;

@Component
public class RoleDTOMapper implements DTOMapper<Role, RoleDTO> {
    @Override
    public RoleDTO map(Role source) {
        Long id = source.getId();
        String name = source.getName();
        return new RoleDTO(id, name);
    }
}
