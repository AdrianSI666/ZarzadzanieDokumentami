package com.asledz.kancelaria_prawnicza.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class FileTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link File#File()}
     *   <li>{@link File#setContent(byte[])}
     *   <li>{@link File#setDocument(Document)}
     *   <li>{@link File#setExtension(String)}
     *   <li>{@link File#setId(Long)}
     *   <li>{@link File#setText(String)}
     *   <li>{@link File#getContent()}
     *   <li>{@link File#getDocument()}
     *   <li>{@link File#getExtension()}
     *   <li>{@link File#getId()}
     *   <li>{@link File#getText()}
     * </ul>
     */
    @Test
    void testConstructor() throws UnsupportedEncodingException {
        File actualFile = new File();
        byte[] content = "AXAXAXAX".getBytes("UTF-8");
        actualFile.setContent(content);
        File file = new File();
        file.setContent("AXAXAXAX".getBytes("UTF-8"));
        Document document = new Document();
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
        Document document3 = new Document();
        document3.setCost(10.0d);
        document3.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document3.setFile(file2);
        document3.setId(1L);
        document3.setOwner(owner2);
        document3.setPaid(true);
        document3.setTitle("Dr");
        document3.setType(type2);
        actualFile.setDocument(document3);
        actualFile.setExtension("Extension");
        actualFile.setId(1L);
        actualFile.setText("Text");
        assertSame(content, actualFile.getContent());
        Document document4 = actualFile.getDocument();
        assertSame(document3, document4);
        File file3 = document4.getFile();
        Document document5 = file3.getDocument();
        assertSame(document2, document5);
        File file4 = document5.getFile();
        assertSame(document, file4.getDocument());
        assertEquals("Extension", actualFile.getExtension());
        assertEquals("Extension", file3.getExtension());
        assertEquals("Extension", file4.getExtension());
        assertEquals(1L, actualFile.getId().longValue());
        assertEquals(1L, file3.getId().longValue());
        assertEquals(1L, file4.getId().longValue());
        assertEquals("Text", actualFile.getText());
        assertEquals("Text", file3.getText());
        assertEquals("Text", file4.getText());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link File#File(Long, String, byte[], String, Document)}
     *   <li>{@link File#File()}
     *   <li>{@link File#setContent(byte[])}
     *   <li>{@link File#setDocument(Document)}
     *   <li>{@link File#setExtension(String)}
     *   <li>{@link File#setId(Long)}
     *   <li>{@link File#setText(String)}
     *   <li>{@link File#getContent()}
     *   <li>{@link File#getDocument()}
     *   <li>{@link File#getExtension()}
     *   <li>{@link File#getId()}
     *   <li>{@link File#getText()}
     * </ul>
     */
    @Test
    void testConstructor2() throws UnsupportedEncodingException {
        byte[] content = "AXAXAXAX".getBytes("UTF-8");

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

        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Name");

        Document document = new Document();
        document.setCost(10.0d);
        document.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document.setFile(file);
        document.setId(1L);
        document.setOwner(owner);
        document.setPaid(true);
        document.setTitle("Dr");
        document.setType(type);

        File file2 = new File();
        file2.setContent("AXAXAXAX".getBytes("UTF-8"));
        file2.setDocument(document);
        file2.setExtension("Extension");
        file2.setId(1L);
        file2.setText("Text");

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
        document2.setFile(file2);
        document2.setId(1L);
        document2.setOwner(owner2);
        document2.setPaid(true);
        document2.setTitle("Dr");
        document2.setType(type2);
        File actualFile = new File(1L, "Extension", content, "Text", document2);
        byte[] content2 = "AXAXAXAX".getBytes("UTF-8");
        actualFile.setContent(content2);
        File file3 = new File();
        file3.setContent("AXAXAXAX".getBytes("UTF-8"));
        Document document3 = new Document();
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
        User owner4 = new User();
        owner4.setDocuments(new ArrayList<>());
        owner4.setEmail("jane.doe@example.org");
        owner4.setId(1L);
        owner4.setName("Name");
        owner4.setPassword("iloveyou");
        owner4.setRoles(new ArrayList<>());
        owner4.setSurname("Doe");
        Type type4 = new Type();
        type4.setDocuments(new ArrayList<>());
        type4.setId(1L);
        type4.setName("Name");
        Document document5 = new Document();
        document5.setCost(10.0d);
        document5.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document5.setFile(file4);
        document5.setId(1L);
        document5.setOwner(owner4);
        document5.setPaid(true);
        document5.setTitle("Dr");
        document5.setType(type4);
        actualFile.setDocument(document5);
        actualFile.setExtension("Extension");
        actualFile.setId(1L);
        actualFile.setText("Text");
        assertSame(content2, actualFile.getContent());
        Document document6 = actualFile.getDocument();
        assertSame(document5, document6);
        File file5 = document6.getFile();
        Document document7 = file5.getDocument();
        assertSame(document4, document7);
        File file6 = document7.getFile();
        assertSame(document3, file6.getDocument());
        assertEquals("Extension", actualFile.getExtension());
        assertEquals("Extension", file5.getExtension());
        assertEquals("Extension", file6.getExtension());
        assertEquals(1L, actualFile.getId().longValue());
        assertEquals(1L, file5.getId().longValue());
        assertEquals(1L, file6.getId().longValue());
        assertEquals("Text", actualFile.getText());
        assertEquals("Text", file5.getText());
        assertEquals("Text", file6.getText());
    }
}

