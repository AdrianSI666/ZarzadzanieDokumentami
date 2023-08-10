package com.asledz.kancelaria_prawnicza.utilis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ZipperTest {
    /**
     * Method under test: {@link Zipper#compress(byte[])}
     */
    @Test
    void testCompress() throws IOException {
        assertEquals(18, Zipper.compress("I love you".getBytes("UTF-8")).length);
    }

    /**
     * Method under test: {@link Zipper#decompress(byte[])}
     */
    @Test
    void testDecompress() throws IOException {
        String message = "I love you";
        byte[] normal = message.getBytes(StandardCharsets.UTF_8);
        byte[] compressed = Zipper.compress(normal);
        byte[] decompressed = Zipper.decompress(compressed);
        String readFromBytesAfterDecompressing = new String(decompressed, StandardCharsets.UTF_8);
        assertEquals(message, readFromBytesAfterDecompressing);
        assertEquals(normal.length, decompressed.length);
        assertNotEquals(compressed.length, decompressed.length);
    }
}

