package com.foober.foober.controller;

import com.foober.foober.dto.ApiResponse;
import com.foober.foober.model.Image;
import com.foober.foober.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse upload(@RequestPart("email") String userEmail, @RequestPart("image") MultipartFile image) {
        try {
            imageService.save(userEmail, image);
            return new ApiResponse(String.format("File uploaded successfully: %s", image.getOriginalFilename()));
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Could not upload the file: %s!", image.getOriginalFilename()));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        Image fileEntity = imageService.get(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFilename() + "\"")
                .contentType(MediaType.valueOf(fileEntity.getContentType()))
                .body(fileEntity.getData());

    }
}
