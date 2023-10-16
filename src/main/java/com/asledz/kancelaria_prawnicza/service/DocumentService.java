package com.asledz.kancelaria_prawnicza.service;

import com.asledz.kancelaria_prawnicza.domain.Document;
import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.domain.Type;
import com.asledz.kancelaria_prawnicza.domain.User;
import com.asledz.kancelaria_prawnicza.dto.DocumentDTO;
import com.asledz.kancelaria_prawnicza.dto.FilterAndSortParameters;
import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import com.asledz.kancelaria_prawnicza.exception.ForbiddenException;
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
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_NUMBER;
import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_SIZE;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final TypeService typeService;
    private final UserService userService;
    private final DTOMapper<Document, DocumentDTO> mapper;
    private final SearchUtils searchUtils;
    protected static final String DOCUMENT_NOT_FOUND_MSG = "Couldn't find document with id: %d";


    public Page<DocumentDTO> getDocuments(Map<String, String> pageProperties) {
        int page = 0;
        if (pageProperties.containsKey(PAGE_NUMBER.name)) {
            page = Integer.parseInt(pageProperties.get(PAGE_NUMBER.name)) - 1;
            pageProperties.remove(PAGE_NUMBER.name);
        }
        int pageSize = 5;
        if (pageProperties.containsKey(PAGE_SIZE.name)) {
            pageSize = Integer.parseInt(pageProperties.get(PAGE_SIZE.name));
            pageProperties.remove(PAGE_SIZE.name);
        }
        log.info("Page %d of all documents".formatted(page));
        Pageable paging;
        if (pageProperties.size() > 0) {
            List<Sort> sortList = SortGenerator.getSortsFromMap(pageProperties);
            Sort result = sortList.get(0);
            for (int i = 1; i < sortList.size(); i++) {
                result = result.and(sortList.get(i));
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

    /**
     * @param parameters MultiValueMap containing keys from FilterAndSortEnum values and
     *                   list of values corresponding to them. It's done like this, because
     *                   some keys can correspond to two values like date can have one value and
     *                   be a single point or have two values and be a range.
     * @return Page of DocumentDTO containing results from Hibernate Search queries generated on
     * given parameter.
     */
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

        FullTextEntityManager fullTextEntityManager = searchUtils.getFullTextEntityManager();
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
        List<SortField> sortFields;
        if (!filterAndSortParameters.sortFields().isEmpty()) {
            sortFields = filterAndSortParameters.sortFields();
        } else {
            sortFields = List.of(new SortField("document.documentId", SortField.Type.LONG, true));
        }
        org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(sortFields.toArray(new SortField[0]));
        fullTextQuery.setSort(sort);
        @SuppressWarnings("unchecked")
        List<File> files = fullTextQuery.getResultList();
        Page<Document> documentPage = new PageImpl<>(files.stream().map(File::getDocument).toList(), paging, fullTextQuery.getResultSize());
        return documentPage.map(mapper::map);
    }

    public DocumentDTO updateDocument(DocumentDTO updatedDocumentInformation, Long documentId, String username) {
        log.info("Updating document with id: %d".formatted(documentId));
        Document oldDocument = documentRepository.findById(documentId).orElseThrow(
                () -> new NotFoundException(String.format(DOCUMENT_NOT_FOUND_MSG, documentId)));
        UserDTO userDTO = userService.getUserByEmail(username);
        if (!Objects.equals(userDTO.id(), oldDocument.getOwner().getId()))
            throw new ForbiddenException("Logged in user is not owner of document and can't change it's data. Document id: %s and UserId: %s"
                    .formatted(documentId, userDTO.id()));
        if (updatedDocumentInformation.typeId() != null && updatedDocumentInformation.typeId() > 0) {
            Type type = typeService.getTypeById(updatedDocumentInformation.typeId());
            oldDocument.setType(type);
        }
        if (updatedDocumentInformation.title() != null) oldDocument.setTitle(updatedDocumentInformation.title());
        if (updatedDocumentInformation.date() != null) oldDocument.setDate(updatedDocumentInformation.date());
        if (updatedDocumentInformation.cost() != null) oldDocument.setCost(updatedDocumentInformation.cost());
        if (updatedDocumentInformation.paid() != null) oldDocument.setPaid(updatedDocumentInformation.paid());
        return mapper.map(documentRepository.save(oldDocument));
    }

    public void deleteDocument(Long documentId, String username) {
        log.info("Deleting Document with id: %d".formatted(documentId));
        Document document = documentRepository.findById(documentId).orElseThrow(
                () -> new NotFoundException(String.format(DOCUMENT_NOT_FOUND_MSG, documentId)));
        UserDTO userDTO = userService.getUserByEmail(username);
        if (!Objects.equals(userDTO.id(), document.getOwner().getId()))
            throw new ForbiddenException("Logged in user is not owner of document so can't delete this document. Document id: %s and UserId: %s"
                    .formatted(documentId, userDTO.id()));
        documentRepository.delete(document);
    }

    /**
     * @param userId - id of user that owns documents
     * @return List of documents that given user owns and are not given dates. Used to give
     * user an option to change null values in date.
     */
    public Page<DocumentDTO> getDocumentsByUserIdWithoutDate(Long userId, Integer page) {
        if (page == null) page = 1;
        page--;
        int pageSize = 25;
        Specification<Document> documentsByUserId = new CustomSpecification<Document>(new SearchCriteria("owner_id",
                ":",
                userId)).and(new CustomSpecification<>(new SearchCriteria("date",
                ":",
                "null")));
        /*CustomSpecification<Document> test = new CustomSpecification<>(new SearchCriteria("date",
                ":",
                "null"));
        var test2 = documentRepository.findAll(test.and(new CustomSpecification<>(new SearchCriteria("owner_id",
                ":",
                userId))));
        var test1 = documentRepository.findAll(documentsByUserId).stream().filter(document -> document.getDate() == null).map(mapper::map).toList();*/

        Pageable paging = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return documentRepository.findAll(documentsByUserId, paging).map(mapper::map);
    }
}
