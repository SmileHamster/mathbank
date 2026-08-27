package com.mathbank.problem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String store(MultipartFile image) {
        try {
            Path dir = Path.of(uploadDir);
            Files.createDirectories(dir);

            String original = image.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.'))
                    : "";
            String filename = UUID.randomUUID() + ext;

            Path target = dir.resolve(filename);
            image.transferTo(target);

            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        }
    }

    public void delete(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return;
        String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
        File file = Path.of(uploadDir, filename).toFile();
        if (file.exists()) {
            file.delete();
        }
    }
}
