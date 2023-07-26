package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.dto.CountDocumentsByDay;
import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.dto.FilterAndSortParameters;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import com.asledz.kancelaria_prawnicza.exception.NotFoundException;
import com.asledz.kancelaria_prawnicza.mapper.DTOMapper;
import com.asledz.kancelaria_prawnicza.repository.DocumentRepository;
import com.asledz.kancelaria_prawnicza.search.QueriesGenerator;
import com.asledz.kancelaria_prawnicza.search.SearchUtils;
import com.asledz.kancelaria_prawnicza.search.SortGenerator;
import com.asledz.kancelaria_prawnicza.specification.CustomSpecification;
import com.asledz.kancelaria_prawnicza.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_NUMBER;
import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_SIZE;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DocumentService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final DocumentRepository documentRepository;
    private final TypeService typeService;
    private final DTOMapper<Document, DocumentDTO> mapper;
    private final SearchUtils searchUtils;
    protected static final String DOCUMENT_NOT_FOUND_MSG = "Couldn't find document with id: %d";


    public Page<DocumentDTO> getDocuments(Map<String, String> pageProperties) {
        int page = 0;
        if (pageProperties.containsKey(PAGE_NUMBER.name)) {
            page = Integer.parseInt(pageProperties.get(PAGE_NUMBER.name)) - 1;
            pageProperties.remove(PAGE_NUMBER.name);
        }
        log.info("Page %d of all documents by Parameters".formatted(page));
        int pageSize = 5;
        if (pageProperties.containsKey(PAGE_SIZE.name)) {
            pageSize = Integer.parseInt(pageProperties.get(PAGE_SIZE.name));
            pageProperties.remove(PAGE_SIZE.name);
        }
        Pageable paging;
        if (pageProperties.size() > 0) {
            List<Sort> sortList = SortGenerator.getSortsFromMap(pageProperties);
            Sort result;
            if (sortList.isEmpty()) {
                result = Sort.by("id").descending();
            } else {
                result = sortList.get(0);
                for (int i = 1; i < sortList.size(); i++) {
                    result.and(sortList.get(i));
                }
            }
            paging = PageRequest.of(page, pageSize, result);
        } else {
            paging = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        }
        Page<Document> documentPage = documentRepository.findAll(paging);
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
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("id"));
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
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("id"));
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
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("id"));
        Page<Document> documentPage = documentRepository.findAll(documentsByUserId, paging);
        return documentPage.map(mapper::map);
    }

    public Page<DocumentDTO> getDocumentsByText(Integer page, String prompt) {
        log.info("Page %d of all documents by Text".formatted(page));
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("id"));
        Page<Document> documentPage;
        if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, "text", prompt)) {
            FullTextEntityManager fullTextEntityManager
                    = Search.getFullTextEntityManager(entityManager);
            QueryBuilder queryBuilder = searchUtils.getQueryBuilder(fullTextEntityManager, File.class);
            Query textQuery = queryBuilder
                    .keyword()
                    .fuzzy()
                    .withEditDistanceUpTo(2)
                    .withPrefixLength(1)
                    .onFields("text") //"document.title"
                    .matching(prompt)
                    .createQuery();
            FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(textQuery,
                            File.class)
                    .setFirstResult(pageSize * page)
                    .setMaxResults(pageSize);
            @SuppressWarnings("unchecked")
            List<File> files = fullTextQuery.getResultList();
            documentPage = new PageImpl<>(files.stream().map(File::getDocument).toList(), paging, fullTextQuery.getResultSize());
        } else {
            throw new BadRequestException("Couldn't use hibernate search on text field with given prompt: %s".formatted(prompt));
        }
        return documentPage.map(mapper::map);
    }

    public Page<DocumentDTO> getDocumentsByParameters(MultiValueMap<String, String> parameters) {
        int page = 0;
        if (parameters.containsKey(PAGE_NUMBER.name)) {
            page = Integer.parseInt(parameters.get(PAGE_NUMBER.name).get(0)) - 1;
            parameters.remove(PAGE_NUMBER.name);
        }
        log.info("Page %d of all documents by Parameters".formatted(page));
        int pageSize = 5;
        if (parameters.containsKey(PAGE_SIZE.name)) {
            pageSize = Integer.parseInt(parameters.get(PAGE_SIZE.name).get(0));
            parameters.remove(PAGE_SIZE.name);
        }
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Document> documentPage;
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = searchUtils.getQueryBuilder(fullTextEntityManager, File.class);
        FilterAndSortParameters filterAndSortParameters = QueriesGenerator.extractQueriesFromMultiMap(queryBuilder, searchUtils, parameters);
        if (filterAndSortParameters.filterQueries().isEmpty()) {
            throw new BadRequestException("Can't use filtering without any parameters. " + parameters);
        }
        BooleanJunction<BooleanJunction> booleanJunction = queryBuilder.bool();
        filterAndSortParameters.filterQueries().forEach(booleanJunction::must);
        Query combinedQuery = booleanJunction.createQuery();
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(combinedQuery,
                        File.class)
                .setFirstResult(pageSize * page)
                .setMaxResults(pageSize);
        if (!filterAndSortParameters.sortFields().isEmpty()) {
            List<SortField> sortFields = filterAndSortParameters.sortFields();
            org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(sortFields.toArray(new SortField[0]));
            fullTextQuery.setSort(sort);
        }
        @SuppressWarnings("unchecked")
        List<File> files = fullTextQuery.getResultList();
        documentPage = new PageImpl<>(files.stream().map(File::getDocument).toList(), paging, fullTextQuery.getResultSize());
        return documentPage.map(mapper::map);
    }

    public DocumentDTO updateDocument(DocumentDTO updatedDocumentInformation, Long documentId) {
        log.info("Updating document with id: %d".formatted(documentId));
        Document oldDocument = documentRepository.findById(documentId).orElseThrow(
                () -> new NotFoundException(String.format(DOCUMENT_NOT_FOUND_MSG, documentId)));
        if (updatedDocumentInformation.typeId() > 0) {
            Type type = typeService.getTypeById(updatedDocumentInformation.typeId());
            oldDocument.setType(type);
        }
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

    public List<CountDocumentsByDay> countDocumentsByDay(Long userId) {
        log.info("Counting documents by date for user with id: {}", userId);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Document> documentRoot = criteriaQuery.from(Document.class);
        //Group By + Having, jeśli bez having to usunąć join i w group by zostawić tylko date
        Join<Object, Object> productsJoin = documentRoot.join("owner");
        criteriaQuery.groupBy(documentRoot.get("date"), productsJoin.get("id"));
        criteriaQuery.having(criteriaBuilder.equal(productsJoin.get("id"), userId));

        criteriaQuery.multiselect(documentRoot.get("date"), criteriaBuilder.count(documentRoot));
        criteriaQuery.orderBy(criteriaBuilder.desc(documentRoot.get("date")));
        TypedQuery<Object[]> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Object[]> resultList = typedQuery.getResultList();
        entityManager.close();
        return resultList.stream().map(result -> new CountDocumentsByDay((Instant) result[0], (Long) result[1])).toList();
    }

    public List<DocumentDTO> getDocumentsByUserIdWithoutDate(Long userId) {
        log.info("Getting documents by ownerId: %d".formatted(userId));
        CustomSpecification<Document> documentsByUserId = new CustomSpecification<>(new SearchCriteria("owner_id",
                ":",
                userId));
        List<Document> documents = documentRepository.findAll(documentsByUserId);
        return documents.stream().filter(document -> document.getDate() == null).map(mapper::map).toList();
    }
}
