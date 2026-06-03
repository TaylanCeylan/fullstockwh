package com.fullstockwh.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService
{
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file)
    {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path targetPath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/products/" + uniqueFilename;
            log.info("File stored successfully: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new RuntimeException("Could not store the file. Please try again.", e);
        }
    }

    @Override
    public void delete(String fileUrl)
    {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            String filename = Paths.get(fileUrl).getFileName().toString();
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", fileUrl);
            }
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", fileUrl, e.getMessage());
        }
    }
}
