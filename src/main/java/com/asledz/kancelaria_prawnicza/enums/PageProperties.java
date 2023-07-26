package com.asledz.kancelaria_prawnicza.enums;

import java.util.HashMap;
import java.util.Map;

public enum PageProperties {
    PAGE_NUMBER("page"),
    PAGE_SIZE("page_size");

    public final String name;

    private static final Map<String, PageProperties> BY_LABEL = new HashMap<>();

    static {
        for (PageProperties e : values()) {
            BY_LABEL.put(e.name, e);
        }
    }

    PageProperties(String contentType
    ) {
        this.name = contentType;
    }

    public static PageProperties valueOfColumnLabels(String label) {
        return BY_LABEL.get(label);
    }
}
