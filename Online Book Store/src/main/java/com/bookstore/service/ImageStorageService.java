package com.bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Path UPLOAD_DIR = Paths.get("uploads", "books");

    public String storeBookImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only image files are allowed (jpg, jpeg, png, webp, gif).");
        }

        try {
            Files.createDirectories(UPLOAD_DIR);
            String filename = UUID.randomUUID() + "." + extension;
            Path destination = UPLOAD_DIR.resolve(filename).normalize();
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/books/" + filename;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save uploaded image.", e);
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("Image file must have an extension.");
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
