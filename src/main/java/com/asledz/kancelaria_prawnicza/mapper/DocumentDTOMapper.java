package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DocumentDTOMapper implements DTOMapper<Document, DocumentDTO> {
    @Override
    public DocumentDTO map(Document source) {
        Long id = source.getId();
        String title = source.getTitle();
        Instant date = source.getDate();
        Double cost = source.getCost();
        Boolean paid = source.getPaid();
        String typeName = source.getType().getName();
        return new DocumentDTO(id, title, date, cost, paid, typeName);
    }
}
