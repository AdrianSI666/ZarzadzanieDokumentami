package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.FileDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for file class. Its functionality is to receive files and to send them to allow for download of files.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(Path.FILE_VALUE + "s")
@Slf4j
public class FileController {
    private final FileService fileService;

    /**
     * Endpoint to download file from backend.
     *
     * @param id of file that you want to download
     * @return ResponseEntity with http header that show that body is a file and with body
     * containing bytes of file.
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        FileDTO fileToSend = fileService.getFileById(id);
        log.info("Sending file with id:" + id);
        String fileName = fileToSend.name() + fileToSend.extension();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(fileToSend.extension()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileToSend.content());
    }

    /**
     * Endpoint to add file to database.
     *
     * @param formData - MultipartFile that contains bytes of file, InputStream and extension of file.
     * @return ResponseEntity with note that everything went correct.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, path = "/{id}")
    public ResponseEntity<String> saveFile(@PathVariable Long id,
                                           @RequestParam("file") MultipartFile formData) {
        fileService.addFile(formData, id);
        //Without this line tomcat doesn't delete temp upload file if worked with Gotenberg service
        System.gc();
        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }
}
