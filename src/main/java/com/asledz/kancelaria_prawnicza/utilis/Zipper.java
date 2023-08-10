package com.asledz.kancelaria_prawnicza.utilis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Zipper to compress or decompress given bytes to save space in database.
 */
public class Zipper {
    private Zipper() {
    }

    /**
     * @param bytes to compress
     * @return compressed bytes
     * @throws IOException if there was an error reading from bytes
     */
    public static byte[] compress(byte[] bytes) throws IOException {
        byte[] compressedBytes;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
            deflaterOutputStream.write(bytes);
            deflaterOutputStream.close();
            compressedBytes = byteArrayOutputStream.toByteArray();
        }
        return compressedBytes;
    }

    /**
     * @param bytes to decompress
     * @return decompressed bytes
     * @throws IOException if there was an error reading from bytes
     */
    public static byte[] decompress(byte[] bytes) throws IOException {
        byte[] decompressedBytes;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int read;
            while ((read = inflaterInputStream.read()) != -1) {
                byteArrayOutputStream.write(read);
            }
            decompressedBytes = byteArrayOutputStream.toByteArray();
        }
        return decompressedBytes;
    }
}
