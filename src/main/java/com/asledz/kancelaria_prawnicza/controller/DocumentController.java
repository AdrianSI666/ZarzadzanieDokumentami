package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.DocumentService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for document class. Its functionality is to send and retrieve documents data for processing.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Path.DOCUMENT_VALUE + "s")
public class DocumentController {
    private final DocumentService documentService;
    private final Converter converter;

    /**
     * Endpoint to get page containing Documents data
     *
     * @param pageProperties map with:
     *                       <p>
     *                       page : number of page that you want to get;
     *                       <p>
     *                       pageSize : numbers of items per page;
     *                       <p>
     *                       sortParams: key values to sort by with boolean if they're reversed or not.
     *                       Available options of sorting can be found in
     *                       enum class {@link  com.asledz.kancelaria_prawnicza.enums.SortEnum  SortEnum}
     * @return ResponseEntity with body containing Map of:
     * <p>
     * "data" - List of Documents
     * <p>
     * "currentPage" - current page which was given
     * <p>
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
     * Endpoint to get Documents filtered by parameters.
     *
     * @param filterParameters MultiValueMap of parameters to filter by. Available options of filtering can be found in
     *                         enum class {@link  com.asledz.kancelaria_prawnicza.enums.FilterAndSortEnum  FilterAndSortEnum}
     * @return ResponseEntity with body containing Map of:
     * <p>
     * "data" - List of Documents
     * <p>
     * "currentPage" - current page which was given
     * <p>
     * "totalPages" - total number of pages that you can get
     */
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok().body(documentService.updateDocument(documentDTO, id, authentication.getName()));
    }

    /**
     * Endpoint to delete Document from database
     *
     * @param id - of document you want to delete
     * @return ResponseEntity with note that everything went correct
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        documentService.deleteDocument(id, authentication.getName());
        return ResponseEntity.ok().body("Success");
    }

    /**
     * Endpoint to find documents without specified dates, created by given user.
     *
     * @param id of owner of documents you want to find.
     * @return ResponseEntity with body containing List of Documents information.
     */
    @GetMapping("/user/{id}/withoutDate/{page}")
    public ResponseEntity<Map<String, Object>> getDocumentsByUserIdWithoutDate(@PathVariable Long id, @PathVariable Integer page) {
        return ResponseEntity.ok().body(converter.convertDataFromPageToMap(documentService.getDocumentsByUserIdWithoutDate(id, page)));
    }

}
