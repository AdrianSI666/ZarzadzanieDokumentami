package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import org.springframework.stereotype.Component;

@Component
public class FileDTOMapper implements DTOMapper<File, FileDTO> {
    @Override
    public FileDTO map(File source) {
        Long id = source.getId();
        String extension = source.getExtension();
        byte[] content = source.getContent();
        return new FileDTO(id, extension, content);
    }
}
