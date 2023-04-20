package com.asledz.kancelaria_prawnicza.utilis;

import java.util.HashMap;
import java.util.Map;

public enum MimeType {
    DOC("application/msword"),
    PDF("application/pdf"),
    ODF("application/vnd.oasis.opendocument.text");
    public final String contentType;

    private static final Map<String, MimeType> BY_LABEL = new HashMap<>();

    static {
        for (MimeType e: values()) {
            BY_LABEL.put(e.contentType, e);
        }
    }
    MimeType(String contentType
    ) {

        this.contentType = contentType;
    }

    public static MimeType valueOfMimeType(String label) {
        return BY_LABEL.get(label);
    }
}
