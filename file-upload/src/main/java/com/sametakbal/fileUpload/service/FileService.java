package com.sametakbal.fileUpload.service;


import com.sametakbal.fileUpload.util.MediaTypeUtils;
import jakarta.servlet.ServletContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service

public class FileService {

    public static final String UPLOAD_DIR = "./uploads/";
    private final Path fileStorageLocation;
    private final ServletContext servletContext;

    public FileService(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.fileStorageLocation = Paths.get(UPLOAD_DIR)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public String uploadFile(MultipartFile file) {

        if (file.isEmpty()) {
            return "file not found!";
        }

        try {
            Path path = Paths.get(UPLOAD_DIR + String.format("%s.jpg",UUID.randomUUID()));
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return "redirect:/";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public InputStreamResource getFile(String fileName) {

        try {
            MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
            System.out.println("fileName: " + fileName);
            System.out.println("mediaType: " + mediaType);

            File file = new File(UPLOAD_DIR + fileName);

            return new InputStreamResource(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new Exception("File not found " + fileName);
            }
        } catch ( Exception ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }
}
