package com.asledz.kancelaria_prawnicza.search;

import com.asledz.kancelaria_prawnicza.domain.File;
import com.asledz.kancelaria_prawnicza.dto.FilterAndSortParameters;
import com.asledz.kancelaria_prawnicza.enums.FilterAndSort;
import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.RangeMatchingContext;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.asledz.kancelaria_prawnicza.enums.ColumnLabels.*;
import static com.asledz.kancelaria_prawnicza.enums.FilterAndSort.FILTER_TEXT;

@Component
public class QueriesGenerator {
    private QueriesGenerator() {
    }

    public static FilterAndSortParameters extractQueriesFromMultiMap(QueryBuilder queryBuilder, SearchUtils searchUtils, MultiValueMap<String, String> parameters) {
        List<Query> filterQueries = new ArrayList<>();
        List<SortField> sortFields = new ArrayList<>();
        parameters.forEach((key, values) -> {
            switch (FilterAndSort.valueOfFilterAndSort(key)) {
                case FILTER_TEXT -> {
                    String prompt = values.get(0);
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, TEXT.path, prompt)) {
                        filterQueries.add(queryBuilder
                                .keyword()
                                .fuzzy()
                                .withEditDistanceUpTo(2)
                                .withPrefixLength(1)
                                .onFields(TEXT.path).andField(TITLE.path)
                                .matching(prompt)
                                .createQuery());
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on text field: %s with given prompt: %s"
                                .formatted(FILTER_TEXT.value, values.toString()));
                    }
                }
                case FILTER_TITLE -> {
                    String prompt = values.get(0);
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, TITLE.path, prompt)) {
                        filterQueries.add(queryBuilder
                                .keyword()
                                .fuzzy()
                                .withEditDistanceUpTo(2)
                                .withPrefixLength(1)
                                .onFields(TITLE.path)
                                .matching(prompt)
                                .createQuery());
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on title field: %s with given prompt: %s"
                                .formatted(TITLE.path, values.toString()));
                    }
                }
                case FILTER_DATE -> {
                    Instant dateFrom = Instant.parse(values.get(0));
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, DATE.path, String.valueOf(dateFrom))) {
                        RangeMatchingContext.FromRangeContext<Instant> from = queryBuilder
                                .range()
                                .onField(DATE.path)
                                .from(dateFrom);
                        if (values.size() == 2) {
                            Instant dateTo = Instant.parse(values.get(1));
                            filterQueries.add(from.to(dateTo).createQuery());
                        } else {
                            filterQueries.add(from.to(null).createQuery());
                        }
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on date field: %s with given range: %s"
                                .formatted(DATE.path, values.toString()));
                    }
                }
                case FILTER_DATE_BEFORE -> {
                    Instant dateFrom = Instant.parse(values.get(0));
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, DATE.path, String.valueOf(dateFrom))) {
                        filterQueries.add(queryBuilder
                                .range()
                                .onField(DATE.path)
                                .below(dateFrom)
                                .createQuery());
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on date field: %s from date: %s"
                                .formatted(DATE.path, values.toString()));
                    }
                }
                case FILTER_COST -> {
                    double costFrom = Double.parseDouble(values.get(0));
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, COST.path, String.valueOf(costFrom))) {
                        RangeMatchingContext.FromRangeContext<Double> from = queryBuilder
                                .range()
                                .onField(COST.path)
                                .from(costFrom);
                        if (values.size() == 2) {
                            double costTo = Double.parseDouble(values.get(1));
                            filterQueries.add(from.to(costTo).createQuery());
                        } else {
                            filterQueries.add(from.to(costFrom + 0.99).createQuery());
                        }
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on cost field: %s with given number: %s"
                                .formatted(COST.path, values.toString()));
                    }
                }
                case FILTER_COST_TO -> {
                    double costTo = Double.parseDouble(values.get(0));
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, COST.path, String.valueOf(costTo))) {
                        filterQueries.add(queryBuilder
                                .range()
                                .onField(COST.path)
                                .below(costTo).createQuery());
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on cost field: %s with given number: %s"
                                .formatted(COST.path, values.toString()));
                    }
                }
                case FILTER_PAID -> {
                    boolean paid = Boolean.parseBoolean(values.get(0));
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, PAID.path, String.valueOf(paid))) {
                        filterQueries.add(queryBuilder
                                .keyword()
                                .onField(PAID.path)
                                .matching(paid)
                                .createQuery());
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on boolean paid field: %s with given prompt: %s"
                                .formatted(PAID.path, values.toString()));
                    }
                }
                case FILTER_TYPE_ID -> {
                    long id = Long.parseLong(values.get(0));
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, TYPE_ID.path, String.valueOf(values.get(0)))) {
                        filterQueries.add(queryBuilder
                                .keyword()
                                .onField(TYPE_ID.path)
                                .matching(id)
                                .createQuery());
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on type id field: %s with given prompt: %s"
                                .formatted(TYPE_ID.path, values.toString()));
                    }
                }
                case FILTER_OWNER_ID -> {
                    long id = Long.parseLong(values.get(0));
                    if (searchUtils.iSearchPossibleOrAlreadyFilteredByAnalyzer(File.class, OWNER_ID.path, String.valueOf(values.get(0)))) {
                        filterQueries.add(queryBuilder
                                .keyword()
                                .onField(OWNER_ID.path)
                                .matching(id)
                                .createQuery());
                    } else {
                        throw new BadRequestException("Couldn't use hibernate search on type id field: %s with given prompt: %s"
                                .formatted(OWNER_ID.path, values.toString()));
                    }
                }
                case SORT_TITLE ->
                        sortFields.add(new SortField(TITLE.path, SortField.Type.STRING, Boolean.parseBoolean(values.get(0))));
                case SORT_DATE ->
                        sortFields.add(new SortField(DATE.path, SortField.Type.LONG, Boolean.parseBoolean(values.get(0))));
                case SORT_COST ->
                        sortFields.add(new SortField(COST.path, SortField.Type.DOUBLE, Boolean.parseBoolean(values.get(0))));
                case SORT_PAID ->
                        sortFields.add(new SortField(PAID.path, SortField.Type.STRING, Boolean.parseBoolean(values.get(0))));
                case SORT_TYPE ->
                        sortFields.add(new SortField(TYPE.path, SortField.Type.STRING, Boolean.parseBoolean(values.get(0))));
            }
        });
        return new FilterAndSortParameters(filterQueries, sortFields);
    }
}
