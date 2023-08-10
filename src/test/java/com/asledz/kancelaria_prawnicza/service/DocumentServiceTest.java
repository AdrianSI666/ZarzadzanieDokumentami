package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.enums.SortEnum;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.helper.DataToPage;
import com.asledz.kancelaria_prawnicza.mapper.DocumentDTOMapper;
import com.asledz.kancelaria_prawnicza.mother.DocumentMother;
import com.asledz.kancelaria_prawnicza.mother.UserMother;
import com.asledz.kancelaria_prawnicza.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_NUMBER;
import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_SIZE;
import static com.asledz.kancelaria_prawnicza.service.DocumentService.DOCUMENT_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class DocumentServiceTest {
    @Spy
    private DocumentDTOMapper dTOMapper;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private TypeService typeService;
    @InjectMocks
    private DocumentService documentService;
    Document.DocumentBuilder documentBuilder = DocumentMother.basic(Clock.systemUTC().instant());
    long userId = 1L;
    User.UserBuilder userBuilder = UserMother.basic(userId);

    /**
     * Method under test: {@link DocumentService#getDocuments(Map)}
     */
    @Test
    void testGetDocuments() {
        when(documentRepository.findAll(Mockito.<Pageable>any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertTrue(documentService.getDocuments(new HashMap<>()).toList().isEmpty());
        verify(documentRepository).findAll(Mockito.<Pageable>any());
    }

    /**
     * Method under test: {@link DocumentService#getDocuments(Map)}
     */
    @Test
    void testGetDocumentsDefaultSort() {

        File file = new File();
        file.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file.setExtension("Page %d of all documents by Parameters");
        file.setId(1L);
        file.setText("Page %d of all documents by Parameters");

        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Page %d of all documents by Parameters");

        File file2 = new File();
        file2.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file2.setExtension("Page %d of all documents by Parameters");
        file2.setId(1L);
        file2.setText("Page %d of all documents by Parameters");

        Type type2 = new Type();
        type2.setDocuments(new ArrayList<>());
        type2.setId(1L);
        type2.setName("Page %d of all documents by Parameters");

        Document document1 = documentBuilder.id(1L)
                .owner(userBuilder.build())
                .type(type)
                .file(file).build();
        Document document2 = documentBuilder.id(2L)
                .owner(userBuilder.build())
                .type(type2)
                .file(file2).build();
        Document document3 = documentBuilder.id(3L)
                .owner(userBuilder.build()).build();
        Document document4 = documentBuilder.id(4L)
                .owner(userBuilder.build()).build();

        List<Document> content = List.of(document1, document2, document3, document4);
        int page = 0;
        int pageSize = 3;
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Document> pageImpl = DataToPage.toPage(content, paging);
        List<DocumentDTO> expectedDTOS = pageImpl.map(dTOMapper::map).toList();

        when(documentRepository.findAll(paging)).thenReturn(pageImpl);

        Page<DocumentDTO> documents = documentService.getDocuments(new HashMap<>(Map.of(PAGE_NUMBER.name, "1", PAGE_SIZE.name, "3")));
        List<DocumentDTO> resultDocumentDTO = documents.stream().toList();

        assertEquals(expectedDTOS, resultDocumentDTO);
        assertEquals(pageImpl.getTotalPages(), documents.getTotalPages());
        assertEquals(pageImpl.getTotalElements(), documents.getTotalElements());
    }

    /**
     * Method under test: {@link DocumentService#getDocuments(Map)}
     */
    @Test
    void testGetDocumentsCustomSort() {

        File file = new File();
        file.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file.setExtension("Page %d of all documents by Parameters");
        file.setId(1L);
        file.setText("Page %d of all documents by Parameters");

        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Page %d of all documents by Parameters");

        File file2 = new File();
        file2.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file2.setExtension("Page %d of all documents by Parameters");
        file2.setId(1L);
        file2.setText("Page %d of all documents by Parameters");

        Type type2 = new Type();
        type2.setDocuments(new ArrayList<>());
        type2.setId(1L);
        type2.setName("Page %d of all documents by Parameters");

        String instantExpected = "2023-12-22T10:15:30Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));

        Document document1 = documentBuilder.id(1L)
                .owner(userBuilder.build())
                .date(clock.instant())
                .type(type)
                .file(file).build();
        instantExpected = "2023-11-22T10:15:30Z";
        clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Document document2 = documentBuilder.id(2L)
                .owner(userBuilder.build())
                .date(clock.instant())
                .type(type2)
                .file(file2).build();
        instantExpected = "2023-04-22T10:15:30Z";
        clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Document document3 = documentBuilder.id(3L)
                .owner(userBuilder.build())
                .date(clock.instant())
                .build();
        instantExpected = "2023-12-12T10:15:30Z";
        clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Document document4 = documentBuilder.id(4L)
                .owner(userBuilder.build())
                .date(clock.instant())
                .build();
        instantExpected = "2023-11-22T20:15:30Z";
        clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Document document5 = documentBuilder.id(5L)
                .owner(userBuilder.build())
                .date(clock.instant())
                .build();

        List<Document> content = List.of(document1, document2, document3, document4, document5);
        int page = 0;
        int pageSize = 3;
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "date"));
        Page<Document> pageImpl = DataToPage.toPage(content, paging);
        List<DocumentDTO> expectedDTOS = pageImpl.map(dTOMapper::map).toList();

        when(documentRepository.findAll(paging)).thenReturn(pageImpl);

        Page<DocumentDTO> documents = documentService.getDocuments(new HashMap<>(Map.of(PAGE_NUMBER.name, "1",
                PAGE_SIZE.name, "3",
                SortEnum.SORT_DATE.value, "false")));
        List<DocumentDTO> resultDocumentDTO = documents.stream().toList();

        assertEquals(expectedDTOS, resultDocumentDTO);
        assertEquals(pageImpl.getTotalPages(), documents.getTotalPages());
        assertEquals(pageImpl.getTotalElements(), documents.getTotalElements());
        //Same code, multiple sort values
        Sort sort = Sort.by("title").descending().and(Sort.by("date").ascending());
        paging = PageRequest.of(page, pageSize, sort);
        pageImpl = DataToPage.toPage(content, paging);
        expectedDTOS = pageImpl.map(dTOMapper::map).toList();

        when(documentRepository.findAll(paging)).thenReturn(pageImpl);

        documents = documentService.getDocuments(
                new HashMap<>(Map.of(PAGE_NUMBER.name, "1",
                        PAGE_SIZE.name, "3",
                        SortEnum.SORT_TITLE.value, "true",
                        SortEnum.SORT_DATE.value, "false")));
        resultDocumentDTO = documents.stream().toList();

        assertEquals(expectedDTOS, resultDocumentDTO);
        assertEquals(pageImpl.getTotalPages(), documents.getTotalPages());
        assertEquals(pageImpl.getTotalElements(), documents.getTotalElements());
    }

    /**
     * Method under test: {@link DocumentService#getDocumentById(Long)}
     */
    @Test
    void testGetDocumentById() throws UnsupportedEncodingException {
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
        Optional<Document> ofResult = Optional.of(document2);
        when(documentRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        DocumentDTO documentDTO = new DocumentDTO(1L, "Dr",
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d, true, 1L, "Name");

        assertEquals(documentDTO, documentService.getDocumentById(1L));
        verify(documentRepository).findById(Mockito.<Long>any());
        verify(dTOMapper).map(Mockito.<Document>any());
    }

    /**
     * Method under test: {@link DocumentService#getDocumentById(Long)}
     */
    @Test
    void testGetDocumentByIdThrowNotFound() {
        Long id = 1L;
        given(documentRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> documentService.getDocumentById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(DOCUMENT_NOT_FOUND_MSG, id));
    }

    /**
     * Method under test: {@link DocumentService#updateDocument(DocumentDTO, Long)}
     */
    @Test
    void testUpdateDocument() throws UnsupportedEncodingException {
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
        Optional<Document> ofResult = Optional.of(document2);

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
        when(documentRepository.save(Mockito.<Document>any())).thenReturn(document5);
        when(documentRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        Type type5 = new Type();
        type5.setDocuments(new ArrayList<>());
        type5.setId(1L);
        type5.setName("Name");
        when(typeService.getTypeById(Mockito.<Long>any())).thenReturn(type5);
        DocumentDTO documentDTO = new DocumentDTO(1L, "Dr",
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d, true, 1L, "Name");

        assertEquals(documentDTO,
                documentService.updateDocument(new DocumentDTO(1L, "Dr",
                                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), 10.0d, true, 1L, "Name"),
                        1L));
        verify(documentRepository).save(Mockito.<Document>any());
        verify(documentRepository).findById(Mockito.<Long>any());
        verify(typeService).getTypeById(Mockito.<Long>any());
        verify(dTOMapper).map(Mockito.<Document>any());
    }

    /**
     * Method under test: {@link DocumentService#updateDocument(DocumentDTO, Long)}
     */
    @Test
    void testUpdateDocumentThrowNotFound() {
        Long id = 1L;
        given(documentRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> documentService.updateDocument(any(DocumentDTO.class), id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(DOCUMENT_NOT_FOUND_MSG, id));
    }

    /**
     * Method under test: {@link DocumentService#deleteDocument(Long)}
     */
    @Test
    void testDeleteDocument() {
        doNothing().when(documentRepository).deleteById(Mockito.<Long>any());
        documentService.deleteDocument(1L);
        verify(documentRepository).deleteById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link DocumentService#getDocumentsByUserIdWithoutDate(Long)}
     */
    @Test
    void testGetDocumentsByUserIdWithoutDate() {
        when(documentRepository.findAll(Mockito.<Specification<Document>>any())).thenReturn(new ArrayList<>());
        assertTrue(documentService.getDocumentsByUserIdWithoutDate(1L).isEmpty());
        verify(documentRepository).findAll(Mockito.<Specification<Document>>any());
    }

    /**
     * Method under test: {@link DocumentService#getDocumentsByUserIdWithoutDate(Long)}
     */
    @Test
    void testGetDocumentsByUserIdWithoutDate2() {
        File file = new File();
        file.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file.setDocument(new Document());
        file.setExtension("Getting documents by ownerId: %d");
        file.setId(1L);
        file.setText("Getting documents by ownerId: %d");

        User owner = new User();
        owner.setDocuments(new ArrayList<>());
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Getting documents by ownerId: %d");
        owner.setPassword("iloveyou");
        owner.setRoles(new ArrayList<>());
        owner.setSurname("Doe");

        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Getting documents by ownerId: %d");

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
        file2.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file2.setDocument(document);
        file2.setExtension("Getting documents by ownerId: %d");
        file2.setId(1L);
        file2.setText("Getting documents by ownerId: %d");

        User owner2 = new User();
        owner2.setDocuments(new ArrayList<>());
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setName("Getting documents by ownerId: %d");
        owner2.setPassword("iloveyou");
        owner2.setRoles(new ArrayList<>());
        owner2.setSurname("Doe");

        Type type2 = new Type();
        type2.setDocuments(new ArrayList<>());
        type2.setId(1L);
        type2.setName("Getting documents by ownerId: %d");

        Document document2 = new Document();
        document2.setCost(10.0d);
        document2.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document2.setFile(file2);
        document2.setId(1L);
        document2.setOwner(owner2);
        document2.setPaid(true);
        document2.setTitle("Dr");
        document2.setType(type2);

        ArrayList<Document> documentList = new ArrayList<>();
        documentList.add(document2);
        when(documentRepository.findAll(Mockito.<Specification<Document>>any())).thenReturn(documentList);
        assertTrue(documentService.getDocumentsByUserIdWithoutDate(1L).isEmpty());
        verify(documentRepository).findAll(Mockito.<Specification<Document>>any());
    }

    /**
     * Method under test: {@link DocumentService#getDocumentsByUserIdWithoutDate(Long)}
     */
    @Test
    void testGetDocumentsByUserIdWithoutDate3() {
        File file = new File();
        file.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file.setDocument(new Document());
        file.setExtension("Getting documents by ownerId: %d");
        file.setId(1L);
        file.setText("Getting documents by ownerId: %d");

        User owner = new User();
        owner.setDocuments(new ArrayList<>());
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Getting documents by ownerId: %d");
        owner.setPassword("iloveyou");
        owner.setRoles(new ArrayList<>());
        owner.setSurname("Doe");

        Type type = new Type();
        type.setDocuments(new ArrayList<>());
        type.setId(1L);
        type.setName("Getting documents by ownerId: %d");

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
        file2.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file2.setDocument(document);
        file2.setExtension("Getting documents by ownerId: %d");
        file2.setId(1L);
        file2.setText("Getting documents by ownerId: %d");

        User owner2 = new User();
        owner2.setDocuments(new ArrayList<>());
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setName("Getting documents by ownerId: %d");
        owner2.setPassword("iloveyou");
        owner2.setRoles(new ArrayList<>());
        owner2.setSurname("Doe");

        Type type2 = new Type();
        type2.setDocuments(new ArrayList<>());
        type2.setId(1L);
        type2.setName("Getting documents by ownerId: %d");

        Document document2 = new Document();
        document2.setCost(10.0d);
        document2.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document2.setFile(file2);
        document2.setId(1L);
        document2.setOwner(owner2);
        document2.setPaid(true);
        document2.setTitle("Dr");
        document2.setType(type2);

        File file3 = new File();
        file3.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file3.setDocument(new Document());
        file3.setExtension("owner_id");
        file3.setId(2L);
        file3.setText("owner_id");

        User owner3 = new User();
        owner3.setDocuments(new ArrayList<>());
        owner3.setEmail("john.smith@example.org");
        owner3.setId(2L);
        owner3.setName("owner_id");
        owner3.setPassword("Getting documents by ownerId: %d");
        owner3.setRoles(new ArrayList<>());
        owner3.setSurname("Getting documents by ownerId: %d");

        Type type3 = new Type();
        type3.setDocuments(new ArrayList<>());
        type3.setId(2L);
        type3.setName("owner_id");

        Document document3 = new Document();
        document3.setCost(0.5d);
        document3.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document3.setFile(file3);
        document3.setId(2L);
        document3.setOwner(owner3);
        document3.setPaid(false);
        document3.setTitle("Mr");
        document3.setType(type3);

        File file4 = new File();
        file4.setContent(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1});
        file4.setDocument(document3);
        file4.setExtension("owner_id");
        file4.setId(2L);
        file4.setText("owner_id");

        User owner4 = new User();
        owner4.setDocuments(new ArrayList<>());
        owner4.setEmail("john.smith@example.org");
        owner4.setId(2L);
        owner4.setName("owner_id");
        owner4.setPassword("Getting documents by ownerId: %d");
        owner4.setRoles(new ArrayList<>());
        owner4.setSurname("Getting documents by ownerId: %d");

        Type type4 = new Type();
        type4.setDocuments(new ArrayList<>());
        type4.setId(2L);
        type4.setName("owner_id");

        Document document4 = new Document();
        document4.setCost(0.5d);
        document4.setDate(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        document4.setFile(file4);
        document4.setId(2L);
        document4.setOwner(owner4);
        document4.setPaid(false);
        document4.setTitle("Mr");
        document4.setType(type4);

        ArrayList<Document> documentList = new ArrayList<>();
        documentList.add(document4);
        documentList.add(document2);
        when(documentRepository.findAll(Mockito.<Specification<Document>>any())).thenReturn(documentList);
        assertTrue(documentService.getDocumentsByUserIdWithoutDate(1L).isEmpty());
        verify(documentRepository).findAll(Mockito.<Specification<Document>>any());
    }
}

