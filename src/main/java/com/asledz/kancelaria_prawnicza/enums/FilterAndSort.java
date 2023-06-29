package com.asledz.kancelaria_prawnicza.enums;

import java.util.HashMap;
import java.util.Map;

public enum FilterAndSort {
    FILTER_TEXT("text"),
    FILTER_TITLE("filter_title"),
    FILTER_DATE("filter_date"),
    FILTER_DATE_BEFORE("filter_date_before"),
    FILTER_COST("filter_cost"),
    FILTER_COST_TO("filter_cost_to"),
    FILTER_PAID("filter_paid"),
    FILTER_TYPE_ID("filter_type_id"),
    SORT_TITLE("sort_title"),
    SORT_DATE("sort_date"),
    SORT_COST("sort_cost"),
    SORT_PAID("sort_paid"),
    SORT_TYPE("sort_type");
    public final String value;

    private static final Map<String, FilterAndSort> BY_LABEL = new HashMap<>();

    static {
        for (FilterAndSort e: values()) {
            BY_LABEL.put(e.value, e);
        }
    }
    FilterAndSort(String contentType
    ) {
        this.value = contentType;
    }

    public static FilterAndSort valueOfFilterAndSort(String label) {
        return BY_LABEL.get(label);
    }
}

