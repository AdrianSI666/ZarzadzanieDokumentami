package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.DocumentService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Path.DOCUMENT_VALUE + "s")
public class DocumentController {
    private final DocumentService documentService;
    private final Converter converter;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getDocuments(@RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok().body(converter.convertDataFromPageToMap(documentService.getDocuments(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping(Path.USER_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentsByUserId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<DocumentDTO> documentDTOPage = documentService.getDocumentsByUser(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(documentDTOPage));
    }

    @GetMapping(Path.TAG_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentsByTagId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<DocumentDTO> documentDTOPage = documentService.getDocumentsByTag(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(documentDTOPage));
    }

    @GetMapping(Path.TYPE_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentsByTypeId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<DocumentDTO> documentDTOPage = documentService.getDocumentsByType(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(documentDTOPage));
    }

    //TODO Zmienić ręczny przesył danych na automatyczny przez multipartfile
    @PostMapping()
    public ResponseEntity<DocumentDTO> addDocument(@RequestBody DocumentDTO documentDTO) {
        return ResponseEntity.ok(documentService.addDocument(documentDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable Long id, @RequestBody DocumentDTO documentDTO) {
        return ResponseEntity.ok().body(documentService.updateDocument(documentDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
