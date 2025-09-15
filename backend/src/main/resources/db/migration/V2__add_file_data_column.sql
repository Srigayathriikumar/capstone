-- Add file_data column to resources table for storing file content in database
ALTER TABLE resources ADD COLUMN file_data BYTEA;

-- Add index for better performance when querying file data
CREATE INDEX idx_resources_file_data ON resources(file_data) WHERE file_data IS NOT NULL;

-- Add comment to explain the column
COMMENT ON COLUMN resources.file_data IS 'Binary data of uploaded files stored directly in database';

