package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.DocumentService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for document class. Its functionality is to send and retrieve documents data for processing.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(Path.DOCUMENT_VALUE + "s")
public class DocumentController {
    private final DocumentService documentService;
    private final Converter converter;

    /**
     * Endpoint to get page containing Documents data
     *
     * @param pageProperties - map with:
     *                       page : number of page that you want to get
     *                       pageSize : numbers of items per page
     * @return ResponseEntity with body containing Map of "data" - List of Documents
     * "currentPage" - current page which was given
     * "totalPages" - total number of pages that you can get
     */
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getDocuments(@RequestParam(defaultValue = ("page:1; maxSize:5")) Map<String, String> pageProperties) {
        return ResponseEntity.ok().body(converter.convertDataFromPageToMap(documentService.getDocuments(pageProperties)));
    }

    /**
     * Endpoint to get data for one Document
     *
     * @param id of Document, you want to get
     * @return ResponseEntity with body containing Document information
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    /**
     * Endpoint to get page containing Documents data that were bond to given userID
     *
     * @param id of user that you want to search
     * @return ResponseEntity with body containing Map of page with Documents
     */
    @GetMapping(Path.USER_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentsByUserId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<DocumentDTO> documentDTOPage = documentService.getDocumentsByUser(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(documentDTOPage));
    }

    /**
     * Endpoint to get page containing Documents data that were bond to given tagId
     *
     * @param id   of tag that you want to search
     * @param page - number of page that you want to get
     * @return ResponseEntity with body containing Map of page with Documents
     */
    @GetMapping(Path.TAG_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentsByTagId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<DocumentDTO> documentDTOPage = documentService.getDocumentsByTag(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(documentDTOPage));
    }

    /**
     * Endpoint to get page containing Documents data that were bond to given typeId
     *
     * @param id   of type that you want to search
     * @param page - number of page that you want to get
     * @return ResponseEntity with body containing Map of page with Documents
     */
    @GetMapping(Path.TYPE_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentsByTypeId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<DocumentDTO> documentDTOPage = documentService.getDocumentsByType(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(documentDTOPage));
    }

    /**
     * Endpoint to get page containing Documents data that have given text in them
     *
     * @param page   - number of page that you want to get
     * @param prompt - which you want to search in texts
     * @return ResponseEntity with body containing Map of page with Documents
     */
    @GetMapping(Path.TEXT_VALUE + "/{prompt}")
    public ResponseEntity<Map<String, Object>> getDocumentsByText(@RequestParam(defaultValue = "1") Integer page,
                                                                  @PathVariable String prompt) {
        Page<DocumentDTO> documentDTOPage = documentService.getDocumentsByText(page, prompt);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(documentDTOPage));
    }

    @GetMapping("/by")
    public ResponseEntity<Map<String, Object>> getDocumentsByParameters(@RequestParam MultiValueMap<String, String> filterParameters) {
        return ResponseEntity.ok().body(converter.convertDataFromPageToMap(documentService.getDocumentsByParameters(filterParameters)));
    }

    /**
     * Endpoint to update Document data
     *
     * @param id          - of document you want to change
     * @param documentDTO - data that need to replace old data
     * @return ResponseEntity with body containing data of updated Document
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable Long id, @RequestBody DocumentDTO documentDTO) {
        return ResponseEntity.ok().body(documentService.updateDocument(documentDTO, id));
    }

    /**
     * Endpoint to delete Document from database
     *
     * @param id - of document you want to delete
     * @return ResponseEntity with note that everything went correct
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().body("Success");
    }

}
