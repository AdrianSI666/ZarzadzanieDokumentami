package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Path.FILE_VALUE + "s")
public class FileController {
    private final FileService fileService;
    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> downloadDocument(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileById(id));
    }
}
