package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.dto.NewUserDTO;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.service.UserService;
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
@RequestMapping(Path.USER_VALUE + "s")
public class UserController {
    private final UserService userService;
    private final Converter converter;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(defaultValue = ("page:1; maxSize:5")) Map<String, String> pageProperties) {
        Page<UserDTO> userDTOPage = userService.getUsers(pageProperties);
        return ResponseEntity.ok().body(converter.convertDataFromPageToMap(userDTOPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping(Path.ROLE_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getUsersByRoleId(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page) {
        Page<UserDTO> userDTOPage = userService.getUsersByRole(id, page);
        return ResponseEntity.ok(converter.convertDataFromPageToMap(userDTOPage));
    }

    @PostMapping()
    public ResponseEntity<UserDTO> addUser(@RequestBody NewUserDTO newUserDTO) {
        return ResponseEntity.ok(userService.addUser(newUserDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO user) {
        return ResponseEntity.ok().body(userService.updateUser(user, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
