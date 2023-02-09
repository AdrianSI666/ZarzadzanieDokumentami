package com.asledz.kancelaria_prawnicza.dto;

public record UserDTO(
        Long id,
        String name,
        String surname,
        String email
) {
}
