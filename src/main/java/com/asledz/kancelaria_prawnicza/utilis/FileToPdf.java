package com.asledz.kancelaria_prawnicza.utilis;

import com.asledz.kancelaria_prawnicza.exception.BadRequestException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FileToPdf {
    private FileToPdf() {
    }

    /**
     * Method to use Gotenberg local service https://gotenberg.dev/docs/getting-started/introduction
     * to convert file from format that libreoffice can convert from to pdf. Its purpose is to convert
     * file to pdf so program can read letters from it and use it in Lucene Indexing and Searching.
     *
     * @param file     InputStream of file that you want to convert
     * @param fileName file's name without extension
     * @return ByteArrayOutputStream of the same file and content but in pdf format.
     */
    public static ByteArrayOutputStream convert(InputStream file, String fileName) {
        try {
            String serverUrl = "http://localhost:3000/forms/libreoffice/convert";
            HttpPost post = new HttpPost(serverUrl);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", file.readAllBytes(), ContentType.APPLICATION_OCTET_STREAM, fileName);
            HttpEntity entity = builder.build();

            post.setEntity(entity);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpResponse response = client.execute(post);
                response.getEntity().writeTo(out);
            }
            return out;

        } catch (Exception e) {
            throw new BadRequestException("Couldn't convert given file to pdf with error message: " + e.getMessage());
        }
    }
}
