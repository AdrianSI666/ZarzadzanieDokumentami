package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

@Builder
public record RoleDTO(
        Long id,
        String name
) {
}
