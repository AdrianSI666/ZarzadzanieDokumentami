package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.RoleService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Path.ROLE_VALUE + "s")
public class RoleController {
    private final RoleService roleService;
    private final Converter converter;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getRoles(@RequestParam(defaultValue = "1") Integer page) {
        Page<RoleDTO> roleDTOPage = roleService.getRoles(page);
        return ResponseEntity.ok().body(converter.convertDataFromPageToMap(roleDTOPage));
    }

}
