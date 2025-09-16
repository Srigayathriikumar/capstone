CREATE TABLE shared_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    document_type VARCHAR(50) NOT NULL,
    author_id BIGINT NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    author_role VARCHAR(50) NOT NULL,
    document_url VARCHAR(500),
    file_data LONGBLOB,
    file_name VARCHAR(255),
    file_size BIGINT,
    shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_author_id (author_id),
    INDEX idx_shared_at (shared_at),
    INDEX idx_document_type (document_type)
);