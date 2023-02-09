package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

@Builder
public record TagDTO(
        Long id,
        String name
) {
}
