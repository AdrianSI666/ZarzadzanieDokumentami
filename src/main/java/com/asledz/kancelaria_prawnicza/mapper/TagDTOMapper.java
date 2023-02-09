package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Tag;
import com.asledz.kancelaria_prawnicza.dto.TagDTO;
import org.springframework.stereotype.Component;

@Component
public class TagDTOMapper implements DTOMapper<Tag, TagDTO> {
    @Override
    public TagDTO map(Tag source) {
        Long id = source.getId();
        String name = source.getName();
        return new TagDTO(id, name);
    }
}
