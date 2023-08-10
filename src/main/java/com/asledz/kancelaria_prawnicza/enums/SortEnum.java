package com.asledz.kancelaria_prawnicza.enums;

import java.util.HashMap;
import java.util.Map;

public enum SortEnum {
    SORT_TITLE("sort_title"),
    SORT_DATE("sort_date"),
    SORT_COST("sort_cost"),
    SORT_PAID("sort_paid"),
    SORT_TYPE("sort_type"),
    NULL("null");
    public final String value;

    private static final Map<String, SortEnum> BY_LABEL = new HashMap<>();

    static {
        for (SortEnum e : values()) {
            BY_LABEL.put(e.value, e);
        }
    }

    SortEnum(String contentType
    ) {
        this.value = contentType;
    }

    public static SortEnum valueOfSort(String label) {
        return BY_LABEL.get(label);
    }
}
