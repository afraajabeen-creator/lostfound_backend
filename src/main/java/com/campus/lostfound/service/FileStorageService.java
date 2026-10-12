package com.campus.lostfound.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for storing uploaded media files (item images and claim proofs).
 */
public interface FileStorageService {

    String storeFile(MultipartFile file);

    void deleteFile(String fileUrl);
}
