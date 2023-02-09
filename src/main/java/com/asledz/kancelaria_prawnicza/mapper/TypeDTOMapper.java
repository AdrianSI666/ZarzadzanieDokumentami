package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.dto.TagDTO;
import org.springframework.stereotype.Component;

@Component
public class TypeDTOMapper implements DTOMapper<Type, TagDTO> {
    @Override
    public TagDTO map(Type source) {
        Long id = source.getId();
        String name = source.getName();
        return new TagDTO(id, name);
    }
}
