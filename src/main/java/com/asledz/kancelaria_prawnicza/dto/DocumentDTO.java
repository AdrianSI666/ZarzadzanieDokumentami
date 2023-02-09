package com.asledz.kancelaria_prawnicza.dto;

import java.time.Instant;

public record DocumentDTO(
        Long id,
        String title,
        Instant date,
        Double cost,
        Boolean paid,
        String typeName
) {
}
