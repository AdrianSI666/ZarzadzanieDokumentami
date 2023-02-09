package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.dto.TypeDTO;
import org.springframework.stereotype.Component;

@Component
public class TypeDTOMapper implements DTOMapper<Type, TypeDTO> {
    @Override
    public TypeDTO map(Type source) {
        return TypeDTO.builder()
                .id(source.getId())
                .name(source.getName())
                .build();
    }
}
