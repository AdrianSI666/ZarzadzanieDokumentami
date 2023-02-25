package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.UserRepository;
import com.asledz.kancelaria_prawnicza.specification.CustomSpecification;
import com.asledz.kancelaria_prawnicza.specification.SearchCriteria;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private final DTOMapper<User, UserDTO> mapper;

    protected static final String USER_NOT_FOUND_MSG = "Couldn't find user with id: %d";

    public Page<UserDTO> getUsers(Integer page) {
        log.info("Page %d of all users".formatted(page));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<User> userPage = userRepository.findAll(paging);
        return userPage.map(mapper::map);
    }

    public UserDTO getUserById(Long id) {
        log.info("Getting user with id: %d".formatted(id));
        return mapper.map(userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, id))
        ));
    }
    //TODO ta metoda jest tylko tymczasowa do zastąpienia przez Security
    public UserDTO getUserByName(String name) {
        log.info("Getting user with name: %s".formatted(name));
        CustomSpecification<User> specByName = new CustomSpecification<>(new SearchCriteria("name", ":", name));
        return mapper.map(userRepository.findOne(specByName).orElseThrow(
                () -> new NotFoundException("Couldn't find user with name: %s".formatted(name))
        ));
    }

    public Page<UserDTO> getUsersByRole(Long roleId, Integer page) {
        log.info("Getting users by roleId: %d".formatted(roleId));
        CustomSpecification<User> usersByRoleId = new CustomSpecification<>(new SearchCriteria("role_id",
                ":",
                roleId));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<User> userPage = userRepository.findAll(usersByRoleId, paging);
        return userPage.map(mapper::map);
    }

    public UserDTO addUser(UserDTO newUserInformation) {
        log.info("Adding user:" + newUserInformation);
        User user = User.builder()
                .name(newUserInformation.name())
                .surname(newUserInformation.surname())
                .email(newUserInformation.email())
                .build();
        //TODO Przydzielanie podstawowej roli dla użytkownika lub dodanie, że podczas dodawania użytkownika podaje się jego rolę
        //user.setRoles();
        return mapper.map(userRepository.save(user));
    }

    public UserDTO updateUser(UserDTO updatedUserInformation, Long id) {
        log.info("Updating user with id: %d".formatted(id));
        User oldUser = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        oldUser.setName(updatedUserInformation.name());
        oldUser.setSurname(updatedUserInformation.surname());
        oldUser.setEmail(updatedUserInformation.email());
        return mapper.map(userRepository.save(oldUser));
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with id: %d".formatted(userId));
        userRepository.deleteById(userId);
    }
}
