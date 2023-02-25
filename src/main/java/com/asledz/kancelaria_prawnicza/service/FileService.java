package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    public void addFile(MultipartFile multipartFile) {
        try {
            byte[] bytesOfFile = multipartFile.getBytes();
            String fileName = multipartFile.getOriginalFilename();
            if (fileName != null) {
                fileName = StringUtils.cleanPath(fileName);
            } else {
                fileName = "No name";
            }
            File file = File.builder()
                    .extension(multipartFile.getContentType())
                    .content(bytesOfFile)
                    .document(Document.builder()
                            .title(fileName)
                            .build())
                    .build();
            log.info("Saving file");
            fileRepository.save(file);
        } catch (IOException e) {
            throw new BadRequestException("Couldn't read file." + e.getMessage());
        }
    }
}
