package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.TypeDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for type class. Its functionality is to manage documents types of documents.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(Path.TYPE_VALUE + "s")
public class TypeController {
    private final TypeService typeService;

    /**
     * Endpoint to get all known types from database
     *
     * @return list of types with theirs id
     */
    @GetMapping()
    public ResponseEntity<List<TypeDTO>> getTypes() {
        return ResponseEntity.ok().body(typeService.getTypes());
    }

    /**
     * Endpoint to add new type.
     *
     * @param typeDTO - new type information containing type name
     * @return saved typed with given by database id and it's name
     */
    @PostMapping()
    public ResponseEntity<TypeDTO> addType(@RequestBody TypeDTO typeDTO) {
        return ResponseEntity.ok().body(typeService.addType(typeDTO));
    }

}
