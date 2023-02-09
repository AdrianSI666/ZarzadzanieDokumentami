package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.RoleRepository;
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
public class RoleService {
    private final RoleRepository roleRepository;

    private final DTOMapper<Role, RoleDTO> mapper;

    protected static final String ROLE_NOT_FOUND_MSG = "Couldn't find role with id: %d";

    public Page<RoleDTO> getRoles(Integer page) {
        log.info("Page %d of all roles".formatted(page));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Role> rolePage = roleRepository.findAll(paging);
        return rolePage.map(mapper::map);
    }

    public RoleDTO getRoleById(Long id) {
        log.info("Getting role with id: %d".formatted(id));
        return mapper.map(roleRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(ROLE_NOT_FOUND_MSG, id))
        ));
    }

    public Page<RoleDTO> getRolesByUser(Long userId, Integer page) {
        log.info("Getting Roles by roleId: %d".formatted(userId));
        CustomSpecification<Role> rolesByUserId = new CustomSpecification<>(new SearchCriteria("user_id",
                ":",
                userId));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Role> rolePage = roleRepository.findAll(rolesByUserId, paging);
        return rolePage.map(mapper::map);
    }

    public RoleDTO addRole(RoleDTO newRoleInformation) {
        log.info("Adding Role:" + newRoleInformation);
        Role role = Role.builder()
                .name(newRoleInformation.name())
                .build();
        return mapper.map(roleRepository.save(role));
    }

    public RoleDTO updateRole(RoleDTO updatedRoleInformation, Long id) {
        log.info("Updating Role with id: %d".formatted(id));
        Role oldRole = roleRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(ROLE_NOT_FOUND_MSG, id)));
        oldRole.setName(updatedRoleInformation.name());
        return mapper.map(roleRepository.save(oldRole));
    }

    public void deleteRole(Long roleId) {
        log.info("Deleting Role with id: %d".formatted(roleId));
        roleRepository.deleteById(roleId);
    }
}
