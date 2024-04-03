package com.yz.pferestapi.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
    private final String uploadDir = "uploads";

    public String uploadFile(MultipartFile file, String subFolderName) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName; // Append a unique identifier to the file name
        Path uploadPath = Paths.get(uploadDir + File.separator + subFolderName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFileName);
        try {
            Files.copy(file.getInputStream(), filePath);

            // Add leading "/"
            String path = File.separator + filePath.toString();

            return path.replace(File.separator, "/"); // Return the path of the saved file
        } catch (IOException e) {
            throw new IOException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

    public void deleteFile(String filename) throws IOException {
        // Remove leading "/" if present
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        Path filePath = Paths.get("").resolve(filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new IOException("File not found: " + filename);
        }
    }
}
