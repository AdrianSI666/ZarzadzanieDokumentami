package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import org.springframework.stereotype.Component;

@Component
public class RoleDTOMapper implements DTOMapper<Role, RoleDTO> {
    @Override
    public RoleDTO map(Role source) {
        return RoleDTO.builder()
                .id(source.getId())
                .name(source.getName())
                .build();
    }
}
