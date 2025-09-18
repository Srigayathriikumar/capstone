
package com.example.TeamResourceAccessManagement.config;

import com.example.TeamResourceAccessManagement.domain.*;
import com.example.TeamResourceAccessManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ResourceRepository resourceRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private AccessRequestRepository accessRequestRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private NotificationRepository notificationRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        // Create Users - matching security configuration usernames
        User admin = createUser("rajesh.admin", "rajesh.admin@company.com", "Rajesh Kumar", User.UserRole.ADMIN);
        
        // Managers
        User manager1 = createUser("priya.manager", "priya.manager@company.com", "Priya Sharma", User.UserRole.PROJECT_MANAGER);
        User manager2 = createUser("james.manager", "james.manager@company.com", "James Wilson", User.UserRole.PROJECT_MANAGER);
        User manager3 = createUser("sarah.manager", "sarah.manager@company.com", "Sarah Johnson", User.UserRole.PROJECT_MANAGER);
        User manager4 = createUser("mike.manager", "mike.manager@company.com", "Mike Davis", User.UserRole.PROJECT_MANAGER);
        User manager5 = createUser("anna.manager", "anna.manager@company.com", "Anna Brown", User.UserRole.PROJECT_MANAGER);
        
        // Team Leads
        User teamlead1 = createUser("adhnanjeff.teamlead", "adhnanjeff.teamlead@company.com", "Adhnan Jeff", User.UserRole.TEAMLEAD);
        User teamlead2 = createUser("swetha.teamlead", "swetha.teamlead@company.com", "Swetha", User.UserRole.TEAMLEAD);
        User teamlead3 = createUser("hari.teamlead", "hari.teamlead@company.com", "Hari", User.UserRole.TEAMLEAD);
        User teamlead4 = createUser("sounder.teamlead", "sounder.teamlead@company.com", "Sounder", User.UserRole.TEAMLEAD);
        User teamlead5 = createUser("tharanika.teamlead", "tharanika.teamlead@company.com", "Tharanika", User.UserRole.TEAMLEAD);
        User teamlead6 = createUser("pradeep.teamlead", "pradeep.teamlead@company.com", "Pradeep", User.UserRole.TEAMLEAD);
        User teamlead7 = createUser("adrin.teamlead", "adrin.teamlead@company.com", "Adrin", User.UserRole.TEAMLEAD);
        User teamlead8 = createUser("lokesh.teamlead", "lokesh.teamlead@company.com", "Lokesh", User.UserRole.TEAMLEAD);
        User teamlead9 = createUser("tom.teamlead", "tom.teamlead@company.com", "Tom Johnson", User.UserRole.TEAMLEAD);
        User teamlead10 = createUser("jane.teamlead", "jane.teamlead@company.com", "Jane Miller", User.UserRole.TEAMLEAD);
        
        // Developers
        User dev1 = createUser("arjun.dev", "arjun.dev@company.com", "Arjun Singh", User.UserRole.TEAM_MEMBER);
        User dev2 = createUser("emily.dev", "emily.dev@company.com", "Emily Chen", User.UserRole.TEAM_MEMBER);
        User dev3 = createUser("vikram.dev", "vikram.dev@company.com", "Vikram Gupta", User.UserRole.TEAM_MEMBER);
        User dev4 = createUser("michael.dev", "michael.dev@company.com", "Michael Brown", User.UserRole.TEAM_MEMBER);
        User dev5 = createUser("jennifer.dev", "jennifer.dev@company.com", "Jennifer Davis", User.UserRole.TEAM_MEMBER);
        User dev6 = createUser("robert.dev", "robert.dev@company.com", "Robert Miller", User.UserRole.TEAM_MEMBER);
        User dev7 = createUser("peter.dev", "peter.dev@company.com", "Peter Lee", User.UserRole.TEAM_MEMBER);
        User dev8 = createUser("amy.dev", "amy.dev@company.com", "Amy Taylor", User.UserRole.TEAM_MEMBER);
        User dev9 = createUser("chris.dev", "chris.dev@company.com", "Chris Anderson", User.UserRole.TEAM_MEMBER);
        User dev10 = createUser("lisa.dev", "lisa.dev@company.com", "Lisa Garcia", User.UserRole.TEAM_MEMBER);
        
        // Testers
        User tester1 = createUser("sophia.test", "sophia.test@company.com", "Sophia Martinez", User.UserRole.TEAM_MEMBER);
        User tester2 = createUser("ravi.test", "ravi.test@company.com", "Ravi Mehta", User.UserRole.TEAM_MEMBER);
        User tester3 = createUser("amanda.test", "amanda.test@company.com", "Amanda Wilson", User.UserRole.TEAM_MEMBER);
        User tester4 = createUser("kevin.test", "kevin.test@company.com", "Kevin Lee", User.UserRole.TEAM_MEMBER);
        User tester5 = createUser("nancy.test", "nancy.test@company.com", "Nancy White", User.UserRole.TEAM_MEMBER);
        User tester6 = createUser("mark.test", "mark.test@company.com", "Mark Thompson", User.UserRole.TEAM_MEMBER);
        User tester7 = createUser("helen.test", "helen.test@company.com", "Helen Clark", User.UserRole.TEAM_MEMBER);
        User tester8 = createUser("paul.test", "paul.test@company.com", "Paul Rodriguez", User.UserRole.TEAM_MEMBER);
        User tester9 = createUser("jane.tester", "jane.tester@company.com", "Jane Tester", User.UserRole.TEAM_MEMBER);

        // Create Projects
        Project meetingRoomBooking = createProject("Meeting Room Booking", "Smart meeting room reservation and management system", Project.ProjectStatus.ACTIVE);
        Project employee360 = createProject("Employee360", "Comprehensive employee management and engagement platform", Project.ProjectStatus.ACTIVE);
        Project leaveManagement = createProject("Leave Management System", "Employee leave tracking and approval workflow system", Project.ProjectStatus.ACTIVE);
        Project assetDesk = createProject("AssetDesk", "IT asset management and tracking system", Project.ProjectStatus.ACTIVE);
        Project visitorPortal = createProject("Visitor Portal", "Visitor registration and access management system", Project.ProjectStatus.ACTIVE);
        Project eventManagement = createProject("Event Management", "Corporate event planning and management platform", Project.ProjectStatus.ACTIVE);
        Project taskHub = createProject("Personalised TaskHub", "Personal task management and productivity system", Project.ProjectStatus.ACTIVE);
        Project budgetTracker = createProject("Budget Tracker", "Financial budget planning and expense tracking system", Project.ProjectStatus.ACTIVE);
        Project enterpriseResourcePlanning = createProject("Enterprise Resource Planning", "Comprehensive ERP system for business process management", Project.ProjectStatus.ACTIVE);

        // Initialize user sets for projects
        meetingRoomBooking.setUsers(new HashSet<>(Arrays.asList(manager1, teamlead1, dev1, dev2, tester1)));
        employee360.setUsers(new HashSet<>(Arrays.asList(manager2, teamlead2, dev3, dev4, tester2)));
        leaveManagement.setUsers(new HashSet<>(Arrays.asList(manager3, teamlead3, dev5, dev6, tester3)));
        assetDesk.setUsers(new HashSet<>(Arrays.asList(manager4, teamlead4, dev7, dev8, tester4)));
        visitorPortal.setUsers(new HashSet<>(Arrays.asList(manager5, teamlead5, dev9, dev10, tester5)));
        eventManagement.setUsers(new HashSet<>(Arrays.asList(manager1, teamlead6, dev1, dev3, tester6)));
        taskHub.setUsers(new HashSet<>(Arrays.asList(manager2, teamlead7, dev2, dev4, tester7)));
        budgetTracker.setUsers(new HashSet<>(Arrays.asList(manager3, teamlead8, dev5, dev7, tester8)));
        enterpriseResourcePlanning.setUsers(new HashSet<>(Arrays.asList(manager1, teamlead10, dev1)));

        projectRepository.saveAll(Arrays.asList(meetingRoomBooking, employee360, leaveManagement, assetDesk, visitorPortal, eventManagement, taskHub, budgetTracker, enterpriseResourcePlanning));

        // Create Resources - 4 Common and 4 Controlled per project
        
        // Meeting Room Booking Resources
        Resource meetingDB = createResource("Meeting Room Database", "Database for room bookings and schedules", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://meeting-db:5432/rooms", false, meetingRoomBooking, "priya.manager");
        Resource meetingAPI = createResource("Meeting Room API", "REST API for room booking operations", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/meetings", false, meetingRoomBooking, "priya.manager");
        Resource meetingDocs = createResource("Meeting Room Documentation", "System documentation and user guides", Resource.ResourceType.PDF, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/meetings.pdf", false, meetingRoomBooking, "arjun.dev");
        Resource meetingRepo = createResource("Meeting Room Repository", "Source code repository", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/meeting-rooms", false, meetingRoomBooking, "emily.dev");
        
        // Employee360 Resources
        Resource employeeDB = createResource("Employee Database", "Employee information and records database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://emp-db:5432/employees", false, employee360, "james.manager");
        Resource employeeAPI = createResource("Employee API", "Employee management REST API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/employees", false, employee360, "james.manager");
        Resource employeeDocs = createResource("Employee System Docs", "Employee system documentation", Resource.ResourceType.DOC, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/employee360.doc", false, employee360, "vikram.dev");
        Resource employeeRepo = createResource("Employee Repository", "Employee system source code", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/employee360", false, employee360, "michael.dev");
        
        // Leave Management Resources
        Resource leaveDB = createResource("Leave Management Database", "Leave requests and approval database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://leave-db:5432/leaves", false, leaveManagement, "sarah.manager");
        Resource leaveAPI = createResource("Leave Management API", "Leave processing REST API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/leaves", false, leaveManagement, "sarah.manager");
        Resource leaveDocs = createResource("Leave System Documentation", "Leave management user guide", Resource.ResourceType.PDF, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/leave-mgmt.pdf", false, leaveManagement, "jennifer.dev");
        Resource leaveRepo = createResource("Leave Management Repository", "Leave system source code", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/leave-management", false, leaveManagement, "robert.dev");
        
        // AssetDesk Resources
        Resource assetDB = createResource("Asset Management Database", "IT assets and inventory database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://asset-db:5432/assets", false, assetDesk, "mike.manager");
        Resource assetAPI = createResource("Asset Management API", "Asset tracking REST API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/assets", false, assetDesk, "mike.manager");
        Resource assetDocs = createResource("Asset Management Docs", "Asset system documentation", Resource.ResourceType.DOC, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/assetdesk.doc", false, assetDesk, "peter.dev");
        Resource assetRepo = createResource("AssetDesk Repository", "Asset management source code", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/assetdesk", false, assetDesk, "amy.dev");
        
        // Visitor Portal Resources
        Resource visitorDB = createResource("Visitor Management Database", "Visitor registration and tracking database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://visitor-db:5432/visitors", false, visitorPortal, "anna.manager");
        Resource visitorAPI = createResource("Visitor Portal API", "Visitor management REST API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/visitors", false, visitorPortal, "anna.manager");
        Resource visitorDocs = createResource("Visitor Portal Documentation", "Visitor system user guide", Resource.ResourceType.PDF, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/visitor-portal.pdf", false, visitorPortal, "chris.dev");
        Resource visitorRepo = createResource("Visitor Portal Repository", "Visitor portal source code", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/visitor-portal", false, visitorPortal, "lisa.dev");
        
        // Event Management Resources
        Resource eventDB = createResource("Event Management Database", "Event planning and tracking database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://event-db:5432/events", false, eventManagement, "priya.manager");
        Resource eventAPI = createResource("Event Management API", "Event management REST API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/events", false, eventManagement, "priya.manager");
        Resource eventDocs = createResource("Event Management Docs", "Event system documentation", Resource.ResourceType.DOC, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/events.doc", false, eventManagement, "arjun.dev");
        Resource eventRepo = createResource("Event Management Repository", "Event management source code", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/event-management", false, eventManagement, "vikram.dev");
        
        // TaskHub Resources
        Resource taskDB = createResource("TaskHub Database", "Personal task and productivity database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://task-db:5432/tasks", false, taskHub, "james.manager");
        Resource taskAPI = createResource("TaskHub API", "Task management REST API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/tasks", false, taskHub, "james.manager");
        Resource taskDocs = createResource("TaskHub Documentation", "TaskHub user documentation", Resource.ResourceType.PDF, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/taskhub.pdf", false, taskHub, "emily.dev");
        Resource taskRepo = createResource("TaskHub Repository", "TaskHub source code", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/taskhub", false, taskHub, "michael.dev");
        
        // Budget Tracker Resources
        Resource budgetDB = createResource("Budget Tracker Database", "Financial budget and expense database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://budget-db:5432/budget", false, budgetTracker, "sarah.manager");
        Resource budgetAPI = createResource("Budget Tracker API", "Budget management REST API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/budget", false, budgetTracker, "sarah.manager");
        Resource budgetDocs = createResource("Budget Tracker Docs", "Budget system documentation", Resource.ResourceType.DOC, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/budget-tracker.doc", false, budgetTracker, "jennifer.dev");
        Resource budgetRepo = createResource("Budget Tracker Repository", "Budget tracker source code", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/budget-tracker", false, budgetTracker, "peter.dev");
        
        // Enterprise Resource Planning Common Resources
        Resource erpDocs = createResource("ERP System Documentation", "Complete ERP system user guide and documentation", Resource.ResourceType.PDF, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/erp-guide.pdf", false, enterpriseResourcePlanning, "arjun.dev");
        Resource erpRepo = createResource("ERP Source Repository", "Main ERP system source code repository", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/erp-system", false, enterpriseResourcePlanning, "arjun.dev");
        Resource erpBlog = createResource("ERP Implementation Blog", "Best practices and implementation guides for ERP", Resource.ResourceType.URL, Resource.ResourceCategory.EXTERNAL_LINKS, Resource.ResourceAccessType.COMMON, "https://blog.company.com/erp-implementation", false, enterpriseResourcePlanning, "jane.teamlead");
        Resource erpTraining = createResource("ERP Training Videos", "Training materials and video tutorials for ERP system", Resource.ResourceType.URL, Resource.ResourceCategory.MEDIA, Resource.ResourceAccessType.COMMON, "https://training.company.com/erp-videos", false, enterpriseResourcePlanning, "jane.teamlead");
        Resource erpWiki = createResource("ERP Knowledge Base", "Internal wiki with ERP processes and procedures", Resource.ResourceType.URL, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://wiki.company.com/erp", false, enterpriseResourcePlanning, "arjun.dev");
        Resource erpSpecs = createResource("ERP Technical Specifications", "Technical architecture and system specifications", Resource.ResourceType.DOC, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/erp-specs.doc", false, enterpriseResourcePlanning, "jane.teamlead");
        Resource erpTestData = createResource("ERP Test Dataset", "Sample data for ERP system testing and development", Resource.ResourceType.JSON, Resource.ResourceCategory.OTHER, Resource.ResourceAccessType.COMMON, "https://data.company.com/erp-testdata.json", false, enterpriseResourcePlanning, "arjun.dev");
        Resource erpAPIGuide = createResource("ERP API Documentation", "Complete API reference and integration guide", Resource.ResourceType.URL, Resource.ResourceCategory.API, Resource.ResourceAccessType.COMMON, "https://api-docs.company.com/erp", false, enterpriseResourcePlanning, "jane.teamlead");
        Resource erpUserManual = createResource("ERP User Manual", "End-user manual for ERP system operations", Resource.ResourceType.PDF, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://manuals.company.com/erp-user-guide.pdf", false, enterpriseResourcePlanning, "arjun.dev");
        Resource erpConfigGuide = createResource("ERP Configuration Guide", "System configuration and setup instructions", Resource.ResourceType.DOC, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://config.company.com/erp-setup.doc", false, enterpriseResourcePlanning, "jane.teamlead");
        
        // Enterprise Resource Planning Controlled Resources
        Resource erpProdDB = createResource("ERP Production Database", "Main production database for ERP system", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://erp-prod:5432/erp_production", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpFinanceAPI = createResource("ERP Finance API", "Financial module API with sensitive data access", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/erp/finance", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpHRDB = createResource("ERP HR Database", "Human resources database with employee sensitive data", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://erp-hr:5432/hr_data", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpPayrollAPI = createResource("ERP Payroll API", "Payroll processing API with salary information", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/erp/payroll", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpInventoryDB = createResource("ERP Inventory Database", "Inventory management database with stock data", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://erp-inventory:5432/inventory", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpSupplierAPI = createResource("ERP Supplier API", "Supplier management API with vendor information", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/erp/suppliers", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpReportsDB = createResource("ERP Reports Database", "Business intelligence and reporting database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://erp-reports:5432/bi_reports", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpCustomerAPI = createResource("ERP Customer API", "Customer relationship management API", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/erp/customers", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpAuditDB = createResource("ERP Audit Database", "Audit trail and compliance database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://erp-audit:5432/audit_logs", false, enterpriseResourcePlanning, "priya.manager");
        Resource erpAdminPanel = createResource("ERP Admin Panel", "Administrative control panel for system management", Resource.ResourceType.URL, Resource.ResourceCategory.CLOUD_SERVICES, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://admin.company.com/erp", false, enterpriseResourcePlanning, "priya.manager");
        
        // Global Common Resources
        Resource testDB = createResource("Test Database", "Global testing environment database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.COMMON, "jdbc:postgresql://test-db:5432/test", false, null, "rajesh.admin");
        Resource jenkins = createResource("Jenkins CI/CD", "Continuous integration and deployment server", Resource.ResourceType.URL, Resource.ResourceCategory.CLOUD_SERVICES, Resource.ResourceAccessType.COMMON, "https://jenkins.company.com", true, null, "rajesh.admin");
        Resource sonarqube = createResource("SonarQube", "Code quality and security analysis", Resource.ResourceType.URL, Resource.ResourceCategory.CLOUD_SERVICES, Resource.ResourceAccessType.COMMON, "https://sonar.company.com", true, null, "rajesh.admin");
        Resource apiGateway = createResource("API Gateway", "Main API gateway for all systems", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/gateway", true, null, "rajesh.admin");

        // Auto-grant permissions for COMMON resources to team members only
        List<User> teamMembers = Arrays.asList(dev1, dev2, dev3, dev4, dev5, dev6, dev7, dev8, dev9, dev10, tester1, tester2, tester3, tester4, tester5, tester6, tester7, tester8);
        List<Resource> commonResources = Arrays.asList(testDB, jenkins, sonarqube, meetingDocs, meetingRepo, employeeDocs, employeeRepo, leaveDocs, leaveRepo, assetDocs, assetRepo, visitorDocs, visitorRepo, eventDocs, eventRepo, taskDocs, taskRepo, budgetDocs, budgetRepo, erpDocs, erpRepo, erpBlog, erpTraining, erpWiki, erpSpecs, erpTestData, erpAPIGuide, erpUserManual, erpConfigGuide);
        
        // Grant READ access to COMMON resources for team members only
        for (User teamMember : teamMembers) {
            for (Resource resource : commonResources) {
                if (resource.getAccessType() == Resource.ResourceAccessType.COMMON) {
                    createPermission(teamMember, resource, Permission.AccessLevel.READ, null);
                }
            }
        }
        
        // Specific permissions for MANAGER_CONTROLLED resources
        // Manager permissions
        createPermission(manager1, meetingDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, meetingAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, eventDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, eventAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager2, employeeDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager2, employeeAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager2, taskDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager2, taskAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager3, leaveDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager3, leaveAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager3, budgetDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager3, budgetAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager4, assetDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager4, assetAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager5, visitorDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager5, visitorAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpProdDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpFinanceAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpHRDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpPayrollAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpInventoryDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpSupplierAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpReportsDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpCustomerAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpAuditDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, erpAdminPanel, Permission.AccessLevel.ADMIN, null);
        
        // Team Lead permissions - each team lead has ADMIN access to their project resources
        createPermission(teamlead1, meetingDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead1, meetingAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead2, employeeDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead2, employeeAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead3, leaveDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead3, leaveAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead4, assetDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead4, assetAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead5, visitorDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead5, visitorAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead6, eventDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead6, eventAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead7, taskDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead7, taskAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead8, budgetDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead8, budgetAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpProdDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpFinanceAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpHRDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpPayrollAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpInventoryDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpSupplierAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpReportsDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpCustomerAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpAuditDB, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead10, erpAdminPanel, Permission.AccessLevel.ADMIN, null);
        
        // Admin permissions - full access to all resources
        createPermission(admin, apiGateway, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, meetingDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, employeeDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, leaveDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, assetDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, visitorDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, eventDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, taskDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, budgetDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpProdDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpFinanceAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpHRDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpPayrollAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpInventoryDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpSupplierAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpReportsDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpCustomerAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpAuditDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, erpAdminPanel, Permission.AccessLevel.ADMIN, null);

        // Create Access Requests - project-scoped with existing resources
        // E-commerce project PENDING requests (prodDB, paymentGateway belong to ecommerce)
        createAccessRequest(dev1, meetingDB, Permission.AccessLevel.READ, "Need access to meeting database for booking feature development", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(dev2, meetingAPI, Permission.AccessLevel.READ, "Need API access for room availability integration", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(tester1, meetingDB, Permission.AccessLevel.READ, "Need database access for testing booking workflows", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(dev4, employeeAPI, Permission.AccessLevel.READ, "Need API access for employee data integration", AccessRequest.RequestStatus.PENDING, null, null, null);
        
        // E-commerce project completed requests
        createAccessRequest(tester1, leaveDB, Permission.AccessLevel.READ, "Need leave database access for integration testing", AccessRequest.RequestStatus.APPROVED, manager3, "Approved for integration testing", LocalDateTime.now().minusDays(2));
        createAccessRequest(dev2, leaveDB, Permission.AccessLevel.WRITE, "Need write access for database schema updates", AccessRequest.RequestStatus.REJECTED, manager3, "Use development environment for schema changes", LocalDateTime.now().minusDays(3));
        createAccessRequest(tester2, assetAPI, Permission.AccessLevel.READ, "Need asset API access for load testing", AccessRequest.RequestStatus.APPROVED, manager4, "Approved for testing", LocalDateTime.now().minusDays(1));
        
        // Mobile banking project PENDING requests (mobileAPI, externalService belong to mobile)
        createAccessRequest(dev3, employeeDB, Permission.AccessLevel.READ, "Need employee database access for profile management", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(dev4, visitorAPI, Permission.AccessLevel.READ, "Need visitor API access for registration integration", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(tester3, taskAPI, Permission.AccessLevel.READ, "Need task API access for automated testing", AccessRequest.RequestStatus.PENDING, null, null, null);
        
        // Mobile banking project completed requests
        createAccessRequest(tester3, budgetAPI, Permission.AccessLevel.READ, "Need access to budget API for testing", AccessRequest.RequestStatus.REJECTED, manager3, "Use test environment instead", LocalDateTime.now().minusDays(5));
        createAccessRequest(dev3, eventAPI, Permission.AccessLevel.WRITE, "Need write access for API endpoint development", AccessRequest.RequestStatus.APPROVED, manager1, "Approved for development work", LocalDateTime.now().minusDays(1));
        
        // Analytics project requests
        createAccessRequest(dev5, budgetDB, Permission.AccessLevel.READ, "Need budget database access for reporting features", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(tester4, assetDB, Permission.AccessLevel.READ, "Need database access for data validation testing", AccessRequest.RequestStatus.APPROVED, manager4, "Approved for testing", LocalDateTime.now().minusDays(3));
        
        // Additional user requests for testing user portal
        createAccessRequest(dev1, eventDB, Permission.AccessLevel.READ, "Need event database access for development", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(dev5, visitorDB, Permission.AccessLevel.READ, "Need database access for performance optimization", AccessRequest.RequestStatus.REJECTED, manager5, "Use staging environment", LocalDateTime.now().minusDays(2));
        createAccessRequest(dev6, apiGateway, Permission.AccessLevel.READ, "Need API gateway access for security audit", AccessRequest.RequestStatus.APPROVED, admin, "Approved for security review", LocalDateTime.now().minusDays(4));
        
        // ERP Access Requests
        createAccessRequest(dev1, erpProdDB, Permission.AccessLevel.READ, "Need production database access for ERP module development", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(dev1, erpFinanceAPI, Permission.AccessLevel.READ, "Need finance API access for integration testing", AccessRequest.RequestStatus.APPROVED, manager1, "Approved for development work", LocalDateTime.now().minusDays(1));
        createAccessRequest(dev2, erpHRDB, Permission.AccessLevel.READ, "Need HR database access for employee module development", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(tester1, erpInventoryDB, Permission.AccessLevel.READ, "Need inventory database access for testing stock management features", AccessRequest.RequestStatus.REJECTED, manager1, "Use test environment for inventory testing", LocalDateTime.now().minusDays(2));
        createAccessRequest(dev3, erpReportsDB, Permission.AccessLevel.READ, "Need reports database access for business intelligence features", AccessRequest.RequestStatus.PENDING, null, null, null);
        


        // Create Audit Logs
        createAuditLog(admin, null, AuditLog.ActionType.USER_LOGIN, "Admin user logged into system");
        createAuditLog(dev1, meetingDB, AuditLog.ActionType.ACCESS_GRANTED, "Read access granted to meeting room database");
        createAuditLog(manager1, testDB, AuditLog.ActionType.PERMISSION_CHANGED, "Updated database permissions for development team");
        createAuditLog(dev2, employeeAPI, AuditLog.ActionType.RESOURCE_CREATED, "Created new employee API endpoint for profile management");
        createAuditLog(tester1, null, AuditLog.ActionType.ACCESS_REQUESTED, "Requested access to meeting room booking system");
        createAuditLog(manager2, null, AuditLog.ActionType.ACCESS_REVOKED, "Revoked expired permissions for former team member");
        createAuditLog(dev3, leaveDB, AuditLog.ActionType.RESOURCE_UPDATED, "Updated leave management database connection parameters");
        createAuditLog(admin, jenkins, AuditLog.ActionType.RESOURCE_CREATED, "Configured new CI/CD pipeline for projects");
        createAuditLog(teamlead1, meetingDB, AuditLog.ActionType.PERMISSION_CHANGED, "Team lead updated meeting database access permissions");
        createAuditLog(teamlead2, employeeAPI, AuditLog.ActionType.ACCESS_GRANTED, "Team lead granted employee API access to team member");
        createAuditLog(teamlead3, leaveDB, AuditLog.ActionType.RESOURCE_UPDATED, "Team lead updated leave management database configuration");
        createAuditLog(dev4, null, AuditLog.ActionType.USER_LOGIN, "New team member logged into system");
        createAuditLog(dev5, apiGateway, AuditLog.ActionType.ACCESS_REQUESTED, "Requested API gateway access for system integration");
        createAuditLog(teamlead5, visitorDB, AuditLog.ActionType.ACCESS_GRANTED, "Visitor portal team lead granted resource access");
        createAuditLog(dev7, assetDB, AuditLog.ActionType.RESOURCE_CREATED, "Created new asset tracking database entry");
        createAuditLog(tester5, visitorAPI, AuditLog.ActionType.ACCESS_REQUESTED, "Requested visitor portal API access for testing");
        createAuditLog(manager4, assetAPI, AuditLog.ActionType.PERMISSION_CHANGED, "Updated asset management API permissions");
        createAuditLog(dev9, eventDB, AuditLog.ActionType.RESOURCE_UPDATED, "Updated event management database schema");
    }

    private User createUser(String username, String email, String fullName, User.UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        return userRepository.save(user);
    }

    private Project createProject(String name, String description, Project.ProjectStatus status) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);
        return projectRepository.save(project);
    }

    private Resource createResource(String name, String description, Resource.ResourceType type, Resource.ResourceCategory category, Resource.ResourceAccessType accessType, String url, boolean isGlobal, Project project, String createdBy) {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setDescription(description);
        resource.setType(type);
        resource.setCategory(category);
        resource.setAccessType(accessType);
        resource.setResourceUrl(url);
        resource.setIsGlobal(isGlobal);
        resource.setProject(project);
        resource.setCreatedBy(createdBy);
        resource.setUploadedBy(createdBy);
        return resourceRepository.save(resource);
    }

    private void createPermission(User user, Resource resource, Permission.AccessLevel level, LocalDateTime expiresAt) {
        Permission permission = new Permission();
        permission.setUser(user);
        permission.setResource(resource);
        permission.setAccessLevel(level);
        permission.setExpiresAt(expiresAt);
        permission.setIsActive(true);
        permission.setGrantedAt(LocalDateTime.now().minusDays((long)(Math.random() * 30)));
        permissionRepository.save(permission);
    }

    private void createAccessRequest(User user, Resource resource, Permission.AccessLevel level, String justification, 
                                   AccessRequest.RequestStatus status, User approver, String comments, LocalDateTime approvedAt) {
        AccessRequest request = new AccessRequest();
        request.setUser(user);
        request.setResource(resource);
        request.setProject(resource.getProject());
        
        // Set project manager - find manager from project users
        if (resource.getProject() != null) {
            User manager = resource.getProject().getUsers().stream()
                .filter(u -> u.getRole() == User.UserRole.PROJECT_MANAGER)
                .findFirst()
                .orElse(null);
            request.setProjectManager(manager);
        }
        
        request.setRequestedAccessLevel(level);
        request.setJustification(justification);
        request.setStatus(status);
        request.setApprovedBy(approver);
        request.setApproverComments(comments);
        request.setRequestedAt(LocalDateTime.now().minusDays((long)(Math.random() * 10)));
        request.setApprovedAt(approvedAt);
        accessRequestRepository.save(request);
    }

    private void createAuditLog(User user, Resource resource, AuditLog.ActionType action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setResource(resource);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now().minusDays((long)(Math.random() * 7)));
        auditLogRepository.save(auditLog);
    }
}