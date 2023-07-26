package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDTO(
        Long id,
        String name,
        String surname,
        String email,
        List<String> roles
) {
}
