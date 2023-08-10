package com.asledz.kancelaria_prawnicza.mother;

import com.asledz.kancelaria_prawnicza.domain.Document;

import java.time.Instant;

public class DocumentMother {
    public static Document.DocumentBuilder basic(Instant instant) {
        return Document.builder()
                .title("Survey")
                .cost(100.00)
                .date(instant)
                .paid(true);
    }
}
