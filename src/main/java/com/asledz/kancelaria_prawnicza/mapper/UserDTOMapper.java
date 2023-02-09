package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper implements DTOMapper<User, UserDTO> {
    @Override
    public UserDTO map(User source) {
        return UserDTO.builder()
                .id(source.getId())
                .name(source.getName())
                .surname(source.getSurname())
                .email(source.getEmail())
                .build();
    }
}
