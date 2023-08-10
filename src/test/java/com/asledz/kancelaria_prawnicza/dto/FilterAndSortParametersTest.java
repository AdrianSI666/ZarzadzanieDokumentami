package com.asledz.kancelaria_prawnicza.dto;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FilterAndSortParametersTest {
    /**
     * Method under test: {@link FilterAndSortParameters#FilterAndSortParameters(List, List)}
     */
    @Test
    void testConstructor() {
        ArrayList<Query> filterQueries = new ArrayList<>();
        ArrayList<SortField> sortFields = new ArrayList<>();
        FilterAndSortParameters actualFilterAndSortParameters = new FilterAndSortParameters(filterQueries, sortFields);

        List<Query> filterQueriesResult = actualFilterAndSortParameters.filterQueries();
        assertSame(filterQueries, filterQueriesResult);
        assertEquals(sortFields, filterQueriesResult);
        assertTrue(filterQueriesResult.isEmpty());
        List<SortField> sortFieldsResult = actualFilterAndSortParameters.sortFields();
        assertSame(sortFields, sortFieldsResult);
        assertEquals(filterQueriesResult, sortFieldsResult);
        assertTrue(sortFieldsResult.isEmpty());
    }

    /**
     * Method under test: {@link FilterAndSortParameters#filterQueries()}
     */
    @Test
    void testFilterQueries() {
        ArrayList<Query> filterQueries = new ArrayList<>();
        List<Query> actualFilterQueriesResult = (new FilterAndSortParameters(filterQueries, new ArrayList<>()))
                .filterQueries();
        assertSame(filterQueries, actualFilterQueriesResult);
        assertTrue(actualFilterQueriesResult.isEmpty());
    }

    /**
     * Method under test: {@link FilterAndSortParameters#filterQueries()}
     */
    @Test
    void testFilterQueries2() {
        SortField sortField = new SortField("Field", SortField.Type.SCORE);
        sortField.setBytesComparator(mock(Comparator.class));

        ArrayList<SortField> sortFields = new ArrayList<>();
        sortFields.add(sortField);
        ArrayList<Query> filterQueries = new ArrayList<>();
        List<Query> actualFilterQueriesResult = (new FilterAndSortParameters(filterQueries, sortFields)).filterQueries();
        assertSame(filterQueries, actualFilterQueriesResult);
        assertTrue(actualFilterQueriesResult.isEmpty());
    }

    /**
     * Method under test: {@link FilterAndSortParameters#sortFields()}
     */
    @Test
    void testSortFields() {
        ArrayList<Query> filterQueries = new ArrayList<>();
        ArrayList<SortField> sortFields = new ArrayList<>();
        List<SortField> actualSortFieldsResult = (new FilterAndSortParameters(filterQueries, sortFields)).sortFields();
        assertSame(sortFields, actualSortFieldsResult);
        assertTrue(actualSortFieldsResult.isEmpty());
    }

    /**
     * Method under test: {@link FilterAndSortParameters#sortFields()}
     */
    @Test
    void testSortFields2() {
        SortField sortField = new SortField("Field", SortField.Type.SCORE);
        sortField.setBytesComparator(mock(Comparator.class));

        ArrayList<SortField> sortFields = new ArrayList<>();
        sortFields.add(sortField);
        List<SortField> actualSortFieldsResult = (new FilterAndSortParameters(new ArrayList<>(), sortFields))
                .sortFields();
        assertSame(sortFields, actualSortFieldsResult);
        assertEquals(1, actualSortFieldsResult.size());
    }
}

