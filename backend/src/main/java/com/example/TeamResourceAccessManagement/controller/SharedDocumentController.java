package com.example.TeamResourceAccessManagement.controller;

import com.example.TeamResourceAccessManagement.dto.SharedDocumentDTO;
import com.example.TeamResourceAccessManagement.service.SharedDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/shared-documents")
@CrossOrigin(origins = "http://localhost:4200")
public class SharedDocumentController {

    @Autowired
    private SharedDocumentService sharedDocumentService;

    @GetMapping
    public ResponseEntity<List<SharedDocumentDTO>> getAllSharedDocuments() {
        List<SharedDocumentDTO> documents = sharedDocumentService.getAllSharedDocuments();
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/share-file")
    public ResponseEntity<SharedDocumentDTO> shareDocumentWithFile(
            @RequestParam("title") String title,
            @RequestParam("message") String message,
            @RequestParam("documentType") String documentType,
            @RequestParam("authorId") Long authorId,
            @RequestParam("authorName") String authorName,
            @RequestParam("authorRole") String authorRole,
            @RequestParam("file") MultipartFile file) {

        SharedDocumentDTO documentDTO = new SharedDocumentDTO();
        documentDTO.setTitle(title);
        documentDTO.setMessage(message);
        documentDTO.setDocumentType(documentType);
        documentDTO.setAuthorId(authorId);
        documentDTO.setAuthorName(authorName);
        documentDTO.setAuthorRole(authorRole);

        SharedDocumentDTO savedDocument = sharedDocumentService.shareDocument(documentDTO, file);
        return ResponseEntity.ok(savedDocument);
    }

    @PostMapping("/share-url")
    public ResponseEntity<SharedDocumentDTO> shareDocumentWithUrl(@RequestBody SharedDocumentDTO documentDTO) {
        SharedDocumentDTO savedDocument = sharedDocumentService.shareDocumentWithUrl(documentDTO);
        return ResponseEntity.ok(savedDocument);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteSharedDocument(
            @PathVariable Long documentId,
            @RequestParam Long userId) {
        sharedDocumentService.deleteSharedDocument(documentId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        byte[] fileData = sharedDocumentService.downloadDocument(documentId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }
}