-- Update existing manager-controlled resources to allow access for dev, test, and QA users
UPDATE resources 
SET allowed_user_groups = 'dev,test,QA' 
WHERE access_type = 'MANAGER_CONTROLLED' 
AND (allowed_user_groups IS NULL OR allowed_user_groups = '');