
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
        User manager1 = createUser("priya.manager", "priya.manager@company.com", "Priya Sharma", User.UserRole.PROJECT_MANAGER);
        User manager2 = createUser("james.manager", "james.manager@company.com", "James Wilson", User.UserRole.PROJECT_MANAGER);
        User teamlead1 = createUser("anita.teamlead", "anita.teamlead@company.com", "Anita Patel", User.UserRole.TEAMLEAD);
        User teamlead2 = createUser("carlos.teamlead", "carlos.teamlead@company.com", "Carlos Rodriguez", User.UserRole.TEAMLEAD);
        User dev1 = createUser("arjun.dev", "arjun.dev@company.com", "Arjun Singh", User.UserRole.TEAM_MEMBER);
        User dev2 = createUser("emily.dev", "emily.dev@company.com", "Emily Chen", User.UserRole.TEAM_MEMBER);
        User dev3 = createUser("vikram.dev", "vikram.dev@company.com", "Vikram Gupta", User.UserRole.TEAM_MEMBER);
        User tester1 = createUser("sophia.test", "sophia.test@company.com", "Sophia Martinez", User.UserRole.TEAM_MEMBER);
        User tester2 = createUser("ravi.test", "ravi.test@company.com", "Ravi Mehta", User.UserRole.TEAM_MEMBER);

        // Create Projects
        Project ecommerce = createProject("E-Commerce Platform", "Online shopping platform development", Project.ProjectStatus.ACTIVE);
        Project mobile = createProject("Mobile Banking App", "Mobile banking application for customers", Project.ProjectStatus.ACTIVE);
        Project analytics = createProject("Data Analytics Dashboard", "Business intelligence and reporting system", Project.ProjectStatus.INACTIVE);
        Project security = createProject("Security Audit System", "Internal security monitoring and compliance", Project.ProjectStatus.COMPLETED);

        // Initialize user sets for projects with managers and teamleads
        ecommerce.setUsers(new HashSet<>(Arrays.asList(manager1, teamlead1, dev1, dev2, tester1)));
        mobile.setUsers(new HashSet<>(Arrays.asList(manager2, teamlead2, dev2, dev3, tester2)));
        analytics.setUsers(new HashSet<>(Arrays.asList(manager1, teamlead1, dev1, dev3)));
        security.setUsers(new HashSet<>(Arrays.asList(admin, manager2, teamlead2, tester1)));

        projectRepository.saveAll(Arrays.asList(ecommerce, mobile, analytics, security));

        // Create Resources with Categories and Access Types
        Resource prodDB = createResource("Production Database", "Main production PostgreSQL database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.MANAGER_CONTROLLED, "jdbc:postgresql://prod-db:5432/ecommerce", false, ecommerce, "priya.manager");
        Resource testDB = createResource("Test Database", "Testing environment database", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.COMMON, "jdbc:postgresql://test-db:5432/ecommerce_test", false, ecommerce, "anita.teamlead");
        Resource apiGateway = createResource("API Gateway", "Main API gateway for microservices", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://api.company.com/gateway", true, null, "rajesh.admin");
        Resource jenkins = createResource("Jenkins CI/CD", "Continuous integration and deployment server", Resource.ResourceType.URL, Resource.ResourceCategory.CLOUD_SERVICES, Resource.ResourceAccessType.COMMON, "https://jenkins.company.com", true, null, "rajesh.admin");
        Resource sonarqube = createResource("SonarQube", "Code quality and security analysis", Resource.ResourceType.URL, Resource.ResourceCategory.CLOUD_SERVICES, Resource.ResourceAccessType.COMMON, "https://sonar.company.com", true, null, "rajesh.admin");
        Resource mobileAPI = createResource("Mobile Banking API", "REST API for mobile banking operations", Resource.ResourceType.API, Resource.ResourceCategory.API, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://mobile-api.bank.com", false, mobile, "james.manager");
        Resource paymentGateway = createResource("Payment Gateway", "External payment processing service", Resource.ResourceType.CLOUD_SERVICE, Resource.ResourceCategory.CLOUD_SERVICES, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://payments.company.com", false, ecommerce, "priya.manager");
        Resource analyticsDB = createResource("Analytics Warehouse", "Data warehouse for business analytics", Resource.ResourceType.DATABASE, Resource.ResourceCategory.DATABASE, Resource.ResourceAccessType.COMMON, "jdbc:postgresql://analytics-db:5432/warehouse", false, analytics, "priya.manager");
        
        // Additional categorized resources
        Resource projectDocs = createResource("Project Documentation", "Comprehensive project documentation", Resource.ResourceType.PDF, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://docs.company.com/project.pdf", false, ecommerce, "arjun.dev");
        Resource codeRepo = createResource("Main Repository", "Primary code repository", Resource.ResourceType.GITHUB_LINK, Resource.ResourceCategory.REPOSITORY, Resource.ResourceAccessType.COMMON, "https://github.com/company/ecommerce", false, ecommerce, "emily.dev");
        Resource apiDocs = createResource("API Documentation", "REST API documentation", Resource.ResourceType.DOC, Resource.ResourceCategory.DOCUMENTATION, Resource.ResourceAccessType.COMMON, "https://api-docs.company.com", false, mobile, "vikram.dev");
        Resource externalService = createResource("External Service", "Third-party integration service", Resource.ResourceType.URL, Resource.ResourceCategory.EXTERNAL_LINKS, Resource.ResourceAccessType.MANAGER_CONTROLLED, "https://external-service.com", false, mobile, "james.manager");

        // Auto-grant permissions for COMMON resources to team members only
        List<User> teamMembers = Arrays.asList(dev1, dev2, dev3, tester1, tester2);
        List<Resource> commonResources = Arrays.asList(testDB, jenkins, sonarqube, analyticsDB, projectDocs, codeRepo, apiDocs);
        
        // Grant READ access to COMMON resources for team members only
        for (User teamMember : teamMembers) {
            for (Resource resource : commonResources) {
                if (resource.getAccessType() == Resource.ResourceAccessType.COMMON) {
                    createPermission(teamMember, resource, Permission.AccessLevel.READ, null);
                }
            }
        }
        
        // Specific permissions for MANAGER_CONTROLLED resources
        createPermission(manager1, prodDB, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, apiGateway, Permission.AccessLevel.ADMIN, null);
        createPermission(manager1, paymentGateway, Permission.AccessLevel.ADMIN, null);
        createPermission(manager2, mobileAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(manager2, externalService, Permission.AccessLevel.ADMIN, null);
        createPermission(teamlead1, prodDB, Permission.AccessLevel.WRITE, null);
        createPermission(teamlead1, apiGateway, Permission.AccessLevel.WRITE, null);
        createPermission(teamlead2, mobileAPI, Permission.AccessLevel.WRITE, null);
        createPermission(admin, prodDB, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, apiGateway, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, mobileAPI, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, paymentGateway, Permission.AccessLevel.ADMIN, null);
        createPermission(admin, externalService, Permission.AccessLevel.ADMIN, null);

        // Create Access Requests - project-scoped with existing resources
        // E-commerce project PENDING requests (prodDB, paymentGateway belong to ecommerce)
        createAccessRequest(dev1, prodDB, Permission.AccessLevel.READ, "Need access to production database for debugging critical issue", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(dev2, paymentGateway, Permission.AccessLevel.READ, "Need access to payment gateway for checkout feature development", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(tester1, prodDB, Permission.AccessLevel.READ, "Need production database access for performance testing", AccessRequest.RequestStatus.PENDING, null, null, null);
        
        // E-commerce project completed requests
        createAccessRequest(tester1, paymentGateway, Permission.AccessLevel.READ, "Need access to payment gateway for integration testing", AccessRequest.RequestStatus.APPROVED, manager1, "Approved for integration testing", LocalDateTime.now().minusDays(2));
        createAccessRequest(dev2, prodDB, Permission.AccessLevel.WRITE, "Need write access for database schema updates", AccessRequest.RequestStatus.REJECTED, manager1, "Use development environment for schema changes", LocalDateTime.now().minusDays(3));
        
        // Mobile banking project PENDING requests (mobileAPI, externalService belong to mobile)
        createAccessRequest(dev2, mobileAPI, Permission.AccessLevel.READ, "Need API access for mobile banking integration", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(dev3, externalService, Permission.AccessLevel.READ, "Need external service access for payment processing integration", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(tester2, mobileAPI, Permission.AccessLevel.READ, "Need mobile API access for automated testing", AccessRequest.RequestStatus.PENDING, null, null, null);
        
        // Mobile banking project completed requests
        createAccessRequest(tester2, externalService, Permission.AccessLevel.READ, "Need access to external service for testing", AccessRequest.RequestStatus.REJECTED, manager2, "Use test environment instead", LocalDateTime.now().minusDays(5));
        createAccessRequest(dev3, mobileAPI, Permission.AccessLevel.WRITE, "Need write access for API endpoint development", AccessRequest.RequestStatus.APPROVED, manager2, "Approved for development work", LocalDateTime.now().minusDays(1));
        
        // Additional user requests for testing user portal
        createAccessRequest(dev1, paymentGateway, Permission.AccessLevel.READ, "Need payment gateway access for e-commerce development", AccessRequest.RequestStatus.PENDING, null, null, null);
        createAccessRequest(tester2, prodDB, Permission.AccessLevel.READ, "Need production database access for bug investigation", AccessRequest.RequestStatus.APPROVED, manager1, "Approved for bug fixing", LocalDateTime.now().minusDays(1));
        createAccessRequest(dev3, prodDB, Permission.AccessLevel.READ, "Need database access for performance optimization", AccessRequest.RequestStatus.REJECTED, manager1, "Use staging environment", LocalDateTime.now().minusDays(2));
        


        // Create Audit Logs
        createAuditLog(admin, null, AuditLog.ActionType.USER_LOGIN, "Admin user logged into system");
        createAuditLog(dev1, prodDB, AuditLog.ActionType.ACCESS_GRANTED, "Read access granted to production database");
        createAuditLog(manager1, testDB, AuditLog.ActionType.PERMISSION_CHANGED, "Updated database permissions for development team");
        createAuditLog(dev2, mobileAPI, AuditLog.ActionType.RESOURCE_CREATED, "Created new mobile API endpoint for account balance");
        createAuditLog(tester1, null, AuditLog.ActionType.ACCESS_REQUESTED, "Requested access to mobile banking API");
        createAuditLog(manager2, null, AuditLog.ActionType.ACCESS_REVOKED, "Revoked expired permissions for former team member");
        createAuditLog(dev3, analyticsDB, AuditLog.ActionType.RESOURCE_UPDATED, "Updated analytics database connection parameters");
        createAuditLog(admin, jenkins, AuditLog.ActionType.RESOURCE_CREATED, "Configured new CI/CD pipeline for mobile project");
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