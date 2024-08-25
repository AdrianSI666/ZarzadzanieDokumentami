package com.asledz.kancelaria_prawnicza.enums;

import java.util.HashMap;
import java.util.Map;

public enum ColumnLabels {
    TEXT("file.text"),
    TITLE("title"),
    DATE("date"),
    COST("cost"),
    PAID("paid"),
    TYPE("type.name"),
    TYPE_ID("type.typeId"),
    OWNER_ID("owner.ownerId");

    public final String path;

    private static final Map<String, ColumnLabels> BY_LABEL = new HashMap<>();

    static {
        for (ColumnLabels e : values()) {
            BY_LABEL.put(e.path, e);
        }
    }

    ColumnLabels(String contentType
    ) {
        this.path = contentType;
    }
}
