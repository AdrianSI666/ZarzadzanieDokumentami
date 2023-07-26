package com.asledz.kancelaria_prawnicza.enums;

import java.util.HashMap;
import java.util.Map;

public enum ColumnLabels {
    TEXT("text"),
    TITLE("document.title"),
    DATE("document.date"),
    COST("document.cost"),
    PAID("document.paid"),
    TYPE("document.type.name"),
    TYPE_ID("document.type.typeId"),
    OWNER_ID("document.owner.ownerId");

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
