package com.example.TeamResourceAccessManagement.repository;

import com.example.TeamResourceAccessManagement.domain.SharedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedDocumentRepository extends JpaRepository<SharedDocument, Long> {
    
    @Query("SELECT sd FROM SharedDocument sd ORDER BY sd.sharedAt DESC")
    List<SharedDocument> findAllOrderBySharedAtDesc();
    
    List<SharedDocument> findByAuthorIdOrderBySharedAtDesc(Long authorId);
    
    List<SharedDocument> findByDocumentTypeOrderBySharedAtDesc(String documentType);
}