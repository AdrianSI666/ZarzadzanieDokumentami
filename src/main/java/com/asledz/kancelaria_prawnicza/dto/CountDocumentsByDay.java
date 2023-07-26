package com.asledz.kancelaria_prawnicza.dto;

import java.time.Instant;

public record CountDocumentsByDay(
        Instant date,
        long count
) {
}
