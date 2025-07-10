package com.work.work.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class MultipartFileWrapper implements MultipartFile {
    private final MultipartFile originalFile;
    private final String contentType;

    public MultipartFileWrapper(MultipartFile originalFile, String contentType) {
        this.originalFile = originalFile;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return originalFile.getName();
    }

    @Override
    public String getOriginalFilename() {
        return originalFile.getOriginalFilename();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return originalFile.isEmpty();
    }

    @Override
    public long getSize() {
        return originalFile.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return originalFile.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return originalFile.getInputStream();
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        originalFile.transferTo(dest);
    }
}