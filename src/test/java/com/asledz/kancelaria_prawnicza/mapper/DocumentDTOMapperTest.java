package com.asledz.kancelaria_prawnicza.mapper;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {DocumentDTOMapper.class})
@ExtendWith(SpringExtension.class)
class DocumentDTOMapperTest {
    @Autowired
    private DocumentDTOMapper documentDTOMapper;

    @Test
    void testMap() throws UnsupportedEncodingException {
        File file = new File();
        file.setContent("AXAXAXAX".getBytes("UTF-8"));
        file.setDocument(new Document());
        file.setExtension("Extension");
        file.setId(1L);
        file.setText("Text");

        User owner = new User();
        owner.setDocuments(new ArrayList<>());
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");
        owner.setPassword("iloveyou");
        owner.setRoles(new ArrayList<>());
        owner.setSurname("Doe");

        Document document = new Document();
        document.setCost(10.0d);
        document.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document.setFile(file);
        document.setId(1L);
        document.setOwner(owner);
        document.setPaid(true);
        document.setTitle("Dr");
        document.setType(null);

        DocumentDTO actualMapResult = documentDTOMapper.map(document);
        assertEquals(10.0d, actualMapResult.cost().doubleValue());
        assertEquals("Dr", actualMapResult.title());
        assertEquals(-1L, actualMapResult.typeId().longValue());
        assertEquals("brak typu", actualMapResult.typeName());
        assertEquals("Dr", actualMapResult.title());
        assertTrue(actualMapResult.paid());
        assertEquals(1L, actualMapResult.id().longValue());
    }
}

