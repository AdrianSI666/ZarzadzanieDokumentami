package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DocumentDTO(
        Long id,
        String title,
        Instant date,
        Double cost,
        Boolean paid,
        Long typeId,
        String typeName

) {
}
