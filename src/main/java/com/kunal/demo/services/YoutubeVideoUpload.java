package com.kunal.demo.services;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class YoutubeVideoUpload {

    private static final String UPLOAD_URL =
            "https://www.googleapis.com/upload/youtube/v3/videos?uploadType=resumable&part=snippet,status";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public String uploadVideo(String title, String description, String visibility,
                              MultipartFile videoFile, String accessToken) throws IOException {
        try {
            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();

            // Metadata JSON
            String metaData = "{\n" +
                    "  \"snippet\": {\n" +
                    "    \"title\": \"" + title + "\",\n" +
                    "    \"description\": \"" + description + "\",\n" +
                    "    \"tags\": [\"project\", \"springboot\", \"upload\"],\n" +
                    "    \"categoryId\": \"22\"\n" +
                    "  },\n" +
                    "  \"status\": {\n" +
                    "    \"privacyStatus\": \"" + visibility.toLowerCase() + "\",\n" +
                    "    \"embeddable\": true,\n" +
                    "    \"license\": \"youtube\"\n" +
                    "  }\n" +
                    "}";

            // Step 1: Create Upload Session
            HttpRequest initRequest = requestFactory.buildPostRequest(
                    new GenericUrl(UPLOAD_URL),
                    ByteArrayContent.fromString("application/json", metaData)
            );

            HttpHeaders initHeaders = new HttpHeaders();
            initHeaders.setAuthorization("Bearer " + accessToken);
            initHeaders.setContentType("application/json");
            initRequest.setHeaders(initHeaders);

            HttpResponse initResponse = initRequest.execute();
            String uploadUrl = initResponse.getHeaders().getLocation();

            if (uploadUrl == null) {
                return "Failed to create upload session.";
            }

            // Step 2: Upload Video File
            InputStreamContent mediaContent =
                    new InputStreamContent("video/*", videoFile.getInputStream());
            mediaContent.setLength(videoFile.getSize());

            HttpRequest uploadRequest = requestFactory.buildPutRequest(
                    new GenericUrl(uploadUrl),
                    mediaContent
            );

            HttpHeaders uploadHeaders = new HttpHeaders();
            uploadHeaders.setContentType("video/*");
            uploadHeaders.setContentLength(videoFile.getSize());
            uploadRequest.setHeaders(uploadHeaders);

            HttpResponse uploadResponse = uploadRequest.execute();

            return "Upload successful. Status Code: " + uploadResponse.getStatusCode();

        } catch (HttpResponseException e) {
            return "Upload failed. HTTP Status: " + e.getStatusCode() + " - " + e.getContent();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unexpected error occurred: " + e.getMessage();
        }
    }
}
