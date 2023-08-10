package com.asledz.kancelaria_prawnicza.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class TypeTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link Type#Type()}
     *   <li>{@link Type#setDocuments(Collection)}
     *   <li>{@link Type#setId(Long)}
     *   <li>{@link Type#setName(String)}
     *   <li>{@link Type#getDocuments()}
     *   <li>{@link Type#getId()}
     *   <li>{@link Type#getName()}
     * </ul>
     */
    @Test
    void testConstructor() {
        Type actualType = new Type();
        ArrayList<Document> documents = new ArrayList<>();
        actualType.setDocuments(documents);
        actualType.setId(1L);
        actualType.setName("Name");
        assertSame(documents, actualType.getDocuments());
        assertEquals(1L, actualType.getId().longValue());
        assertEquals("Name", actualType.getName());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link Type#Type(Long, String, Collection)}
     *   <li>{@link Type#setDocuments(Collection)}
     *   <li>{@link Type#setId(Long)}
     *   <li>{@link Type#setName(String)}
     *   <li>{@link Type#getDocuments()}
     *   <li>{@link Type#getId()}
     *   <li>{@link Type#getName()}
     * </ul>
     */
    @Test
    void testConstructor2() {
        ArrayList<Document> documents = new ArrayList<>();
        Type actualType = new Type(1L, "Name", documents);
        ArrayList<Document> documents2 = new ArrayList<>();
        actualType.setDocuments(documents2);
        actualType.setId(1L);
        actualType.setName("Name");
        Collection<Document> documents3 = actualType.getDocuments();
        assertSame(documents2, documents3);
        assertEquals(documents, documents3);
        assertEquals(1L, actualType.getId().longValue());
        assertEquals("Name", actualType.getName());
    }
}

