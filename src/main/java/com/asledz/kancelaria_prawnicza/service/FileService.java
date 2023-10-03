package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.FileRepository;
import com.asledz.kancelaria_prawnicza.repository.TypeRepository;
import com.asledz.kancelaria_prawnicza.repository.UserRepository;
import com.asledz.kancelaria_prawnicza.utilis.TextExtractor;
import com.asledz.kancelaria_prawnicza.utilis.Zipper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FileService {
    private final FileRepository fileRepository;
    private final TypeRepository typeRepository;
    private final UserRepository userRepository;
    private final DTOMapper<File, FileDTO> mapper;

    protected static final String FILE_NOT_FOUND_MSG = "Couldn't find file with id: %d";

    public FileDTO getFileById(Long id) {
        log.info("Getting file with id: %d".formatted(id));
        return mapper.map(fileRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(FILE_NOT_FOUND_MSG, id))
        ));
    }

    public void addFile(MultipartFile multipartFile, Long userId) {
        try {
            byte[] bytesOfFile = multipartFile.getBytes();
            String fileName = multipartFile.getOriginalFilename();
            if (fileName == null) {
                fileName = "No name.txt";
            } else if (fileName.isBlank()) {
                fileName = "No name.txt";
            } else {
                fileName = StringUtils.cleanPath(fileName);
            }
            String originalFileName = fileName;
            if (fileName.lastIndexOf(".") != -1) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new NotFoundException("Failed to save file, because couldn't find user with given id: %d".formatted(userId)));
            Type newlyCreatedFileType = typeRepository.findById(0L).orElseThrow(
                    () -> new NotFoundException("Database lacks default type for newly added files"));
            log.info(multipartFile.getContentType());

            String textData = TextExtractor.extractTextFromFile(multipartFile.getInputStream(),
                    multipartFile.getContentType(),
                    originalFileName);
            byte[] compressedBytes = Zipper.compress(bytesOfFile);


            File file = File.builder()
                    .extension(multipartFile.getContentType())
                    .text(textData)
                    .content(compressedBytes)
                    .document(Document.builder()
                            .title(fileName)
                            .type(newlyCreatedFileType)
                            .owner(user)
                            .build())
                    .build();
            log.info("Saving file");
            fileRepository.save(file);
        } catch (IOException e) {
            throw new BadRequestException("Couldn't read file." + e.getMessage());
        }
    }
}
