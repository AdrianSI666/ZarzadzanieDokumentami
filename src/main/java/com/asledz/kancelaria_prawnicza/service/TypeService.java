package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.dto.TypeDTO;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Class to manage types od Documents
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TypeService {
    private final TypeRepository typeRepository;
    private final DTOMapper<Type, TypeDTO> mapper;
    protected static final String TYPE_NOT_FOUND_MSG = "Couldn't find type with id: %d";

    /**
     * Function to get all known types of Documents
     *
     * @return all types that exists in database
     */
    public List<TypeDTO> getTypes() {
        log.info("Getting all types.");
        return typeRepository.findAll(Sort.by("id")).stream().map(mapper::map).toList();
    }

    /**
     * Function to get type by id
     *
     * @return type with given id, or throws NotFoundException
     */
    public Type getTypeById(Long typeId) {
        log.info("Getting type by id: %d".formatted(typeId));
        return typeRepository.findById(typeId).orElseThrow(
                () -> new NotFoundException(String.format(TYPE_NOT_FOUND_MSG, typeId)));
    }

    /**
     * Function to add new type of Document
     *
     * @param newTypeInformation - contains name of new type
     * @return type with given id from database and name
     */
    public TypeDTO addType(TypeDTO newTypeInformation) {
        log.info("Adding type:" + newTypeInformation);
        Type type = Type.builder()
                .name(newTypeInformation.name())
                .build();
        return mapper.map(typeRepository.save(type));
    }
}
