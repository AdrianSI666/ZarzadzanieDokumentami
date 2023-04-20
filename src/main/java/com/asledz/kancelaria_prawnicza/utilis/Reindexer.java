package com.asledz.kancelaria_prawnicza.utilis;

import com.asledz.kancelaria_prawnicza.domain.File;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.CacheMode;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.batchindexing.impl.SimpleIndexingProgressMonitor;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class Reindexer {
    @PersistenceContext
    private EntityManager entityManager;
    private final MassIndexerProgressMonitor monitor = new SimpleIndexingProgressMonitor();

    @EventListener(ApplicationStartedEvent.class)
    @SneakyThrows
    @Transactional
    public void builIndexOnStartup() {
        log.info("Rebuild index");
        FullTextEntityManager fullTextEntityManager
                = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer()
                .progressMonitor(monitor)
                .startAndWait();
        log.info("Rebuilding index finished");
    }

    @SneakyThrows
    @Transactional
    public void reindex(boolean purge) {
        log.info("Trigger reIndex");
        FullTextEntityManager fullTextEntityManager
                = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager
                .createIndexer(File.class)
                .purgeAllOnStart(purge)
                .batchSizeToLoadObjects(25)
                .cacheMode(CacheMode.NORMAL)
                .threadsToLoadObjects(1)
                .idFetchSize(150)
                .transactionTimeout(1800)
                .progressMonitor(monitor)
                .optimizeAfterPurge(purge)
                .startAndWait();
    }
}
