package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.dto.SharedDocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SharedDocumentService {
    List<SharedDocumentDTO> getAllSharedDocuments();
    SharedDocumentDTO shareDocument(SharedDocumentDTO documentDTO, MultipartFile file);
    SharedDocumentDTO shareDocumentWithUrl(SharedDocumentDTO documentDTO);
    void deleteSharedDocument(Long documentId, Long userId);
    byte[] downloadDocument(Long documentId);
}