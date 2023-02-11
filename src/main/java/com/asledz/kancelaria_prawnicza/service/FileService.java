package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FileService {
    private final FileRepository fileRepository;

    private final DTOMapper<File, FileDTO> mapper;

    protected static final String FILE_NOT_FOUND_MSG = "Couldn't find file with id: %d";

    public FileDTO getFileById(Long id) {
        log.info("Getting file with id: %d".formatted(id));
        return mapper.map(fileRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(FILE_NOT_FOUND_MSG, id))
        ));
    }
}
