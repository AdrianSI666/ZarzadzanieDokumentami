package com.asledz.kancelaria_prawnicza.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NewUserDTOTest {
    /**
     * Method under test: {@link NewUserDTO#NewUserDTO(String, String, String, String)}
     */
    @Test
    void testConstructor() {
        NewUserDTO actualNewUserDTO = new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou");

        assertEquals("jane.doe@example.org", actualNewUserDTO.email());
        assertEquals("Doe", actualNewUserDTO.surname());
        assertEquals("iloveyou", actualNewUserDTO.password());
        assertEquals("Name", actualNewUserDTO.name());
    }

    /**
     * Method under test: {@link NewUserDTO#email()}
     */
    @Test
    void testEmail() {
        assertEquals("jane.doe@example.org", (new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou")).email());
    }

    /**
     * Method under test: {@link NewUserDTO#name()}
     */
    @Test
    void testName() {
        assertEquals("Name", (new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou")).name());
    }

    /**
     * Method under test: {@link NewUserDTO#password()}
     */
    @Test
    void testPassword() {
        assertEquals("iloveyou", (new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou")).password());
    }

    /**
     * Method under test: {@link NewUserDTO#surname()}
     */
    @Test
    void testSurname() {
        assertEquals("Doe", (new NewUserDTO("Name", "Doe", "jane.doe@example.org", "iloveyou")).surname());
    }
}

