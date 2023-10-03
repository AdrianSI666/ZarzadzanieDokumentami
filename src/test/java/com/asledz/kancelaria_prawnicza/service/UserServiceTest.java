package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.NewUserDTO;
import com.asledz.kancelaria_prawnicza.dto.UserAuthorities;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.asledz.kancelaria_prawnicza.exception.LoginException;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.helper.DataToPage;
import com.asledz.kancelaria_prawnicza.mapper.UserDTOMapper;
import com.asledz.kancelaria_prawnicza.mother.UserMother;
import com.asledz.kancelaria_prawnicza.repository.RoleRepository;
import com.asledz.kancelaria_prawnicza.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_NUMBER;
import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_SIZE;
import static com.asledz.kancelaria_prawnicza.service.UserService.USER_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @Spy
    private UserDTOMapper dTOMapper;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    User user = UserMother.basic(1L).build();

    /**
     * Method under test: {@link UserService#getUsers(Map)}
     */
    @Test
    void testGetUsers() {
        when(userRepository.findAll(Mockito.<Pageable>any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertTrue(userService.getUsers(new HashMap<>()).toList().isEmpty());
        verify(userRepository).findAll(Mockito.<Pageable>any());
    }

    /**
     * Method under test: {@link UserService#getUsers(Map)}
     */
    @Test
    void testGetUsers2() {

        User user1 = UserMother.basic(1L).name("first").build();
        User user2 = UserMother.basic(2L).name("second").build();
        User user3 = UserMother.basic(3L).name("third").build();
        User user4 = UserMother.basic(4L).name("fourth").build();
        User user5 = UserMother.basic(5L).name("fifth").build();
        User user6 = UserMother.basic(6L).name("sixth").build();
        User user7 = UserMother.basic(7L).name("seventh").build();

        List<User> content = List.of(user1, user2, user3, user4, user5, user6, user7);
        int page = 1;
        int pageSize = 2;
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> pageImpl = DataToPage.toPage(content, paging);
        List<UserDTO> expectedDTOS = pageImpl.map(dTOMapper::map).toList();

        when(userRepository.findAll(paging)).thenReturn(pageImpl);

        Page<UserDTO> users = userService.getUsers(Map.of(PAGE_NUMBER.name, "2", PAGE_SIZE.name, "2"));
        List<UserDTO> resultUserDTO = users.stream().toList();

        assertEquals(expectedDTOS, resultUserDTO);
        assertEquals(pageImpl.getTotalPages(), users.getTotalPages());
        assertEquals(pageImpl.getTotalElements(), users.getTotalElements());
    }

    /**
     * Method under test: {@link UserService#getUserDetailsByEmail(String)}
     */
    @Test
    void testGetUserDetailsByEmail() {
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        UserAuthorities actualUserDetailsByEmail = userService.getUserDetailsByEmail(user.getEmail());
        assertEquals(user.getEmail(), actualUserDetailsByEmail.email());
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
    }

    /**
     * Method under test: {@link UserService#getUserDetailsByEmail(String)}
     */
    @Test
    void testGetUserDetailsByEmail2() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getRoles()).thenReturn(new ArrayList<>());
        doNothing().when(user).setDocuments(Mockito.<Collection<Document>>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setRoles(Mockito.<Collection<Role>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        ArrayList<Document> documents = new ArrayList<>();
        user.setDocuments(documents);
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        UserAuthorities actualUserDetailsByEmail = userService.getUserDetailsByEmail("jane.doe@example.org");
        assertEquals("jane.doe@example.org", actualUserDetailsByEmail.email());
        assertEquals(documents, actualUserDetailsByEmail.roles());
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
        verify(user).getEmail();
        verify(user).getRoles();
        verify(user).setDocuments(Mockito.<Collection<Document>>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setRoles(Mockito.<Collection<Role>>any());
        verify(user).setSurname(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#getUserByEmail(String)}
     */
    @Test
    void testGetUserByEmail() {
        User user = new User();
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        UserDTO userDTO = new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>());

        //when(dTOMapper.map(Mockito.<User>any())).thenReturn(userDTO);
        assertEquals(userDTO, userService.getUserByEmail("jane.doe@example.org"));
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
        verify(dTOMapper).map(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#getUserByEmail(String)}
     */
    @Test
    void testGetUserByEmailThrowsNotFound() {
        given(userRepository.findOne(Mockito.<Specification<User>>any())).willReturn(Optional.empty());
        String email = "jane.doe@example.org";
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Couldn't find user with email: %s".formatted(email));
    }

    /**
     * Method under test: {@link UserService#addUser(NewUserDTO)}
     */
    @Test
    void testAddUser() {
        User user = new User();
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        Role role = new Role();
        role.setId(1L);
        role.setName("Name");
        role.setUsers(new ArrayList<>());
        Optional<Role> ofResult = Optional.of(role);
        when(roleRepository.findRoleByName(Mockito.<String>any())).thenReturn(ofResult);
        UserDTO userDTO = new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>());

        assertEquals(userDTO, userService.addUser(new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou")));
        verify(userRepository).save(Mockito.<User>any());
        verify(roleRepository).findRoleByName(Mockito.<String>any());
        verify(dTOMapper).map(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#addUser(NewUserDTO)}
     */
    @Test
    void testAddUserThrowsNotFound() {
        given(roleRepository.findRoleByName("User")).willReturn(Optional.empty());
        NewUserDTO newUserDTO = new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou");
        assertThatThrownBy(() -> userService.addUser(newUserDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Can't find role: \"User\" in database");
    }

    /**
     * Method under test: {@link UserService#updateUser(UserDTO, Long)}
     */
    @Test
    void testUpdateUser() {
        User user = new User();
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setDocuments(new ArrayList<>());
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setRoles(new ArrayList<>());
        user2.setSurname("Doe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        UserDTO userDTO = new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>());

        assertEquals(userDTO,
                userService.updateUser(new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>()), 1L));
        verify(userRepository).save(Mockito.<User>any());
        verify(userRepository).findById(Mockito.<Long>any());
        verify(dTOMapper).map(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#updateUser(UserDTO, Long)}
     */
    @Test
    void testUpdateUserThrowsNotFound() {
        Long id = 1L;
        given(userRepository.findById(id)).willReturn(Optional.empty());
        UserDTO userDTO = dTOMapper.map(user);
        assertThatThrownBy(() -> userService.updateUser(userDTO, id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(USER_NOT_FOUND_MSG, id));
    }

    /**
     * Method under test: {@link UserService#updateUser(UserDTO, Long)}
     */
    @Test
    void testUpdateUser3() {
        User user = mock(User.class);
        doNothing().when(user).setDocuments(Mockito.<Collection<Document>>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setRoles(Mockito.<Collection<Role>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setDocuments(new ArrayList<>());
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setName("Name");
        user2.setPassword("iloveyou");
        user2.setRoles(new ArrayList<>());
        user2.setSurname("Doe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        UserDTO userDTO = new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>());

        assertEquals(userDTO,
                userService.updateUser(new UserDTO(1L, "Name", "Doe", "jane.doe@example.org", new ArrayList<>()), 1L));
        verify(userRepository).save(Mockito.<User>any());
        verify(userRepository).findById(Mockito.<Long>any());
        verify(user).setDocuments(Mockito.<Collection<Document>>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user, atLeast(1)).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setRoles(Mockito.<Collection<Role>>any());
        verify(user, atLeast(1)).setSurname(Mockito.<String>any());
        verify(dTOMapper).map(Mockito.<User>any());
    }

    /**
     * Method under test: {@link UserService#deleteUser(Long)}
     */
    @Test
    void testDeleteUser() {
        given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user));

        userService.deleteUser(1L);

        assertFalse(user.getEnabled());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername() {
        User user = new User();
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        UserDetails actualLoadUserByUsernameResult = userService.loadUserByUsername("jane.doe@example.org");
        assertTrue(actualLoadUserByUsernameResult.getAuthorities().isEmpty());
        assertTrue(actualLoadUserByUsernameResult.isEnabled());
        assertTrue(actualLoadUserByUsernameResult.isCredentialsNonExpired());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
        assertEquals("jane.doe@example.org", actualLoadUserByUsernameResult.getUsername());
        assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername2() {
        ArrayList<Role> roles = new ArrayList<>();

        User user = new User(1L, "email", "Doe", "jane.doe@example.org", "iloveyou", true, roles, new ArrayList<>());
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        UserDetails actualLoadUserByUsernameResult = userService.loadUserByUsername("jane.doe@example.org");
        assertTrue(actualLoadUserByUsernameResult.getAuthorities().isEmpty());
        assertTrue(actualLoadUserByUsernameResult.isEnabled());
        assertTrue(actualLoadUserByUsernameResult.isCredentialsNonExpired());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
        assertEquals("jane.doe@example.org", actualLoadUserByUsernameResult.getUsername());
        assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername3() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getPassword()).thenReturn("iloveyou");
        when(user.getRoles()).thenReturn(new ArrayList<>());
        when(user.getEnabled()).thenReturn(true);
        doNothing().when(user).setDocuments(Mockito.<Collection<Document>>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setRoles(Mockito.<Collection<Role>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        user.setEnabled(true);
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        UserDetails actualLoadUserByUsernameResult = userService.loadUserByUsername("jane.doe@example.org");
        assertTrue(actualLoadUserByUsernameResult.getAuthorities().isEmpty());
        assertTrue(actualLoadUserByUsernameResult.isEnabled());
        assertTrue(actualLoadUserByUsernameResult.isCredentialsNonExpired());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
        assertEquals("jane.doe@example.org", actualLoadUserByUsernameResult.getUsername());
        assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
        verify(user).getEmail();
        verify(user).getPassword();
        verify(user).getRoles();
        verify(user).setDocuments(Mockito.<Collection<Document>>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setRoles(Mockito.<Collection<Role>>any());
        verify(user).setSurname(Mockito.<String>any());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsernameThrowsLoginException() {
        Role role = new Role();
        role.setId(1L);
        role.setName("email");
        role.setUsers(new ArrayList<>());

        ArrayList<Role> roleList = new ArrayList<>();
        roleList.add(role);
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getPassword()).thenReturn("iloveyou");
        when(user.getRoles()).thenReturn(roleList);
        when(user.getEnabled()).thenReturn(false);

        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        user.setEnabled(true);
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        assertThatThrownBy(() -> userService.loadUserByUsername(user.getEmail()))
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Given user with email %s have his account disabled, but tried to log in".formatted(user.getEmail()));
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
    }

    /**
     * Method under test: {@link UserService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername6() {
        Role role = new Role();
        role.setId(1L);
        role.setName("email");
        role.setUsers(new ArrayList<>());

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(":");
        role2.setUsers(new ArrayList<>());

        ArrayList<Role> roleList = new ArrayList<>();
        roleList.add(role2);
        roleList.add(role);
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("jane.doe@example.org");
        when(user.getPassword()).thenReturn("iloveyou");
        when(user.getRoles()).thenReturn(roleList);
        when(user.getEnabled()).thenReturn(true);
        doNothing().when(user).setDocuments(Mockito.<Collection<Document>>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Long>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setRoles(Mockito.<Collection<Role>>any());
        doNothing().when(user).setSurname(Mockito.<String>any());
        user.setDocuments(new ArrayList<>());
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        user.setPassword("iloveyou");
        user.setRoles(new ArrayList<>());
        user.setSurname("Doe");
        user.setEnabled(true);
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findOne(Mockito.<Specification<User>>any())).thenReturn(ofResult);
        UserDetails actualLoadUserByUsernameResult = userService.loadUserByUsername("jane.doe@example.org");
        assertEquals(2, actualLoadUserByUsernameResult.getAuthorities().size());
        assertTrue(actualLoadUserByUsernameResult.isEnabled());
        assertTrue(actualLoadUserByUsernameResult.isCredentialsNonExpired());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
        assertEquals("jane.doe@example.org", actualLoadUserByUsernameResult.getUsername());
        assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
        verify(userRepository).findOne(Mockito.<Specification<User>>any());
        verify(user).getEmail();
        verify(user).getPassword();
        verify(user).getRoles();
        verify(user).setDocuments(Mockito.<Collection<Document>>any());
        verify(user).setEmail(Mockito.<String>any());
        verify(user).setId(Mockito.<Long>any());
        verify(user).setName(Mockito.<String>any());
        verify(user).setPassword(Mockito.<String>any());
        verify(user).setRoles(Mockito.<Collection<Role>>any());
        verify(user).setSurname(Mockito.<String>any());
    }
}

