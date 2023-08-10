package com.asledz.kancelaria_prawnicza.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class UserTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link User#User()}
     *   <li>{@link User#setDocuments(Collection)}
     *   <li>{@link User#setEmail(String)}
     *   <li>{@link User#setId(Long)}
     *   <li>{@link User#setName(String)}
     *   <li>{@link User#setPassword(String)}
     *   <li>{@link User#setRoles(Collection)}
     *   <li>{@link User#setSurname(String)}
     *   <li>{@link User#getDocuments()}
     *   <li>{@link User#getEmail()}
     *   <li>{@link User#getId()}
     *   <li>{@link User#getName()}
     *   <li>{@link User#getPassword()}
     *   <li>{@link User#getRoles()}
     *   <li>{@link User#getSurname()}
     * </ul>
     */
    @Test
    void testConstructor() {
        User actualUser = new User();
        ArrayList<Document> documents = new ArrayList<>();
        actualUser.setDocuments(documents);
        actualUser.setEmail("jane.doe@example.org");
        actualUser.setId(1L);
        actualUser.setName("Name");
        actualUser.setPassword("iloveyou");
        ArrayList<Role> roles = new ArrayList<>();
        actualUser.setRoles(roles);
        actualUser.setSurname("Doe");
        Collection<Document> documents2 = actualUser.getDocuments();
        assertSame(documents, documents2);
        assertEquals(roles, documents2);
        assertEquals("jane.doe@example.org", actualUser.getEmail());
        assertEquals(1L, actualUser.getId().longValue());
        assertEquals("Name", actualUser.getName());
        assertEquals("iloveyou", actualUser.getPassword());
        Collection<Role> roles2 = actualUser.getRoles();
        assertSame(roles, roles2);
        assertEquals(documents2, roles2);
        assertEquals("Doe", actualUser.getSurname());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link User#User(Long, String, String, String, String, Collection, Collection)}
     *   <li>{@link User#setDocuments(Collection)}
     *   <li>{@link User#setEmail(String)}
     *   <li>{@link User#setId(Long)}
     *   <li>{@link User#setName(String)}
     *   <li>{@link User#setPassword(String)}
     *   <li>{@link User#setRoles(Collection)}
     *   <li>{@link User#setSurname(String)}
     *   <li>{@link User#getDocuments()}
     *   <li>{@link User#getEmail()}
     *   <li>{@link User#getId()}
     *   <li>{@link User#getName()}
     *   <li>{@link User#getPassword()}
     *   <li>{@link User#getRoles()}
     *   <li>{@link User#getSurname()}
     * </ul>
     */
    @Test
    void testConstructor2() {
        ArrayList<Role> roles = new ArrayList<>();
        ArrayList<Document> documents = new ArrayList<>();
        User actualUser = new User(1L, "Name", "Doe", "jane.doe@example.org", "iloveyou", roles, documents);
        ArrayList<Document> documents2 = new ArrayList<>();
        actualUser.setDocuments(documents2);
        actualUser.setEmail("jane.doe@example.org");
        actualUser.setId(1L);
        actualUser.setName("Name");
        actualUser.setPassword("iloveyou");
        ArrayList<Role> roles2 = new ArrayList<>();
        actualUser.setRoles(roles2);
        actualUser.setSurname("Doe");
        Collection<Document> documents3 = actualUser.getDocuments();
        assertSame(documents2, documents3);
        assertEquals(roles, documents3);
        assertEquals(documents, documents3);
        assertEquals(roles2, documents3);
        assertEquals("jane.doe@example.org", actualUser.getEmail());
        assertEquals(1L, actualUser.getId().longValue());
        assertEquals("Name", actualUser.getName());
        assertEquals("iloveyou", actualUser.getPassword());
        Collection<Role> roles3 = actualUser.getRoles();
        assertSame(roles2, roles3);
        assertEquals(roles, roles3);
        assertEquals(documents, roles3);
        assertEquals(documents3, roles3);
        assertEquals("Doe", actualUser.getSurname());
    }
}

