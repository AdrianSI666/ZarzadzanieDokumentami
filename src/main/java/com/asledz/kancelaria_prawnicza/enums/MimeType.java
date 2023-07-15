package com.asledz.kancelaria_prawnicza.enums;

import java.util.HashMap;
import java.util.Map;

public enum MimeType {
    DOC("application/msword"),
    PDF("application/pdf"),
    ODF("application/vnd.oasis.opendocument.text"),
    HTML("text/html"),
    XML("text/xml"),
    UOF("application/octet-stream"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    TXT("text/plain"),
    RTF("application/rtf");
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
