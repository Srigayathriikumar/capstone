package com.example.TeamResourceAccessManagement.repository;

import com.example.TeamResourceAccessManagement.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.UserRole role);
    
    @Query("SELECT u FROM User u JOIN u.projects p WHERE p.id = :projectId")
    List<User> findByProjectId(Long projectId);
    
    @Query(value = "SELECT * FROM tramusers WHERE " +
           "LOWER(username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(COALESCE(full_name, '')) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(role) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY created_at DESC", nativeQuery = true)
    List<User> searchUsers(@Param("query") String query);
    
    @Query(value = "SELECT * FROM tramusers ORDER BY created_at DESC", nativeQuery = true)
    List<User> findAllOrderByCreatedAtDesc();
}