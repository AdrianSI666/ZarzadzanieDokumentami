package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import org.springframework.stereotype.Component;

@Component
public class DocumentDTOMapper implements DTOMapper<Document, DocumentDTO> {
    @Override
    public DocumentDTO map(Document source) {
        String typeName;
        long typeId;
        if(source.getType() == null){
            typeName = "brak typu";
            typeId = -1;
        } else {
            typeName = source.getType().getName();
            typeId = source.getType().getId();
        }
        return DocumentDTO.builder()
                .id(source.getId())
                .title(source.getTitle())
                .date(source.getDate())
                .cost(source.getCost())
                .paid(source.getPaid())
                .typeId(typeId)
                .typeName(typeName)
                .build();
    }
}
