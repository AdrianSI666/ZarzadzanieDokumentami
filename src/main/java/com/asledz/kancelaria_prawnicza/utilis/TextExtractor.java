package com.asledz.kancelaria_prawnicza.utilis;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
public class TextExtractor {
    private TextExtractor() {

    }

    public static String extractTextFromFile(InputStream inputStream, String contentType, String fileName) throws IOException {
        log.info("Processing content type: %s".formatted(contentType));
        String textData;
        switch (MimeType.valueOfMimeType(contentType)) {
            case DOC -> {
                POIFSFileSystem fs = new POIFSFileSystem(inputStream);
                HWPFDocument doc = new HWPFDocument(fs);
                WordExtractor we = new WordExtractor(doc);
                textData = Arrays.toString(we.getParagraphText());
            }
            case PDF -> {
                PDFParser parser = new PDFParser(new RandomAccessReadBuffer(inputStream));
                try (PDDocument pdDocument = parser.parse()) {
                    COSDocument cosDoc = pdDocument.getDocument();
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    PDDocument pdDoc = new PDDocument(cosDoc);
                    textData = pdfStripper.getText(pdDoc);
                }
            }
            default -> {
                ByteArrayOutputStream pdfFile = FileToPdf.convert(inputStream, fileName);
                PDFParser parser = new PDFParser(new RandomAccessReadBuffer(pdfFile.toByteArray()));
                try (PDDocument pdDocument = parser.parse()) {
                    COSDocument cosDoc = pdDocument.getDocument();
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    PDDocument pdDoc = new PDDocument(cosDoc);
                    textData = pdfStripper.getText(pdDoc);
                }
            }
        }
        return textData;
    }
}
