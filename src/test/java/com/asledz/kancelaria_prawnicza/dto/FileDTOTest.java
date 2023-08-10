package com.asledz.kancelaria_prawnicza.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

class FileDTOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link FileDTO#FileDTO(Long, String, String, byte[])}
     *   <li>{@link FileDTO#toString()}
     * </ul>
     */
    @Test
    void testConstructor() throws UnsupportedEncodingException {
        assertEquals("FileDTO{id=1, extension='Extension', content=[65, 88, 65, 88, 65, 88, 65, 88]}",
                (new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8"))).toString());
    }

    /**
     * Method under test: {@link FileDTO#equals(Object)}
     */
    @Test
    void testEquals() throws UnsupportedEncodingException {
        assertNotEquals(null, new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8")));
        assertNotEquals("Different type to FileDTO", new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link FileDTO#equals(Object)}
     *   <li>{@link FileDTO#hashCode()}
     * </ul>
     */
    @Test
    void testEquals2() throws UnsupportedEncodingException {
        FileDTO fileDTO = new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8"));
        assertEquals(fileDTO, fileDTO);
        int expectedHashCodeResult = fileDTO.hashCode();
        assertEquals(expectedHashCodeResult, fileDTO.hashCode());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link FileDTO#equals(Object)}
     *   <li>{@link FileDTO#hashCode()}
     * </ul>
     */
    @Test
    void testEquals3() throws UnsupportedEncodingException {
        FileDTO fileDTO = new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8"));
        FileDTO fileDTO2 = new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8"));

        assertEquals(fileDTO, fileDTO2);
        int expectedHashCodeResult = fileDTO.hashCode();
        assertEquals(expectedHashCodeResult, fileDTO2.hashCode());
    }

    /**
     * Method under test: {@link FileDTO#equals(Object)}
     */
    @Test
    void testEquals4() throws UnsupportedEncodingException {
        FileDTO fileDTO = new FileDTO(2L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8"));
        assertNotEquals(fileDTO, new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Method under test: {@link FileDTO#equals(Object)}
     */
    @Test
    void testEquals5() throws UnsupportedEncodingException {
        FileDTO fileDTO = new FileDTO(1L, "Name", null, "AXAXAXAX".getBytes("UTF-8"));
        assertNotEquals(fileDTO, new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Method under test: {@link FileDTO#equals(Object)}
     */
    @Test
    void testEquals6() throws UnsupportedEncodingException {
        FileDTO fileDTO = new FileDTO(1L, "Name", "Extension", new byte[]{1, 'X', 'A', 'X', 'A', 'X', 'A', 'X'});
        assertNotEquals(fileDTO, new FileDTO(1L, "Name", "Extension", "AXAXAXAX".getBytes("UTF-8")));
    }
}

