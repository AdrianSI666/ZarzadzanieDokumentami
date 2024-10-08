package com.asledz.kancelaria_prawnicza.search;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class SearchUtils {
    private final EntityManager entityManager;

    public FullTextEntityManager getFullTextEntityManager() {
        return Search.getFullTextEntityManager(entityManager);
    }

    public QueryBuilder getQueryBuilder(FullTextEntityManager fullTextEntityManager,
                                        Class<?> targetClass) {
        return fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(targetClass)
                .get();
    }
    
    public boolean iSearchPossibleOrAlreadyFilteredByAnalyzer(
            Class<?> entity,
            String fieldName,
            String searchTerm) {
        Analyzer customAnalyzer = getAnalyzer(entity);
        return anyTokensLeftAfterAnalyze(customAnalyzer, fieldName, searchTerm);
    }

    public Analyzer getAnalyzer(Class<?> entity) {
        return getFullTextEntityManager().getSearchFactory().getAnalyzer(entity);
    }

    @SneakyThrows(IOException.class)
    private boolean anyTokensLeftAfterAnalyze(
            Analyzer analyzer,
            String fieldName,
            String searchTerm) {
        try (TokenStream stream = analyzer.tokenStream(fieldName, searchTerm)) {
            stream.reset();
            return stream.incrementToken();
        }
    }
}
