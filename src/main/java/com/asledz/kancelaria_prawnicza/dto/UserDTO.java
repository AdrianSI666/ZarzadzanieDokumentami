package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

@Builder
public record UserDTO(
        Long id,
        String name,
        String surname,
        String email
) {
}
