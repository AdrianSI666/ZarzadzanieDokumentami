package com.asledz.kancelaria_prawnicza.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class DocumentTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link Document#Document()}
     *   <li>{@link Document#setCost(Double)}
     *   <li>{@link Document#setDate(Instant)}
     *   <li>{@link Document#setFile(File)}
     *   <li>{@link Document#setId(Long)}
     *   <li>{@link Document#setOwner(User)}
     *   <li>{@link Document#setPaid(Boolean)}
     *   <li>{@link Document#setTitle(String)}
     *   <li>{@link Document#setType(Type)}
     *   <li>{@link Document#toString()}
     *   <li>{@link Document#getCost()}
     *   <li>{@link Document#getFile()}
     *   <li>{@link Document#getDate()}
     *   <li>{@link Document#getId()}
     *   <li>{@link Document#getOwner()}
     *   <li>{@link Document#getPaid()}
     *   <li>{@link Document#getTitle()}
     *   <li>{@link Document#getType()}
     * </ul>
     */
    @Test
    void testConstructor() throws UnsupportedEncodingException {
        Document actualDocument = new Document();
        actualDocument.setCost(10.0d);
        Instant date = LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        actualDocument.setDate(date);
        Document document = new Document();
        document.setCost(10.0d);
        document.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document.setFile(new File());
        document.setId(1L);
        document.setOwner(new User());
        document.setPaid(true);
        document.setTitle("Dr");
        document.setType(new Type());
        File file = new File();
        file.setContent("AXAXAXAX".getBytes("UTF-8"));
        file.setDocument(document);
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
        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Name");
        Document document2 = new Document();
        document2.setCost(10.0d);
        document2.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document2.setFile(file);
        document2.setId(1L);
        document2.setOwner(owner);
        document2.setPaid(true);
        document2.setTitle("Dr");
        document2.setType(type);
        File file2 = new File();
        file2.setContent("AXAXAXAX".getBytes("UTF-8"));
        file2.setDocument(document2);
        file2.setExtension("Extension");
        file2.setId(1L);
        file2.setText("Text");
        actualDocument.setFile(file2);
        actualDocument.setId(1L);
        User owner2 = new User();
        owner2.setDocuments(new ArrayList<>());
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setName("Name");
        owner2.setPassword("iloveyou");
        owner2.setRoles(new ArrayList<>());
        owner2.setSurname("Doe");
        actualDocument.setOwner(owner2);
        actualDocument.setPaid(true);
        actualDocument.setTitle("Dr");
        Type type2 = new Type();
        type2.setDocuments(new ArrayList<>());
        type2.setId(1L);
        type2.setName("Name");
        actualDocument.setType(type2);
        actualDocument.toString();
        assertEquals(10.0d, actualDocument.getCost().doubleValue());
        File file3 = actualDocument.getFile();
        Document document3 = file3.getDocument();
        assertEquals(10.0d, document3.getCost().doubleValue());
        Instant expectedDate = date.EPOCH;
        Instant date2 = actualDocument.getDate();
        assertSame(expectedDate, date2);
        assertSame(date2, document3.getDate());
        assertSame(file2, file3);
        assertSame(file, document3.getFile());
        assertEquals(1L, actualDocument.getId().longValue());
        assertEquals(1L, document3.getId().longValue());
        assertSame(owner2, actualDocument.getOwner());
        assertSame(owner, document3.getOwner());
        assertTrue(actualDocument.getPaid());
        assertTrue(document3.getPaid());
        assertEquals("Dr", actualDocument.getTitle());
        assertEquals("Dr", document3.getTitle());
        assertSame(type2, actualDocument.getType());
        assertSame(type, document3.getType());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link Document#Document(Long, String, Instant, Double, Boolean, User, Type, File)}
     *   <li>{@link Document#Document()}
     *   <li>{@link Document#setCost(Double)}
     *   <li>{@link Document#setDate(Instant)}
     *   <li>{@link Document#setFile(File)}
     *   <li>{@link Document#setId(Long)}
     *   <li>{@link Document#setOwner(User)}
     *   <li>{@link Document#setPaid(Boolean)}
     *   <li>{@link Document#setTitle(String)}
     *   <li>{@link Document#setType(Type)}
     *   <li>{@link Document#toString()}
     *   <li>{@link Document#getCost()}
     *   <li>{@link Document#getFile()}
     *   <li>{@link Document#getDate()}
     *   <li>{@link Document#getId()}
     *   <li>{@link Document#getOwner()}
     *   <li>{@link Document#getPaid()}
     *   <li>{@link Document#getTitle()}
     *   <li>{@link Document#getType()}
     * </ul>
     */
    @Test
    void testConstructor2() throws UnsupportedEncodingException {
        Instant date = LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();

        User owner = new User();
        owner.setDocuments(new ArrayList<>());
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");
        owner.setPassword("iloveyou");
        owner.setRoles(new ArrayList<>());
        owner.setSurname("Doe");

        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Name");

        Document document = new Document();
        document.setCost(10.0d);
        document.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document.setFile(new File());
        document.setId(1L);
        document.setOwner(new User());
        document.setPaid(true);
        document.setTitle("Dr");
        document.setType(new Type());

        File file = new File();
        file.setContent("AXAXAXAX".getBytes("UTF-8"));
        file.setDocument(document);
        file.setExtension("Extension");
        file.setId(1L);
        file.setText("Text");

        User owner2 = new User();
        owner2.setDocuments(new ArrayList<>());
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setName("Name");
        owner2.setPassword("iloveyou");
        owner2.setRoles(new ArrayList<>());
        owner2.setSurname("Doe");

        Type type2 = new Type();
        type2.setDocuments(new ArrayList<>());
        type2.setId(1L);
        type2.setName("Name");

        Document document2 = new Document();
        document2.setCost(10.0d);
        document2.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document2.setFile(file);
        document2.setId(1L);
        document2.setOwner(owner2);
        document2.setPaid(true);
        document2.setTitle("Dr");
        document2.setType(type2);

        File file2 = new File();
        file2.setContent("AXAXAXAX".getBytes("UTF-8"));
        file2.setDocument(document2);
        file2.setExtension("Extension");
        file2.setId(1L);
        file2.setText("Text");
        Document actualDocument = new Document(1L, "Dr", date, 10.0d, true, owner, type, file2);
        actualDocument.setCost(10.0d);
        Instant date2 = LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        actualDocument.setDate(date2);
        Document document3 = new Document();
        document3.setCost(10.0d);
        document3.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document3.setFile(new File());
        document3.setId(1L);
        document3.setOwner(new User());
        document3.setPaid(true);
        document3.setTitle("Dr");
        document3.setType(new Type());
        File file3 = new File();
        file3.setContent("AXAXAXAX".getBytes("UTF-8"));
        file3.setDocument(document3);
        file3.setExtension("Extension");
        file3.setId(1L);
        file3.setText("Text");
        User owner3 = new User();
        owner3.setDocuments(new ArrayList<>());
        owner3.setEmail("jane.doe@example.org");
        owner3.setId(1L);
        owner3.setName("Name");
        owner3.setPassword("iloveyou");
        owner3.setRoles(new ArrayList<>());
        owner3.setSurname("Doe");
        Type type3 = new Type();
        type3.setDocuments(new ArrayList<>());
        type3.setId(1L);
        type3.setName("Name");
        Document document4 = new Document();
        document4.setCost(10.0d);
        document4.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document4.setFile(file3);
        document4.setId(1L);
        document4.setOwner(owner3);
        document4.setPaid(true);
        document4.setTitle("Dr");
        document4.setType(type3);
        File file4 = new File();
        file4.setContent("AXAXAXAX".getBytes("UTF-8"));
        file4.setDocument(document4);
        file4.setExtension("Extension");
        file4.setId(1L);
        file4.setText("Text");
        actualDocument.setFile(file4);
        actualDocument.setId(1L);
        User owner4 = new User();
        owner4.setDocuments(new ArrayList<>());
        owner4.setEmail("jane.doe@example.org");
        owner4.setId(1L);
        owner4.setName("Name");
        owner4.setPassword("iloveyou");
        owner4.setRoles(new ArrayList<>());
        owner4.setSurname("Doe");
        actualDocument.setOwner(owner4);
        actualDocument.setPaid(true);
        actualDocument.setTitle("Dr");
        Type type4 = new Type();
        type4.setDocuments(new ArrayList<>());
        type4.setId(1L);
        type4.setName("Name");
        actualDocument.setType(type4);
        actualDocument.toString();
        assertEquals(10.0d, actualDocument.getCost().doubleValue());
        File file5 = actualDocument.getFile();
        Document document5 = file5.getDocument();
        assertEquals(10.0d, document5.getCost().doubleValue());
        Instant expectedDate = date2.EPOCH;
        Instant date3 = actualDocument.getDate();
        assertSame(expectedDate, date3);
        assertSame(date3, document5.getDate());
        assertSame(file4, file5);
        assertSame(file3, document5.getFile());
        assertEquals(1L, actualDocument.getId().longValue());
        assertEquals(1L, document5.getId().longValue());
        assertSame(owner4, actualDocument.getOwner());
        assertSame(owner3, document5.getOwner());
        assertTrue(actualDocument.getPaid());
        assertTrue(document5.getPaid());
        assertEquals("Dr", actualDocument.getTitle());
        assertEquals("Dr", document5.getTitle());
        assertSame(type4, actualDocument.getType());
        assertSame(type3, document5.getType());
    }
}

