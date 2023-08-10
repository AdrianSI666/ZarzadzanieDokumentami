package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
class FileDTOMapperTest {
    @InjectMocks
    private FileDTOMapper fileDTOMapper;

    /**
     * Method under test: {@link FileDTOMapper#map(File)}
     */
    @Test
    void testMapThrowsBadRequestBecauseContentOfFileIsCorrupted() {

        Document document = new Document();
        document.setCost(10.0d);
        document.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document.setId(1L);
        document.setPaid(true);
        document.setTitle("Dr");

        File file = new File();
        file.setContent(null);
        file.setDocument(document);
        file.setExtension("Extension");
        file.setId(1L);
        file.setText("Text");
        document.setFile(file);
        
        assertThatThrownBy(() -> fileDTOMapper.map(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error while decompressing byte array occurred on file: %d".formatted(1L));
    }
}

