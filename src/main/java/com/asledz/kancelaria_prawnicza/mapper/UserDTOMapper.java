package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper implements DTOMapper<User, UserDTO> {
    @Override
    public UserDTO map(User source) {
        Long id = source.getId();
        String name = source.getName();
        String surname = source.getSurname();
        String email = source.getEmail();
        return new UserDTO(id, name, surname, email);
    }
}
