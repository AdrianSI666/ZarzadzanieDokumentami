package com.asledz.kancelaria_prawnicza.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class MimeTypeTest {
    /**
     * Method under test: {@link MimeType#valueOfMimeType(String)}
     */
    @Test
    void testValueOfMimeType() {
        assertNull(MimeType.valueOfMimeType("Label"));
        assertEquals(MimeType.DOC, MimeType.valueOfMimeType("application/msword"));
        assertEquals(MimeType.UOF, MimeType.valueOfMimeType("application/octet-stream"));
        assertEquals(MimeType.PDF, MimeType.valueOfMimeType("application/pdf"));
        assertEquals(MimeType.RTF, MimeType.valueOfMimeType("application/rtf"));
        assertEquals(MimeType.ODF, MimeType.valueOfMimeType("application/vnd.oasis.opendocument.text"));
        assertEquals(MimeType.DOCX,
                MimeType.valueOfMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        assertEquals(MimeType.NULL, MimeType.valueOfMimeType("null"));
    }
}

