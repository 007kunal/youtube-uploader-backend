package com.kunal.demo.controller;

import com.kunal.demo.services.YoutubeVideoUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/youtube")

public class YoutubeVideoServiceController {

    @Autowired
    private YoutubeVideoUpload youtubeVideoUpload;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("visibility") String visibility,
            @RequestParam("file") MultipartFile file,
            @RequestParam("accessToken") String accessToken) {

        try {
            String response = youtubeVideoUpload.uploadVideo(title, description, visibility, file, accessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
}
