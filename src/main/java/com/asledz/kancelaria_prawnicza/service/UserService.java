package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.NewUserDTO;
import com.asledz.kancelaria_prawnicza.dto.UserAuthorities;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.RoleRepository;
import com.asledz.kancelaria_prawnicza.repository.UserRepository;
import com.asledz.kancelaria_prawnicza.specification.CustomSpecification;
import com.asledz.kancelaria_prawnicza.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_NUMBER;
import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_SIZE;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final DTOMapper<User, UserDTO> mapper;

    protected static final String USER_NOT_FOUND_MSG = "Couldn't find user with id: %d";

    public Page<UserDTO> getUsers(Map<String, String> pageProperties) {
        int page = 0;
        if (pageProperties.containsKey(PAGE_NUMBER.name)) {
            page = Integer.parseInt(pageProperties.get(PAGE_NUMBER.name)) - 1;
        }
        log.info("Page %d of all users".formatted(page));
        int pageSize = 5;
        if (pageProperties.containsKey(PAGE_SIZE.name)) {
            pageSize = Integer.parseInt(pageProperties.get(PAGE_SIZE.name));
        }
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> userPage = userRepository.findAll(paging);
        return userPage.map(mapper::map);
    }

    public UserAuthorities getUserDetailsByEmail(String email) {
        log.info("Getting user with email: %s".formatted(email));
        CustomSpecification<User> specByName = new CustomSpecification<>(new SearchCriteria("email", ":", email));
        User user = userRepository.findOne(specByName).orElseThrow(() -> new NotFoundException("Couldn't find user with email: %s".formatted(email)));

        return new UserAuthorities(user.getEmail(), user.getRoles());
    }

    public UserDTO getUserByEmail(String email) {
        log.info("Getting user with email: %s".formatted(email));
        CustomSpecification<User> specByName = new CustomSpecification<>(new SearchCriteria("email", ":", email));
        User user = userRepository.findOne(specByName).orElseThrow(() -> new NotFoundException("Couldn't find user with email: %s".formatted(email)));
        return mapper.map(user);
    }

    public UserDTO addUser(NewUserDTO newUserInformation) {
        log.info("Adding user:" + newUserInformation);
        User user = User.builder()
                .name(newUserInformation.name())
                .surname(newUserInformation.surname())
                .email(newUserInformation.email())
                .password(passwordEncoder.encode(newUserInformation.password()))
                .build();
        Role role = roleRepository.findRoleByName("User").orElseThrow(
                () -> new NotFoundException("Can't find role: \"User\" in database"));
        user.setRoles(List.of(role));
        return mapper.map(userRepository.save(user));
    }

    public UserDTO updateUser(UserDTO updatedUserInformation, Long id) {
        log.info("Updating user with id: %d".formatted(id));
        User oldUser = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        oldUser.setName(updatedUserInformation.name());
        oldUser.setSurname(updatedUserInformation.surname());
        return mapper.map(userRepository.save(oldUser));
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with id: %d".formatted(userId));
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        CustomSpecification<User> specByUsername = new CustomSpecification<>(new SearchCriteria("email", ":", email));
        User user = userRepository.findOne(specByUsername).orElseThrow(() -> new NotFoundException("Can't find user with email: %s".formatted(email)));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
