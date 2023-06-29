package com.asledz.kancelaria_prawnicza.dto;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

import java.util.List;

public record FilterAndSortParameters(
        List<Query> filterQueries,
        List<SortField> sortFields
) {
}
