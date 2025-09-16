-- Summary of User and Project Updates
-- This file documents the changes made to ensure each project has exactly one team lead

-- NEW USERS ADDED:
-- ================

-- Additional Manager:
-- sarah.manager (Sarah Johnson) - PROJECT_MANAGER

-- Additional Team Leads:
-- david.teamlead (David Kim) - TEAMLEAD for Analytics project
-- maria.teamlead (Maria Garcia) - TEAMLEAD for Security project  
-- alex.teamlead (Alex Thompson) - TEAMLEAD for Cloud Migration project
-- lisa.teamlead (Lisa Wang) - TEAMLEAD for AI Platform project

-- Additional Team Members:
-- michael.dev (Michael Brown) - TEAM_MEMBER
-- jennifer.dev (Jennifer Davis) - TEAM_MEMBER
-- robert.dev (Robert Miller) - TEAM_MEMBER
-- amanda.test (Amanda Wilson) - TEAM_MEMBER
-- kevin.test (Kevin Lee) - TEAM_MEMBER

-- NEW PROJECTS ADDED:
-- ===================
-- Cloud Migration - Migration of legacy systems to cloud infrastructure (ACTIVE)
-- AI Platform - Machine learning and AI development platform (ACTIVE)

-- PROJECT TEAM LEAD ASSIGNMENTS:
-- ===============================
-- E-Commerce Platform: anita.teamlead (Anita Patel) - ONLY team lead
-- Mobile Banking App: carlos.teamlead (Carlos Rodriguez) - ONLY team lead
-- Data Analytics Dashboard: david.teamlead (David Kim) - ONLY team lead
-- Security Audit System: maria.teamlead (Maria Garcia) - ONLY team lead
-- Cloud Migration: alex.teamlead (Alex Thompson) - ONLY team lead
-- AI Platform: lisa.teamlead (Lisa Wang) - ONLY team lead

-- SECURITY CONFIGURATION UPDATED:
-- ================================
-- All new users added to SecurityConfig.java with appropriate roles
-- Team leads have TEAMLEAD role
-- Team members have USER role
-- Manager has MANAGER role

-- PERMISSIONS GRANTED:
-- ====================
-- Each team lead has WRITE access to their project's MANAGER_CONTROLLED resources
-- Team members have READ access to COMMON resources
-- Managers have ADMIN access to their project resources
-- Admin has ADMIN access to all resources

-- VERIFICATION QUERIES:
-- =====================
-- To verify each project has exactly one team lead:
SELECT p.name as project_name, 
       COUNT(u.id) as teamlead_count,
       GROUP_CONCAT(u.full_name) as teamleads
FROM tramprojects p
JOIN project_users pu ON p.id = pu.project_id
JOIN tramusers u ON pu.user_id = u.id
WHERE u.role = 'TEAMLEAD'
GROUP BY p.id, p.name
ORDER BY p.name;

-- To verify all users and their roles:
SELECT username, full_name, role, email
FROM tramusers
ORDER BY role, username;

-- To verify project memberships:
SELECT p.name as project_name, 
       u.username, 
       u.full_name, 
       u.role
FROM tramprojects p
JOIN project_users pu ON p.id = pu.project_id
JOIN tramusers u ON pu.user_id = u.id
ORDER BY p.name, u.role, u.username;