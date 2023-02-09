package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import org.springframework.stereotype.Component;

@Component
public class FileDTOMapper implements DTOMapper<File, FileDTO> {
    @Override
    public FileDTO map(File source) {
        return FileDTO.builder()
                .id(source.getId())
                .extension(source.getExtension())
                .content(source.getContent())
                .build();
    }
}
