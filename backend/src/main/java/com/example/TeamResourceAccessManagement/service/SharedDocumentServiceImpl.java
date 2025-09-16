package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.SharedDocument;
import com.example.TeamResourceAccessManagement.dto.SharedDocumentDTO;
import com.example.TeamResourceAccessManagement.exceptions.ResourceNotFoundException;
import com.example.TeamResourceAccessManagement.repository.SharedDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharedDocumentServiceImpl implements SharedDocumentService {

    @Autowired
    private SharedDocumentRepository sharedDocumentRepository;

    @Override
    public List<SharedDocumentDTO> getAllSharedDocuments() {
        return sharedDocumentRepository.findAllOrderBySharedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SharedDocumentDTO shareDocument(SharedDocumentDTO documentDTO, MultipartFile file) {
        SharedDocument document = new SharedDocument();
        document.setTitle(documentDTO.getTitle());
        document.setMessage(documentDTO.getMessage());
        document.setDocumentType(documentDTO.getDocumentType());
        document.setAuthorId(documentDTO.getAuthorId());
        document.setAuthorName(documentDTO.getAuthorName());
        document.setAuthorRole(documentDTO.getAuthorRole());

        if (file != null && !file.isEmpty()) {
            try {
                document.setFileData(file.getBytes());
                document.setFileName(file.getOriginalFilename());
                document.setFileSize(file.getSize());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file", e);
            }
        }

        SharedDocument savedDocument = sharedDocumentRepository.save(document);
        return convertToDTO(savedDocument);
    }

    @Override
    public SharedDocumentDTO shareDocumentWithUrl(SharedDocumentDTO documentDTO) {
        SharedDocument document = new SharedDocument();
        document.setTitle(documentDTO.getTitle());
        document.setMessage(documentDTO.getMessage());
        document.setDocumentType(documentDTO.getDocumentType());
        document.setAuthorId(documentDTO.getAuthorId());
        document.setAuthorName(documentDTO.getAuthorName());
        document.setAuthorRole(documentDTO.getAuthorRole());
        document.setDocumentUrl(documentDTO.getDocumentUrl());

        SharedDocument savedDocument = sharedDocumentRepository.save(document);
        return convertToDTO(savedDocument);
    }

    @Override
    public void deleteSharedDocument(Long documentId, Long userId) {
        SharedDocument document = sharedDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only the author can delete this document");
        }

        sharedDocumentRepository.delete(document);
    }

    @Override
    public byte[] downloadDocument(Long documentId) {
        SharedDocument document = sharedDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        return document.getFileData();
    }

    private SharedDocumentDTO convertToDTO(SharedDocument document) {
        SharedDocumentDTO dto = new SharedDocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setMessage(document.getMessage());
        dto.setDocumentType(document.getDocumentType());
        dto.setAuthorId(document.getAuthorId());
        dto.setAuthorName(document.getAuthorName());
        dto.setAuthorRole(document.getAuthorRole());
        dto.setDocumentUrl(document.getDocumentUrl());
        dto.setFileName(document.getFileName());
        dto.setFileSize(document.getFileSize());
        dto.setSharedAt(document.getSharedAt());
        return dto;
    }
}