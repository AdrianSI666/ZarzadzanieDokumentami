package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.RoleService;
import com.asledz.kancelaria_prawnicza.utilis.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping(Path.USER_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getRolesByUserId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<RoleDTO> roleDTOPage = roleService.getRolesByUser(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(roleDTOPage));
    }

    @PostMapping()
    public ResponseEntity<RoleDTO> addRole(@RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.addRole(roleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO role) {
        return ResponseEntity.ok().body(roleService.updateRole(role, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
