package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import com.asledz.kancelaria_prawnicza.utilis.Zipper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileDTOMapper implements DTOMapper<File, FileDTO> {
    @Override
    public FileDTO map(File source) {
        byte[] decompressed;
        try {
            decompressed = Zipper.decompress(source.getContent());
        } catch (IOException e) {
            throw new BadRequestException("Error while decompressing byte array occurred on file: %d".formatted(source.getDocument().getId()));
        }
        return FileDTO.builder()
                .id(source.getId())
                .name(source.getDocument().getTitle())
                .extension(source.getExtension())
                .content(decompressed)
                .build();
    }
}
