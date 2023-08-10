package com.asledz.kancelaria_prawnicza.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class DocumentDTOTest {
    /**
     * Method under test: {@link DocumentDTO#DocumentDTO(Long, String, Instant, Double, Boolean, Long, String)}
     */
    @Test
    void testConstructor() {
        Instant date = LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        DocumentDTO actualDocumentDTO = new DocumentDTO(1L, "Dr", date, 10.0d, true, 1L, "Type Name");

        assertEquals(10.0d, actualDocumentDTO.cost().doubleValue());
        assertEquals("Type Name", actualDocumentDTO.typeName());
        assertEquals(1L, actualDocumentDTO.typeId().longValue());
        assertEquals("Dr", actualDocumentDTO.title());
        assertTrue(actualDocumentDTO.paid());
        assertEquals(1L, actualDocumentDTO.id().longValue());
        Instant expectedDateResult = date.EPOCH;
        Instant dateResult = actualDocumentDTO.date();
        assertSame(expectedDateResult, dateResult);
        assertEquals(0L, dateResult.getEpochSecond());
        assertEquals(0, dateResult.getNano());
    }

    /**
     * Method under test: {@link DocumentDTO#cost()}
     */
    @Test
    void testCost() {
        assertEquals(10.0d,
                (new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d,
                        true, 1L, "Type Name")).cost().doubleValue());
    }

    /**
     * Method under test: {@link DocumentDTO#date()}
     */
    @Test
    void testDate() {
        Instant actualDateResult = (new DocumentDTO(1L, "Dr",
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d, true, 1L, "Type Name"))
                .date();
        assertSame(actualDateResult.EPOCH, actualDateResult);
    }

    /**
     * Method under test: {@link DocumentDTO#id()}
     */
    @Test
    void testId() {
        assertEquals(1L,
                (new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d,
                        true, 1L, "Type Name")).id().longValue());
    }

    /**
     * Method under test: {@link DocumentDTO#paid()}
     */
    @Test
    void testPaid() {
        assertTrue((new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(),
                10.0d, true, 1L, "Type Name")).paid());
        assertFalse(
                (new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(),
                        10.0d, false, 1L, "Type Name")).paid());
    }

    /**
     * Method under test: {@link DocumentDTO#title()}
     */
    @Test
    void testTitle() {
        assertEquals("Dr",
                (new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d,
                        true, 1L, "Type Name")).title());
    }

    /**
     * Method under test: {@link DocumentDTO#typeId()}
     */
    @Test
    void testTypeId() {
        assertEquals(1L,
                (new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d,
                        true, 1L, "Type Name")).typeId().longValue());
    }

    /**
     * Method under test: {@link DocumentDTO#typeName()}
     */
    @Test
    void testTypeName() {
        assertEquals("Type Name",
                (new DocumentDTO(1L, "Dr", LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d,
                        true, 1L, "Type Name")).typeName());
    }
}

