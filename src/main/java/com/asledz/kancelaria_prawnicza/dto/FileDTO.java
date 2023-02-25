package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

import java.util.Arrays;
import java.util.Objects;

@Builder
public record FileDTO(
        Long id,
        String name,
        String extension,
        byte[] content
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDTO fileDTO = (FileDTO) o;
        return Objects.equals(id, fileDTO.id) && Objects.equals(extension, fileDTO.extension) && Arrays.equals(content, fileDTO.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, extension);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "id=" + id +
                ", extension='" + extension + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
