import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TeamService } from '../services/team.service';
import { ToastService } from '../services/toast.service';
import { ToastComponent } from '../components/toast/toast.component';

@Component({
  selector: 'app-superadmin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, ToastComponent],
  templateUrl: './superadmin.component.html',
  styleUrls: ['./superadmin.component.css']
})
export class SuperAdminComponent implements OnInit {
  activeTab: string = 'overview';
  selectedProject: any = null;
  showResourceDetailModal = false;
  selectedResourceDetail: any = null;
  resourceAccessList: any[] = [];
  loading = true;
  sidebarHidden = false;
  showNotifications = false;
  showUserDropdown = false;
  currentUsername: string = '';
  currentUserRole: string = '';
  notifications: any[] = [];
  unreadNotificationCount: number = 0;

  // Overview data
  systemStats = {
    totalUsers: 0,
    totalProjects: 0,
    totalResources: 0,
    totalRequests: 0,
    activeUsers: 0,
    activeProjects: 0,
    pendingRequests: 0,
    approvedRequests: 0
  };

  // Members data
  allUsers: any[] = [];
  filteredUsers: any[] = [];
  userFilters = {
    search: '',
    role: '',
    status: ''
  };
  userSort = {
    field: 'createdAt',
    direction: 'desc' as 'asc' | 'desc'
  };

  // Resources data
  allResources: any[] = [];
  sharedResources: any[] = [];
  projectResources: any[] = [];
  allProjects: any[] = [];
  projectCommonResources: any[] = [];
  projectControlledResources: any[] = [];

  // Chart data
  projectStatusChart = {
    active: 0,
    inactive: 0,
    completed: 0,
    archived: 0
  };

  userRoleChart = {
    admins: 0,
    managers: 0,
    teamLeads: 0,
    members: 0
  };
  resourceFilters = {
    search: '',
    type: '',
    category: '',
    accessType: ''
  };

  // Activity data
  recentActivities: any[] = [];
  activityFilters = {
    team: '',
    action: '',
    dateFrom: '',
    dateTo: ''
  };

  constructor(
    private router: Router,
    private teamService: TeamService,
    private fb: FormBuilder,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeUser();
    this.loadSystemData();
  }

  initializeUser(): void {
    this.currentUsername = this.teamService.getCurrentUsername();
    this.currentUserRole = this.teamService.getCurrentUserRole();
    this.loadNotifications();
  }

  loadSystemData(): void {
    this.loading = true;
    this.loadSystemStats();
    this.loadAllUsers();
    this.loadAllResources();
    this.loadRecentActivities();
    this.loading = false;
  }

  loadSystemStats(): void {
    this.teamService.getAllTeams().subscribe({
      next: (projects) => {
        this.systemStats.totalProjects = projects?.length || 0;
        this.systemStats.activeProjects = projects?.filter(p => p.status === 'ACTIVE').length || 0;
      },
      error: (err) => console.error('Error loading projects:', err)
    });

    this.teamService.getAllEmployees().subscribe({
      next: (users) => {
        this.systemStats.totalUsers = users?.length || 0;
        this.systemStats.activeUsers = users?.length || 0;
      },
      error: (err) => console.error('Error loading users:', err)
    });
  }

  loadAllUsers(): void {
    this.teamService.getAllEmployees().subscribe({
      next: (users) => {
        this.allUsers = users || [];
        this.filteredUsers = [...this.allUsers];
        this.applyUserFilters();
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.allUsers = [];
        this.filteredUsers = [];
      }
    });
  }

  loadAllResources(): void {
    this.teamService.getSharedDocuments().subscribe({
      next: (sharedDocs) => {
        this.sharedResources = sharedDocs || [];
        console.log('Loaded shared resources:', this.sharedResources);
      },
      error: (err) => {
        console.error('Error loading shared resources:', err);
        this.sharedResources = [];
      }
    });
    
    this.teamService.getAllTeams().subscribe({
      next: (projects) => {
        this.allProjects = projects || [];
        console.log('Loaded all projects:', this.allProjects);
      },
      error: (err) => {
        console.error('Error loading projects:', err);
        this.allProjects = [];
      }
    });
    
    this.allResources = [...this.sharedResources, ...this.projectResources];
  }

  loadRecentActivities(): void {
    // Load all access requests as activities
    this.teamService.getAllTeams().subscribe({
      next: (projects: any) => {
        const activities: any[] = [];
        
        projects.forEach((project: any) => {
          this.teamService.getAccessRequests(project.id).subscribe({
            next: (requests: any) => {
              requests.forEach((request: any) => {
                activities.push({
                  id: request.id,
                  type: 'ACCESS_REQUEST',
                  action: this.getActivityAction(request.status),
                  user: request.userName || request.requestedBy,
                  resource: request.resourceName,
                  project: project.name,
                  status: request.status,
                  timestamp: request.requestedAt,
                  details: request.justification
                });
              });
              
              // Sort activities by timestamp
              this.recentActivities = activities.sort((a, b) => 
                new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
              );
            },
            error: (err: any) => console.error('Error loading project requests:', err)
          });
        });
      },
      error: (err: any) => console.error('Error loading projects for activities:', err)
    });
  }

  loadNotifications(): void {
    this.notifications = [
      {
        id: 1,
        title: 'System Status',
        message: 'All systems operational',
        type: 'info',
        isRead: false,
        createdAt: new Date().toISOString()
      }
    ];
    this.unreadNotificationCount = this.notifications.filter(n => !n.isRead).length;
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  isTabActive(tab: string): boolean {
    return this.activeTab === tab;
  }

  toggleSidebar(): void {
    this.sidebarHidden = !this.sidebarHidden;
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  toggleUserDropdown(): void {
    this.showUserDropdown = !this.showUserDropdown;
  }

  markNotificationAsRead(notificationId: number): void {
    const notification = this.notifications.find(n => n.id === notificationId);
    if (notification && !notification.isRead) {
      notification.isRead = true;
      this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
    }
  }

  applyUserFilters(): void {
    this.filteredUsers = this.allUsers.filter(user => {
      const searchMatch = !this.userFilters.search || 
        user.username.toLowerCase().includes(this.userFilters.search.toLowerCase()) ||
        user.email.toLowerCase().includes(this.userFilters.search.toLowerCase()) ||
        user.fullName?.toLowerCase().includes(this.userFilters.search.toLowerCase());
      
      const roleMatch = !this.userFilters.role || user.role === this.userFilters.role;
      
      return searchMatch && roleMatch;
    });
    
    this.applySorting();
  }

  applySorting(): void {
    this.filteredUsers.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (this.userSort.field) {
        case 'createdAt':
          aValue = new Date(a.createdAt || 0);
          bValue = new Date(b.createdAt || 0);
          break;
        case 'username':
          aValue = a.username.toLowerCase();
          bValue = b.username.toLowerCase();
          break;
        case 'email':
          aValue = a.email.toLowerCase();
          bValue = b.email.toLowerCase();
          break;
        case 'role':
          aValue = a.role;
          bValue = b.role;
          break;
        default:
          return 0;
      }
      
      if (aValue < bValue) return this.userSort.direction === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.userSort.direction === 'asc' ? 1 : -1;
      return 0;
    });
  }

  toggleSortDirection(): void {
    this.userSort.direction = this.userSort.direction === 'asc' ? 'desc' : 'asc';
    this.applySorting();
  }

  clearUserFilters(): void {
    this.userFilters = {
      search: '',
      role: '',
      status: ''
    };
    this.applyUserFilters();
  }

  getTabTitle(): string {
    switch (this.activeTab) {
      case 'overview': return 'System Overview';
      case 'members': return 'User Management';
      case 'shared-resources': return 'Shared Resources';
      case 'project-resources': return 'Project Resources';
      case 'project-detail': return this.selectedProject ? `${this.selectedProject.name} Resources` : 'Project Resources';
      case 'activity-history': return 'Activity History';
      default: return 'Super Admin Dashboard';
    }
  }

  getCurrentUserEmail(): string {
    return `${this.currentUsername}@company.com`;
  }

  getUserAvatar(): string {
    return `https://api.dicebear.com/7.x/initials/svg?seed=${this.currentUsername}&backgroundColor=1976d2,2196f3,4caf50,ff9800,9c27b0&textColor=ffffff`;
  }

  logout(): void {
    this.teamService.logout();
    this.router.navigate(['/login']);
  }

  onImageError(event: any): void {
    event.target.style.display = 'none';
  }

  viewProjectResources(project: any): void {
    this.selectedProject = project;
    this.loadProjectResources(project.id);
    this.setActiveTab('project-detail');
  }

  loadProjectResources(projectId: number): void {
    // Load common resources for the project
    this.teamService.getCommonResources(projectId).subscribe({
      next: (resources: any) => {
        this.projectCommonResources = resources || [];
        console.log('Loaded project common resources:', this.projectCommonResources);
      },
      error: (err: any) => {
        console.error('Error loading project common resources:', err);
        this.projectCommonResources = [];
      }
    });

    // Load controlled resources for the project
    this.teamService.getManagerControlledResources(projectId).subscribe({
      next: (resources: any) => {
        this.projectControlledResources = resources || [];
        console.log('Loaded project controlled resources:', this.projectControlledResources);
      },
      error: (err: any) => {
        console.error('Error loading project controlled resources:', err);
        this.projectControlledResources = [];
      }
    });
  }

  backToProjectList(): void {
    this.selectedProject = null;
    this.setActiveTab('project-resources');
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'active': return 'status-active';
      case 'inactive': return 'status-inactive';
      case 'completed': return 'status-completed';
      case 'archived': return 'status-archived';
      default: return 'status-active';
    }
  }

  viewResourceDetail(resource: any): void {
    this.selectedResourceDetail = resource;
    this.loadResourceAccess(resource.id);
    this.showResourceDetailModal = true;
  }

  loadResourceAccess(resourceId: number): void {
    this.teamService.getResourceAccess(resourceId).subscribe({
      next: (accessList: any) => {
        this.resourceAccessList = accessList || [];
        console.log('Loaded resource access list:', this.resourceAccessList);
      },
      error: (err: any) => {
        console.error('Error loading resource access:', err);
        this.resourceAccessList = [];
      }
    });
  }

  closeResourceDetailModal(): void {
    this.showResourceDetailModal = false;
    this.selectedResourceDetail = null;
    this.resourceAccessList = [];
  }

  formatFileSize(bytes: number): string {
    if (!bytes) return 'N/A';
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
  }

  getActivityAction(status: string): string {
    switch (status) {
      case 'PENDING': return 'requested access to';
      case 'APPROVED': return 'was granted access to';
      case 'REJECTED': return 'was denied access to';
      default: return 'interacted with';
    }
  }

  getActivityIcon(type: string, status?: string): string {
    if (type === 'ACCESS_REQUEST') {
      switch (status) {
        case 'APPROVED': return 'pi-check-circle';
        case 'REJECTED': return 'pi-times-circle';
        case 'PENDING': return 'pi-clock';
        default: return 'pi-key';
      }
    }
    return 'pi-info-circle';
  }

  getActivityIconClass(status: string): string {
    switch (status) {
      case 'APPROVED': return 'approved';
      case 'REJECTED': return 'rejected';
      case 'PENDING': return 'pending';
      default: return 'info';
    }
  }

  applyActivityFilters(): void {
    // Filter activities based on selected filters
    // Implementation would filter this.recentActivities based on this.activityFilters
  }

  clearActivityFilters(): void {
    this.activityFilters = {
      team: '',
      action: '',
      dateFrom: '',
      dateTo: ''
    };
    this.applyActivityFilters();
  }

  getUserRolePercentage(role: string): number {
    const total = this.systemStats.totalUsers;
    if (total === 0) return 0;
    
    switch (role) {
      case 'admins': return Math.round((this.userRoleChart.admins / total) * 100);
      case 'managers': return Math.round((this.userRoleChart.managers / total) * 100);
      case 'teamLeads': return Math.round((this.userRoleChart.teamLeads / total) * 100);
      case 'members': return Math.round((this.userRoleChart.members / total) * 100);
      default: return 0;
    }
  }
}