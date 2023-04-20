package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.DocumentRepository;
import com.asledz.kancelaria_prawnicza.specification.CustomSpecification;
import com.asledz.kancelaria_prawnicza.specification.SearchCriteria;
import com.asledz.kancelaria_prawnicza.utilis.SearchUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DocumentService {
    private final EntityManager entityManager;
    private final DocumentRepository documentRepository;

    private final DTOMapper<Document, DocumentDTO> mapper;
    private final SearchUtils searchUtils;

    protected static final String DOCUMENT_NOT_FOUND_MSG = "Couldn't find document with id: %d";


    public Page<DocumentDTO> getDocuments(Integer page) {
        log.info("Page %d of all documents".formatted(page));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Document> documentPage = documentRepository.findAll(paging);

        if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, "text", "adwokat")) {
            FullTextEntityManager fullTextEntityManager
                    = Search.getFullTextEntityManager(entityManager);
            QueryBuilder queryBuilder = searchUtils.getQueryBuilder(fullTextEntityManager, File.class);
            FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(
                    queryBuilder
                            .keyword()
                            .fuzzy()
                            .withEditDistanceUpTo(2)
                            .withPrefixLength(1)
                            .onFields("text") //"document.title"
                            .matching("wniosek")
                            .createQuery(),
                    File.class);
            List<File> files = fullTextQuery.getResultList();
            files.forEach(file -> log.info("Z fulltextSearch: " + file.getDocument().getId()));
        }

        return documentPage.map(mapper::map);
    }

    public DocumentDTO getDocumentById(Long id) {
        log.info("Getting document with id: %d".formatted(id));
        return mapper.map(documentRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(DOCUMENT_NOT_FOUND_MSG, id))
        ));
    }

    public Page<DocumentDTO> getDocumentsByUser(Long userId, Integer page) {
        log.info("Getting documents by userId: %d".formatted(userId));
        CustomSpecification<Document> documentsByUserId = new CustomSpecification<>(new SearchCriteria("user_id",
                ":",
                userId));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Document> documentPage = documentRepository.findAll(documentsByUserId, paging);
        return documentPage.map(mapper::map);
    }

    public Page<DocumentDTO> getDocumentsByType(Long typeId, Integer page) {
        log.info("Getting documents by typeId: %d".formatted(typeId));
        CustomSpecification<Document> documentsByUserId = new CustomSpecification<>(new SearchCriteria("type_id",
                ":",
                typeId));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Document> documentPage = documentRepository.findAll(documentsByUserId, paging);
        return documentPage.map(mapper::map);
    }

    public Page<DocumentDTO> getDocumentsByTag(Long tagId, Integer page) {
        log.info("Getting documents by tagId: %d".formatted(tagId));
        CustomSpecification<Document> documentsByUserId = new CustomSpecification<>(new SearchCriteria("tag_id",
                ":",
                tagId));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Document> documentPage = documentRepository.findAll(documentsByUserId, paging);
        return documentPage.map(mapper::map);
    }

    //TODO Tutaj trzeba użyć MultipartFile i przez fulltextsearch wczytać z niego wszystkie dane zamiast DTO.
    public DocumentDTO addDocument(DocumentDTO newDocumentInformation) {
        log.info("Adding Document:" + newDocumentInformation);
        Type type = Type.builder()
                .name(newDocumentInformation.typeName())
                .build();
        File file = File.builder()
                .extension("test")
                .build();
        Document document = Document.builder()
                .title(newDocumentInformation.title())
                .date(newDocumentInformation.date())
                .cost(newDocumentInformation.cost())
                .paid(newDocumentInformation.paid())
                .cost(newDocumentInformation.cost())
                .type(type)
                .tags(List.of())
                .file(file).build();
        return mapper.map(documentRepository.save(document));
    }

    public DocumentDTO updateDocument(DocumentDTO updatedDocumentInformation, Long id) {
        log.info("Updating document with id: %d".formatted(id));
        Document oldDocument = documentRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(DOCUMENT_NOT_FOUND_MSG, id)));
        oldDocument.setTitle(updatedDocumentInformation.title());
        oldDocument.setDate(updatedDocumentInformation.date());
        oldDocument.setCost(updatedDocumentInformation.cost());
        oldDocument.setPaid(updatedDocumentInformation.paid());
        return mapper.map(documentRepository.save(oldDocument));
    }

    public void deleteDocument(Long documentId) {
        log.info("Deleting Document with id: %d".formatted(documentId));
        documentRepository.deleteById(documentId);
    }
}
