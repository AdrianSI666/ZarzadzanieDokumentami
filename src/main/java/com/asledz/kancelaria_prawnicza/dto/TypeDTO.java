package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

@Builder
public record TypeDTO(
        Long id,
        String name
) {
}
