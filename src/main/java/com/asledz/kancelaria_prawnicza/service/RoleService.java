package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.dto.RoleDTO;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    private final DTOMapper<Role, RoleDTO> mapper;

    public Page<RoleDTO> getRoles(Integer page) {
        log.info("Page %d of all roles".formatted(page));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Role> rolePage = roleRepository.findAll(paging);
        return rolePage.map(mapper::map);
    }

    public RoleDTO addRole(RoleDTO newRoleInformation) {
        log.info("Adding Role:" + newRoleInformation);
        Role role = Role.builder()
                .name(newRoleInformation.name())
                .build();
        return mapper.map(roleRepository.save(role));
    }
}
