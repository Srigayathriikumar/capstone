import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TeamService, Team, TeamMember, TeamResource, AccessRequest, CreateResourceRequest, ResourceAccess, AccessRequestDTO, Notification, ChatMessage } from '../services/team.service';
import { ToastService } from '../services/toast.service';
import { LoadingService } from '../services/loading.service';
import { ButtonComponent } from '../components/ui/button/button.component';
import { CardComponent } from '../components/ui/card/card.component';
import { ToastComponent } from '../components/toast/toast.component';
import { CommonResourcesComponent } from '../pages/resources/common-resources/common-resources.component';
import { CreateProjectComponent } from '../pages/create-project/create-project.component';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { BadgeModule } from 'primeng/badge';
import { TagModule } from 'primeng/tag';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { Subject, of } from 'rxjs';

type TabType = 'overview' | 'members' | 'resources' | 'requests' | 'audit' | 'uploads';

@Component({
  selector: 'app-teamdetails',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, ToastComponent, CommonResourcesComponent, CreateProjectComponent, ButtonModule, CardModule, InputTextModule, DropdownModule, DialogModule, ToastModule, TableModule, BadgeModule, TagModule],
  templateUrl: 'teamdetails.html',
  styleUrls: ['./teamdetails.css', './toast-notification.css']
})
export class Teamdetails implements OnInit {
  team: Team | null = null;
  members: TeamMember[] = [];
  resources: TeamResource[] = [];
  commonResources: TeamResource[] = [];
  managerResources: TeamResource[] = [];
  accessRequests: AccessRequest[] = [];
  loading = true;
  activeTab: any = 'overview';
  teamId: number = 0;
  showAddResourceModal = false;
  addResourceForm: FormGroup;
  selectedAccessType: string = 'COMMON';
  selectedResourceTab: string = 'common';
  selectedFile: File | null = null;
  uploadMode: 'url' | 'file' = 'url';
  manager: TeamMember | null = null;
  teamMembers: TeamMember[] = [];
  showResourceAccessModal = false;
  selectedResource: TeamResource | null = null;
  resourceAccess: ResourceAccess[] = [];
  loadingResourceAccess = false;
  showRequestAccessModal = false;
  requestAccessForm: FormGroup;
  showRejectModal = false;
  selectedRequest: AccessRequest | null = null;
  rejectForm: FormGroup;
  showEditAccessModal = false;
  editAccessForm: FormGroup;
  selectedUserGroups: string[] = [];
  currentUserId: number = 0;
  currentUsername: string = '';
  currentUserRole: string = '';
  adminStats: any = null;
  notifications: Notification[] = [];
  unreadNotificationCount: number = 0;
  showNotifications = false;
  showUserDropdown = false;
  userAccessRequests: AccessRequest[] = [];
  pendingApprovalRequests: AccessRequest[] = [];
  userUploads: TeamResource[] = [];
  allProjects: Team[] = [];
  sidebarHidden = false;
  showProjectSwitcher = false;
  
  // Chat properties
  showTeamChat = false;
  chatMessages: ChatMessage[] = [];
  newMessage = '';
  
  // Audit log properties
  filteredAuditRequests: AccessRequest[] = [];
  paginatedAuditRequests: AccessRequest[] = [];
  auditFilters = {
    user: '',
    status: '',
    resource: '',
    dateFrom: '',
    dateTo: ''
  };
  auditSort = {
    field: 'requestedAt',
    direction: 'desc' as 'asc' | 'desc'
  };
  auditPagination = {
    currentPage: 1,
    pageSize: 25,
    totalPages: 1
  };
  jumpToPage: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teamService: TeamService,
    private fb: FormBuilder,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef
  ) {

    this.addResourceForm = this.fb.group({
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      type: ['DATABASE', [Validators.required]],
      category: ['OTHER', [Validators.required]],
      accessType: ['COMMON', [Validators.required]],
      resourceUrl: [''],
      uploadMode: ['url'],
      allowedUserGroups: ['']
    });
    
    this.requestAccessForm = this.fb.group({
      justification: ['', [Validators.required, Validators.minLength(10)]]
    });
    
    this.rejectForm = this.fb.group({
      rejectionReason: ['', [Validators.required, Validators.minLength(10)]]
    });
    
    this.editAccessForm = this.fb.group({
      accessType: ['COMMON', [Validators.required]],
      allowedUserGroups: ['']
    });
    
    this.createUserForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      role: ['', [Validators.required]]
    });
    
    this.profileSettingsForm = this.fb.group({
      username: [{ value: '', disabled: true }],
      email: ['', [Validators.required, Validators.email]],
      fullName: [''],
      role: [{ value: '', disabled: true }],
      timezone: ['UTC'],
      language: ['en'],
      emailNotifications: [true]
    });
    
    this.shareDocumentForm = this.fb.group({
      title: ['', [Validators.required]],
      message: ['', [Validators.required]],
      type: ['PDF', [Validators.required]],
      documentUrl: [''],
      shareMode: ['url']
    });
    
    this.deleteResourceForm = this.fb.group({
      deletionReason: ['', [Validators.required, Validators.minLength(10)]]
    });
    

  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.teamId = +params['id'];
      this.initializeUser();
      this.loadTeamData();
      this.loadAllProjects();
    });
    
    // Setup debounced search
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(searchTerm => {
        if (!searchTerm.trim()) {
          return of([]);
        }
        console.log('API search for term:', searchTerm);
        return this.teamService.searchUsers(searchTerm).pipe(
          catchError(err => {
            console.error('API search error:', err);
            return of([]);
          })
        );
      })
    ).subscribe({
      next: (searchResults) => {
        console.log('API search results:', searchResults);
        if (searchResults && searchResults.length > 0) {
          const availableResults = searchResults.filter(employee => 
            !this.isEmployeeSelected(employee.id)
          );
          // Only update if we got better results from API
          if (availableResults.length > this.filteredEmployees.length) {
            this.filteredEmployees = availableResults;
            console.log('Updated with API results:', this.filteredEmployees);
          }
        }
        this.isSearchingEmployees = false;
      },
      error: (err) => {
        console.error('Error in debounced search subscription:', err);
        this.isSearchingEmployees = false;
      }
    });
  }
  
  initializeUser(): void {
    this.currentUserId = this.teamService.getCurrentUserId();
    this.currentUsername = this.teamService.getCurrentUsername();
    this.currentUserRole = this.teamService.getCurrentUserRole();
    
    console.log('User initialized:', {
      userId: this.currentUserId,
      username: this.currentUsername,
      role: this.currentUserRole,
      isManager: this.isManagerOrAdmin()
    });
    this.loadNotifications();
    this.loadUserAccessRequests();
    this.loadUserUploads();
    if (this.isManagerOrAdmin()) {
      this.loadPendingApprovalRequests();
    }
    if (this.isAdmin()) {
      this.loadAdminStatistics();
    }
  }

  loadTeamData(): void {
    this.loading = true;
    this.loadingService.show();
    
    this.teamService.getTeamById(this.teamId).subscribe({
      next: (team) => {
        this.team = team;
        this.loadMembers();
        this.loadResources();
        this.loadAccessRequests();
      },
      error: (err) => {
        console.error('Error loading team:', err);
        this.loading = false;
        this.loadingService.hide();
      }
    });
  }

  loadMembers(): void {
    this.teamService.getTeamMembers(this.teamId).subscribe({
      next: (members) => {
        this.members = members || [];
        this.manager = members?.find(m => m.role === 'PROJECT_MANAGER') || null;
        this.teamMembers = members?.filter(m => m.role !== 'PROJECT_MANAGER') || [];
        this.checkLoadingComplete();
      },
      error: (err) => {
        console.error('Error loading members:', err);
        this.members = [];
        this.manager = null;
        this.teamMembers = [];
        this.checkLoadingComplete();
      }
    });
  }

  loadResources(): void {
    // Load all resources
    this.teamService.getTeamResources(this.teamId).subscribe({
      next: (resources) => {
        this.resources = resources || [];
        this.loadCategorizedResources();
        this.checkLoadingComplete();
      },
      error: (err) => {
        console.error('Error loading resources:', err);
        this.resources = [];
        this.commonResources = [];
        this.managerResources = [];
        this.checkLoadingComplete();
      }
    });
  }

  loadCategorizedResources(): void {
    // Load common resources
    this.teamService.getCommonResources(this.teamId).subscribe({
      next: (resources) => {
        this.commonResources = resources || [];
      },
      error: (err) => {
        console.error('Error loading common resources:', err);
        this.commonResources = [];
      }
    });

    // Load manager controlled resources
    this.teamService.getManagerControlledResources(this.teamId).subscribe({
      next: (resources) => {
        this.managerResources = resources || [];
      },
      error: (err) => {
        console.error('Error loading manager controlled resources:', err);
        this.managerResources = [];
      }
    });
  }

  loadAccessRequests(): void {
    this.teamService.getAccessRequests(this.teamId).subscribe({
      next: (requests) => {
        this.accessRequests = requests || [];
        this.initializeAuditLog();
        this.checkLoadingComplete();
      },
      error: (err) => {
        console.error('Error loading access requests:', err);
        this.accessRequests = [];
        this.checkLoadingComplete();
      }
    });
  }

  checkLoadingComplete(): void {
    if (this.members !== undefined && this.resources !== undefined && this.accessRequests !== undefined) {
      this.loading = false;
      this.loadingService.hide();
      this.cdr.detectChanges();
    }
  }

  setActiveTab(tab: any): void {
    console.log('Setting active tab to:', tab);
    this.activeTab = tab;
  }

  isTabActive(tab: any): boolean {
    return this.activeTab === tab;
  }

  goBack(): void {
    // Navigate to first available team or logout
    this.teamService.getTeams().subscribe({
      next: (teams) => {
        if (teams && teams.length > 0) {
          const firstTeam = teams.find(t => t.id !== this.teamId) || teams[0];
          this.router.navigate(['/team', firstTeam.id]);
        } else {
          this.logout();
        }
      },
      error: () => this.logout()
    });
  }

  navigateToAuditLog(): void {
    this.router.navigate(['/team', this.teamId, 'audit-log']);
  }

  getStatusClass(status: string): string {
    return status ? `status-${status.toLowerCase()}` : 'status-unknown';
  }

  openAddResourceModal(accessType: string = 'COMMON'): void {
    console.log('Opening add resource modal for:', accessType);
    this.selectedAccessType = accessType;
    this.addResourceForm.patchValue({ accessType: accessType });
    this.showAddResourceModal = true;
  }

  closeAddResourceModal(): void {
    this.showAddResourceModal = false;
    this.selectedAccessType = 'COMMON';
    this.selectedFile = null;
    this.uploadMode = 'url';
    this.addResourceForm.reset({
      name: '',
      description: '',
      type: 'DATABASE',
      category: 'OTHER',
      accessType: 'COMMON',
      resourceUrl: '',
      uploadMode: 'url',
      allowedUserGroups: ''
    });
  }

  onAddResource(): void {
    if (this.addResourceForm.invalid) {
      this.toastService.error('Error', 'Please fill in all required fields');
      return;
    }
    
    if (this.uploadMode === 'file' && !this.selectedFile) {
      this.toastService.error('Error', 'Please select a file to upload');
      return;
    }
    
    const resourceData: CreateResourceRequest = {
      name: this.addResourceForm.get('name')?.value || '',
      description: this.addResourceForm.get('description')?.value || '',
      type: this.addResourceForm.get('type')?.value || 'DATABASE',
      category: this.addResourceForm.get('category')?.value || 'OTHER',
      isGlobal: false,
      projectId: this.team?.id || this.teamId,
      allowedUserGroups: this.addResourceForm.get('allowedUserGroups')?.value || ''
    };

    if (this.uploadMode === 'url') {
      resourceData.resourceUrl = this.addResourceForm.get('resourceUrl')?.value || '';
    }
    
    this.toastService.info('Processing', 'Adding resource...');
    
    if (this.uploadMode === 'file' && this.selectedFile) {
      this.teamService.createResourceWithFile(this.selectedFile, resourceData).subscribe({
        next: (response) => {
          this.toastService.success('Success', `Resource "${resourceData.name}" uploaded successfully!`);
          this.closeAddResourceModal();
          this.loadResources();
          this.loadUserUploads();
        },
        error: (err) => {
          this.toastService.error('Error', 'Failed to upload resource. Please try again.');
        }
      });
    } else {
      this.teamService.createResource(resourceData).subscribe({
        next: (response) => {
          this.toastService.success('Success', `Resource "${resourceData.name}" added successfully!`);
          this.closeAddResourceModal();
          this.loadResources();
          this.loadUserUploads();
        },
        error: (err) => {
          this.toastService.error('Error', 'Failed to add resource. Please try again.');
        }
      });
    }
  }

  showResourceAccess(resource: TeamResource): void {
    this.selectedResource = resource;
    this.showResourceAccessModal = true;
    this.loadResourceAccess(resource.id);
  }

  closeResourceAccessModal(): void {
    this.showResourceAccessModal = false;
    this.selectedResource = null;
    this.resourceAccess = [];
  }

  loadResourceAccess(resourceId: number): void {
    this.loadingResourceAccess = true;
    this.loadingService.show();
    this.teamService.getResourceAccess(resourceId).subscribe({
      next: (access) => {
        this.resourceAccess = access || [];
        this.loadingResourceAccess = false;
        this.loadingService.hide();
      },
      error: (err) => {
        console.error('Error loading resource access:', err);
        this.resourceAccess = [];
        this.loadingResourceAccess = false;
        this.loadingService.hide();
      }
    });
  }
  
  loadNotifications(): void {
    if (this.currentUserId) {
      // Create mock notifications based on recent activity
      this.createMockNotifications();
      
      this.teamService.getUserNotifications(this.currentUserId).subscribe({
        next: (notifications) => {
          if (notifications && notifications.length > 0) {
            this.notifications = notifications;
          }
          this.unreadNotificationCount = this.notifications.filter(n => !n.isRead).length;
        },
        error: (err) => {
          console.error('Error loading notifications:', err);
          this.unreadNotificationCount = this.notifications.filter(n => !n.isRead).length;
        }
      });
    }
  }
  
  createMockNotifications(): void {
    const now = new Date().toISOString();
    const teamName = this.team?.name || 'Team';
    
    this.notifications = [];
    
    if (this.isManagerOrAdmin()) {
      // Manager/Admin notifications
      if (this.pendingApprovalRequests.length > 0) {
        this.notifications.push({
          id: 1,
          title: `${this.pendingApprovalRequests.length} Pending Approvals`,
          message: `${this.pendingApprovalRequests.map(r => r.userName || r.requestedBy).join(', ')} requesting access`,
          type: 'warning',
          isRead: false,
          createdAt: now
        });
      }
      
      this.notifications.push({
        id: 2,
        title: `Team Management`,
        message: `Managing ${this.resources.length} resources and ${this.members.length} members in ${teamName}`,
        type: 'info',
        isRead: false,
        createdAt: now
      });
      
      const recentRequests = this.accessRequests.slice(0, 2);
      recentRequests.forEach((request, index) => {
        this.notifications.push({
          id: index + 3,
          title: `Team Activity`,
          message: `${request.userName || request.requestedBy} ${request.status.toLowerCase()} access to ${request.resourceName}`,
          type: request.status === 'APPROVED' ? 'success' : request.status === 'REJECTED' ? 'error' : 'info',
          isRead: false,
          createdAt: request.requestedAt
        });
      });
    } else {
      // Team Member notifications
      const myRequests = this.userAccessRequests.slice(0, 3);
      myRequests.forEach((request, index) => {
        let message = `Access to ${request.resourceName} ${request.status.toLowerCase()}`;
        if (request.status === 'REJECTED' && request.approverComments) {
          message += `. Reason: ${request.approverComments}`;
        }
        
        this.notifications.push({
          id: index + 1,
          title: `Your Request ${request.status}`,
          message: message,
          type: request.status === 'APPROVED' ? 'success' : request.status === 'REJECTED' ? 'error' : 'info',
          isRead: false,
          createdAt: request.requestedAt
        });
      });
      
      if (this.userUploads.length > 0) {
        this.notifications.push({
          id: 998,
          title: 'Your Contributions',
          message: `You have ${this.userUploads.length} resources uploaded to ${teamName}`,
          type: 'info',
          isRead: false,
          createdAt: now
        });
      }
      
      this.notifications.push({
        id: 999,
        title: `Welcome to ${teamName}`,
        message: `You have access to ${this.resources.filter(r => r.accessType === 'COMMON').length} common resources`,
        type: 'info',
        isRead: false,
        createdAt: now
      });
    }
  }
  
  loadUserAccessRequests(): void {
    if (this.team?.id || this.teamId) {
      const id = this.team?.id || this.teamId;
      console.log('Loading user access requests for teamId:', id, 'userId:', this.currentUserId);
      this.teamService.getAccessRequests(id).subscribe({
        next: (requests) => {
          console.log('All requests received:', requests);
          console.log('Current user ID:', this.currentUserId);
          console.log('Current username:', this.currentUsername);
          this.userAccessRequests = (requests || []).filter(req => {
            console.log('Checking request:', req, 'userId match:', req.userId === this.currentUserId);
            return req.userId === this.currentUserId;
          });
          console.log('Filtered user requests:', this.userAccessRequests);
          console.log('User access requests length:', this.userAccessRequests.length);
        },
        error: (err) => console.error('Error loading user access requests:', err)
      });
    }
  }
  
  loadPendingApprovalRequests(): void {
    if (this.team?.id || this.teamId) {
      const id = this.team?.id || this.teamId;
      this.teamService.getAccessRequests(id).subscribe({
        next: (requests) => {
          console.log('Loading pending approval requests, all requests:', requests);
          this.pendingApprovalRequests = (requests || []).filter(req => req.status === 'PENDING');
          console.log('Pending approval requests:', this.pendingApprovalRequests);
          console.log('Pending approval requests length:', this.pendingApprovalRequests.length);
        },
        error: (err) => console.error('Error loading pending approval requests:', err)
      });
    }
  }

  loadUserUploads(): void {
    if (this.team?.id || this.teamId) {
      const id = this.team?.id || this.teamId;
      this.teamService.getTeamResources(id).subscribe({
        next: (resources) => {
          this.userUploads = (resources || []).filter(resource => 
            resource.createdBy === this.currentUsername || resource.uploadedBy === this.currentUsername
          );
          console.log('User uploads:', this.userUploads);
        },
        error: (err) => console.error('Error loading user uploads:', err)
      });
    }
  }
  
  // Access Request Methods
  openRequestAccessModal(resource: TeamResource): void {
    this.selectedResource = resource;
    this.showRequestAccessModal = true;
    this.requestAccessForm.reset();
  }
  
  closeRequestAccessModal(): void {
    this.showRequestAccessModal = false;
    this.selectedResource = null;
    this.requestAccessForm.reset();
  }
  
  onRequestAccess(): void {
    console.log('Creating access request for resource:', this.selectedResource);
    if (this.requestAccessForm.invalid || !this.selectedResource) {
      this.toastService.error('Error', 'Please fill in all required fields');
      return;
    }
    
    const requestData: AccessRequestDTO = {
      resourceId: this.selectedResource.id,
      requestedAccessLevel: 'read',
      justification: this.requestAccessForm.get('justification')?.value
    };
    
    this.teamService.createAccessRequest(this.currentUsername, requestData, this.team?.id || this.teamId).subscribe({
      next: (response) => {
        this.toastService.success('Success', `Access request for "${this.selectedResource?.name}" submitted successfully!`);
        this.closeRequestAccessModal();
        this.loadUserAccessRequests();
        this.loadAccessRequests();
        this.refreshRecentActivity();
      },
      error: (err) => {
        const errorMessage = err.error?.message || 'Failed to submit access request';
        console.error('Error:', err);
        this.toastService.error('Error', errorMessage);
      }
    });
  }
  
  approveRequest(requestId: number, comments?: string): void {
    this.teamService.approveAccessRequest(requestId, this.currentUserId, comments).subscribe({
      next: () => {
        this.toastService.success('Success', 'Request approved successfully!');
        this.loadPendingApprovalRequests();
        this.loadAccessRequests();
        this.loadUserAccessRequests();
        this.loadCategorizedResources();
        this.refreshRecentActivity();
      },
      error: (err) => {
        console.error('Error approving request:', err);
        this.toastService.error('Error', 'Failed to approve request');
      }
    });
  }
  
  openRejectModal(request: AccessRequest): void {
    this.selectedRequest = request;
    this.showRejectModal = true;
    this.rejectForm.reset();
  }

  closeRejectModal(): void {
    this.showRejectModal = false;
    this.selectedRequest = null;
    this.rejectForm.reset();
  }

  submitRejection(): void {
    if (this.rejectForm.invalid || !this.selectedRequest) {
      return;
    }

    const rejectionReason = this.rejectForm.get('rejectionReason')?.value;
    
    this.teamService.rejectAccessRequest(this.selectedRequest.id, this.currentUserId, rejectionReason).subscribe({
      next: () => {
        this.toastService.success('Success', 'Request rejected successfully!');
        this.closeRejectModal();
        this.loadPendingApprovalRequests();
        this.loadAccessRequests();
        this.loadUserAccessRequests();
        this.loadCategorizedResources();
        this.refreshRecentActivity();
      },
      error: (err) => {
        console.error('Error rejecting request:', err);
        this.toastService.error('Error', 'Failed to reject request');
      }
    });
  }

  rejectRequest(requestId: number, comments?: string): void {
    this.teamService.rejectAccessRequest(requestId, this.currentUserId, comments).subscribe({
      next: () => {
        this.toastService.success('Success', 'Request rejected successfully!');
        this.loadPendingApprovalRequests();
        this.loadAccessRequests();
      },
      error: (err) => {
        console.error('Error rejecting request:', err);
        this.toastService.error('Error', 'Failed to reject request');
      }
    });
  }
  
  // Notification Methods
  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.loadNotifications();
    }
  }
  
  markNotificationAsRead(notificationId: number): void {
    const notification = this.notifications.find(n => n.id === notificationId);
    if (notification && !notification.isRead) {
      notification.isRead = true;
      this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
      this.toastService.success('Success', 'Notification marked as read');
    }
  }
  
  // Utility Methods
  isManagerOrAdmin(): boolean {
    const isManager = this.currentUserRole === 'PROJECT_MANAGER' || 
                     this.currentUserRole === 'ADMIN' || 
                     this.currentUserRole === 'MANAGER' ||
                     this.currentUserRole === 'TEAMLEAD';
    console.log('isManagerOrAdmin check:', {
      currentUserRole: this.currentUserRole,
      isManager: isManager
    });
    return isManager;
  }
  
  isManagerOrAdminOnly(): boolean {
    const isManagerOnly = this.currentUserRole === 'PROJECT_MANAGER' || 
                          this.currentUserRole === 'ADMIN' || 
                          this.currentUserRole === 'MANAGER';
    return isManagerOnly;
  }
  
  isTeamMember(): boolean {
    return this.currentUserRole === 'TEAM_MEMBER' || this.currentUserRole === 'USER';
  }
  
  isTeamLead(): boolean {
    return this.currentUserRole === 'TEAMLEAD';
  }

  isSuperAdmin(): boolean {
    const role = this.teamService.getCurrentUserRole();
    return role === 'SUPER_ADMIN';
  }

  isAdmin(): boolean {
    const role = this.teamService.getCurrentUserRole();
    return role === 'ADMIN' || role === 'SUPER_ADMIN';
  }

  loadAdminStatistics(): void {
    // Mock admin statistics for demonstration
    this.adminStats = {
      totalProjects: this.allProjects.length || 5,
      activeProjects: this.allProjects.filter(p => p.status === 'ACTIVE').length || 3,
      completedProjects: this.allProjects.filter(p => p.status === 'COMPLETED').length || 2,
      totalUsers: 25,
      activeUsers: 22,
      totalResources: this.resources.length || 15,
      commonResources: this.commonResources.length || 8,
      controlledResources: this.managerResources.length || 7,
      totalAccessRequests: this.accessRequests.length || 12,
      pendingRequests: this.pendingApprovalRequests.length || 3,
      approvedRequests: this.accessRequests.filter(r => r.status === 'APPROVED').length || 8
    };
  }

  navigateToUserManagement(): void {
    this.router.navigate(['/admin/users']);
  }

  navigateToProjectManagement(): void {
    this.router.navigate(['/admin/projects']);
  }
  
  canRequestAccess(resource: TeamResource): boolean {
    return this.isTeamMember() && resource.accessType === 'MANAGER_CONTROLLED';
  }
  
  hasRequestedAccess(resourceId: number): boolean {
    return this.userAccessRequests.some(req => 
      req.resourceId === resourceId && req.status === 'PENDING'
    );
  }

  hasAccess(resourceId: number): boolean {
    // Managers have access to all resources
    if (this.isManagerOrAdmin()) {
      return true;
    }
    // Check if user has approved access to this resource
    return this.userAccessRequests.some(req => 
      req.resourceId === resourceId && req.status === 'APPROVED'
    );
  }

  // File upload methods
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      // Auto-set resource type based on file extension
      const extension = file.name.split('.').pop()?.toUpperCase();
      this.setResourceTypeFromFile(extension);
    }
  }

  setResourceTypeFromFile(extension: string | undefined): void {
    if (!extension) return;
    
    const typeMap: { [key: string]: string } = {
      'PDF': 'PDF',
      'DOC': 'DOC',
      'DOCX': 'DOCX',
      'XLS': 'XLS',
      'XLSX': 'XLSX',
      'PPT': 'PPT',
      'PPTX': 'PPTX',
      'TXT': 'TXT',
      'JPG': 'JPG',
      'JPEG': 'JPEG',
      'PNG': 'PNG',
      'GIF': 'GIF',
      'BMP': 'BMP',
      'TIFF': 'TIFF',
      'SVG': 'SVG',
      'WEBP': 'WEBP',
      'MP4': 'MP4',
      'AVI': 'AVI',
      'MOV': 'MOV',
      'WMV': 'WMV',
      'MP3': 'MP3',
      'WAV': 'WAV',
      'FLAC': 'FLAC',
      'AAC': 'AAC',
      'ZIP': 'ZIP',
      'RAR': 'RAR',
      'JAVA': 'JAVA',
      'JS': 'JAVASCRIPT',
      'PY': 'PYTHON',
      'HTML': 'HTML',
      'CSS': 'CSS',
      'XML': 'XML',
      'JSON': 'JSON',
      'YAML': 'YAML',
      'SQL': 'SQL'
    };
    
    const resourceType = typeMap[extension] || 'OTHER';
    this.addResourceForm.patchValue({ type: resourceType });
    
    // Set category based on file type
    const categoryMap: { [key: string]: string } = {
      'PDF': 'DOCUMENTS',
      'DOC': 'DOCUMENTS',
      'DOCX': 'DOCUMENTS',
      'XLS': 'DOCUMENTS',
      'XLSX': 'DOCUMENTS',
      'PPT': 'DOCUMENTS',
      'PPTX': 'DOCUMENTS',
      'TXT': 'DOCUMENTS',
      'JPG': 'IMAGES',
      'JPEG': 'IMAGES',
      'PNG': 'IMAGES',
      'GIF': 'IMAGES',
      'BMP': 'IMAGES',
      'TIFF': 'IMAGES',
      'SVG': 'IMAGES',
      'WEBP': 'IMAGES',
      'MP4': 'VIDEOS',
      'AVI': 'VIDEOS',
      'MOV': 'VIDEOS',
      'WMV': 'VIDEOS',
      'MP3': 'AUDIO',
      'WAV': 'AUDIO',
      'FLAC': 'AUDIO',
      'AAC': 'AUDIO',
      'ZIP': 'ARCHIVES',
      'RAR': 'ARCHIVES',
      'JAVA': 'CODE_FILES',
      'JS': 'CODE_FILES',
      'PY': 'CODE_FILES',
      'HTML': 'CODE_FILES',
      'CSS': 'CODE_FILES',
      'XML': 'CODE_FILES',
      'JSON': 'CODE_FILES',
      'YAML': 'CODE_FILES',
      'SQL': 'CODE_FILES'
    };
    
    const category = categoryMap[extension] || 'OTHER';
    this.addResourceForm.patchValue({ category: category });
  }

  setUploadMode(mode: 'url' | 'file'): void {
    this.uploadMode = mode;
    this.addResourceForm.patchValue({ uploadMode: mode });
    
    if (mode === 'file') {
      this.addResourceForm.get('resourceUrl')?.clearValidators();
    } else {
      this.addResourceForm.get('resourceUrl')?.setValidators([Validators.required]);
    }
    this.addResourceForm.get('resourceUrl')?.updateValueAndValidity();
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  // File viewing methods
  viewFile(resource: TeamResource): void {
    if (resource.filePath || resource.fileData) {
      // Download file and open in new tab for viewing
      this.teamService.downloadFile(resource.id).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          window.open(url, '_blank');
          // Clean up the object URL after a delay
          setTimeout(() => window.URL.revokeObjectURL(url), 1000);
        },
        error: (err) => {
          console.error('Error viewing file:', err);
          this.toastService.error('Error', `Failed to view "${resource.name}"`);
        }
      });
    } else if (resource.resourceUrl) {
      // Open URL in new tab
      window.open(resource.resourceUrl, '_blank');
    }
  }

  downloadFile(resource: TeamResource): void {
    if (resource.filePath || resource.fileData) {
      this.teamService.downloadFile(resource.id).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = resource.name + (resource.fileExtension || '');
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        },
        error: (err) => {
          console.error('Error downloading file:', err);
          this.toastService.error('Error', `Failed to download "${resource.name}"`);
        }
      });
    } else if (resource.resourceUrl) {
      // For URL resources, open in new tab
      window.open(resource.resourceUrl, '_blank');
    }
  }

  canViewFile(resource: TeamResource): boolean {
    return !!(resource.filePath || resource.resourceUrl || resource.fileData);
  }

  hasPendingRequest(resource: TeamResource): boolean {
    // Managers don't have pending requests since they can access all resources
    if (this.isManagerOrAdmin()) {
      return false;
    }
    // Check if there's a pending access request for this resource by the current user
    return this.userAccessRequests.some(request => 
      request.resourceId === resource.id && 
      request.status === 'PENDING'
    );
  }

  hasApprovedAccess(resource: TeamResource): boolean {
    // Managers and team leads have approved access to all resources
    if (this.isManagerOrAdmin()) {
      return true;
    }
    // Check if there's an approved access request for this resource by the current user
    return this.userAccessRequests.some(request => 
      request.resourceId === resource.id && 
      request.status === 'APPROVED'
    );
  }

  canAccessResource(resource: TeamResource): boolean {
    // Managers, admins, and team leads can access all resources
    if (this.isManagerOrAdmin()) {
      return true;
    }
    // Regular users can access if they have approved access or if it's a common resource
    return resource.accessType === 'COMMON' || this.hasApprovedAccess(resource);
  }

  refreshAccessStatus(): void {
    // Refresh access requests and resources to update status
    this.loadAccessRequests();
    this.loadUserAccessRequests();
    this.loadCategorizedResources();
  }

  getFileIcon(resource: TeamResource): string {
    if (resource.filePath) {
      const extension = resource.fileExtension?.toLowerCase() || '';
      if (['.pdf'].includes(extension)) return 'pi pi-file-pdf';
      if (['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.svg', '.webp'].includes(extension)) return 'pi pi-image';
      if (['.mp4', '.avi', '.mov', '.wmv', '.webm'].includes(extension)) return 'pi pi-video';
      if (['.mp3', '.wav', '.flac', '.aac'].includes(extension)) return 'pi pi-volume-up';
      if (['.zip', '.rar', '.7z'].includes(extension)) return 'pi pi-box';
      if (['.java', '.js', '.py', '.html', '.css', '.xml', '.json', '.sql'].includes(extension)) return 'pi pi-code';
      if (['.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.txt'].includes(extension)) return 'pi pi-file-edit';
      return 'pi pi-file';
    }
    if (resource.resourceUrl) {
      if (resource.resourceUrl.includes('github.com')) return 'pi pi-github';
      if (resource.resourceUrl.includes('gitlab.com')) return 'pi pi-code';
      if (resource.resourceUrl.includes('youtube.com') || resource.resourceUrl.includes('youtu.be')) return 'pi pi-video';
      return 'pi pi-link';
    }
    return 'pi pi-file';
  }

  getAuditAction(status: string): string {
    switch (status) {
      case 'APPROVED':
        return 'was granted';
      case 'REJECTED':
        return 'was denied';
      case 'PENDING':
        return 'requested';
      default:
        return 'requested';
    }
  }
  
  // Audit log methods
  initializeAuditLog(): void {
    this.filteredAuditRequests = [...this.accessRequests];
    this.applySorting();
    this.updatePagination();
  }
  
  applyAuditFilters(): void {
    this.filteredAuditRequests = this.accessRequests.filter(request => {
      const userMatch = !this.auditFilters.user || 
        (request.userName || request.requestedBy || '').toLowerCase().includes(this.auditFilters.user.toLowerCase());
      
      const statusMatch = !this.auditFilters.status || request.status === this.auditFilters.status;
      
      const resourceMatch = !this.auditFilters.resource || 
        (request.resourceName || '').toLowerCase().includes(this.auditFilters.resource.toLowerCase());
      
      let dateMatch = true;
      if (this.auditFilters.dateFrom || this.auditFilters.dateTo) {
        const requestDate = new Date(request.requestedAt);
        if (this.auditFilters.dateFrom) {
          dateMatch = dateMatch && requestDate >= new Date(this.auditFilters.dateFrom);
        }
        if (this.auditFilters.dateTo) {
          const toDate = new Date(this.auditFilters.dateTo);
          toDate.setHours(23, 59, 59, 999);
          dateMatch = dateMatch && requestDate <= toDate;
        }
      }
      
      return userMatch && statusMatch && resourceMatch && dateMatch;
    });
    
    this.auditPagination.currentPage = 1;
    this.applySorting();
    this.updatePagination();
  }
  
  applySorting(): void {
    this.filteredAuditRequests.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (this.auditSort.field) {
        case 'requestedAt':
          aValue = new Date(a.requestedAt);
          bValue = new Date(b.requestedAt);
          break;
        case 'userName':
          aValue = (a.userName || a.requestedBy || '').toLowerCase();
          bValue = (b.userName || b.requestedBy || '').toLowerCase();
          break;
        case 'resourceName':
          aValue = (a.resourceName || '').toLowerCase();
          bValue = (b.resourceName || '').toLowerCase();
          break;
        case 'status':
          aValue = a.status;
          bValue = b.status;
          break;
        default:
          return 0;
      }
      
      if (aValue < bValue) return this.auditSort.direction === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.auditSort.direction === 'asc' ? 1 : -1;
      return 0;
    });
    
    this.updatePagination();
  }
  
  updatePagination(): void {
    this.auditPagination.totalPages = Math.ceil(this.filteredAuditRequests.length / this.auditPagination.pageSize);
    const startIndex = (this.auditPagination.currentPage - 1) * this.auditPagination.pageSize;
    const endIndex = startIndex + this.auditPagination.pageSize;
    this.paginatedAuditRequests = this.filteredAuditRequests.slice(startIndex, endIndex);
  }
  
  clearAuditFilters(): void {
    this.auditFilters = {
      user: '',
      status: '',
      resource: '',
      dateFrom: '',
      dateTo: ''
    };
    this.applyAuditFilters();
  }
  
  toggleSortDirection(): void {
    this.auditSort.direction = this.auditSort.direction === 'asc' ? 'desc' : 'asc';
    this.applySorting();
  }
  
  changePageSize(): void {
    this.auditPagination.currentPage = 1;
    this.updatePagination();
  }
  
  goToPage(page: number): void {
    if (page >= 1 && page <= this.auditPagination.totalPages) {
      this.auditPagination.currentPage = page;
      this.updatePagination();
    }
  }
  
  goToFirstPage(): void {
    this.goToPage(1);
  }
  
  goToLastPage(): void {
    this.goToPage(this.auditPagination.totalPages);
  }
  
  goToPreviousPage(): void {
    this.goToPage(this.auditPagination.currentPage - 1);
  }
  
  goToNextPage(): void {
    this.goToPage(this.auditPagination.currentPage + 1);
  }
  
  jumpToPageNumber(): void {
    if (this.jumpToPage) {
      this.goToPage(this.jumpToPage);
    }
  }
  
  getTotalPages(): number {
    return this.auditPagination.totalPages;
  }
  
  getPaginationStart(): number {
    return (this.auditPagination.currentPage - 1) * this.auditPagination.pageSize + 1;
  }
  
  getPaginationEnd(): number {
    return Math.min(this.auditPagination.currentPage * this.auditPagination.pageSize, this.filteredAuditRequests.length);
  }
  
  getVisiblePages(): number[] {
    const totalPages = this.auditPagination.totalPages;
    const currentPage = this.auditPagination.currentPage;
    const pages: number[] = [];
    
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 4) {
        for (let i = 1; i <= 5; i++) pages.push(i);
        pages.push(-1); // ellipsis
        pages.push(totalPages);
      } else if (currentPage >= totalPages - 3) {
        pages.push(1);
        pages.push(-1); // ellipsis
        for (let i = totalPages - 4; i <= totalPages; i++) pages.push(i);
      } else {
        pages.push(1);
        pages.push(-1); // ellipsis
        for (let i = currentPage - 1; i <= currentPage + 1; i++) pages.push(i);
        pages.push(-1); // ellipsis
        pages.push(totalPages);
      }
    }
    
    return pages;
  }
  
  getUniqueUsers(): string[] {
    const users = this.accessRequests.map(r => r.userName || r.requestedBy || '').filter(u => u);
    return [...new Set(users)].sort();
  }
  
  getUniqueResources(): string[] {
    const resources = this.accessRequests.map(r => r.resourceName || '').filter(r => r);
    return [...new Set(resources)].sort();
  }
  
  getStatusCount(status: string): number {
    return this.filteredAuditRequests.filter(r => r.status === status).length;
  }
  
  getPendingRequestsCount(): number {
    return this.accessRequests.filter(r => r.status === 'PENDING').length;
  }
  
  getApprovedRequestsCount(): number {
    return this.accessRequests.filter(r => r.status === 'APPROVED').length;
  }
  
  getRejectedRequestsCount(): number {
    return this.accessRequests.filter(r => r.status === 'REJECTED').length;
  }
  
  getCommonResourcePercentage(): string {
    const total = this.resources.length;
    if (total === 0) return '0%';
    return Math.round((this.commonResources.length / total) * 100) + '%';
  }
  
  getManagerResourcePercentage(): string {
    const total = this.resources.length;
    if (total === 0) return '0%';
    return Math.round((this.managerResources.length / total) * 100) + '%';
  }
  
  getResourceChartGradient(): string {
    const commonPercent = this.commonResources.length / (this.resources.length || 1) * 100;
    return `conic-gradient(#3b82f6 0% ${commonPercent}%, #f59e0b ${commonPercent}% 100%)`;
  }
  
  getActivityHeatmapData(): any[] {
    const days = [];
    const today = new Date();
    
    for (let i = 29; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      
      const dayRequests = this.accessRequests.filter(req => {
        const reqDate = new Date(req.requestedAt);
        return reqDate.toDateString() === date.toDateString();
      }).length;
      
      let level = 0;
      if (dayRequests > 0) level = 1;
      if (dayRequests > 2) level = 2;
      if (dayRequests > 5) level = 3;
      
      days.push({
        date: date.toLocaleDateString(),
        count: dayRequests,
        level: level
      });
    }
    
    return days;
  }
  
  getApprovalRate(): number {
    const totalRequests = this.accessRequests.length;
    if (totalRequests === 0) return 0;
    const approvedRequests = this.accessRequests.filter(r => r.status === 'APPROVED').length;
    return Math.round((approvedRequests / totalRequests) * 100);
  }
  
  getAvgResponseTime(): number {
    const processedRequests = this.accessRequests.filter(r => r.status !== 'PENDING');
    if (processedRequests.length === 0) return 0;
    // Mock calculation - in real app would calculate actual response time
    return Math.round(Math.random() * 24 + 1); // 1-24 hours
  }
  
  getActiveMembers(): number {
    // Mock calculation - members who have made requests in last 30 days
    const recentUsers = new Set(this.accessRequests.map(r => r.userName || r.requestedBy));
    return recentUsers.size;
  }
  
  getUserApprovedCount(): number {
    return this.userAccessRequests.filter(r => r.status === 'APPROVED').length;
  }
  
  getUserPendingCount(): number {
    return this.userAccessRequests.filter(r => r.status === 'PENDING').length;
  }
  
  hasActiveFilters(): boolean {
    return !!(this.auditFilters.user || this.auditFilters.status || this.auditFilters.resource || 
             this.auditFilters.dateFrom || this.auditFilters.dateTo);
  }
  
  trackByAuditId(index: number, item: AccessRequest): number {
    return item.id;
  }
  
  viewAuditDetails(request: AccessRequest): void {
    // Implementation for viewing detailed audit information
    console.log('Viewing audit details for request:', request);
  }
  
  exportAuditLog(): void {
    try {
      const csvContent = this.generateAuditCSV();
      const blob = new Blob([csvContent], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `audit-log-team-${this.teamId}-${new Date().toISOString().split('T')[0]}.csv`;
      link.click();
      window.URL.revokeObjectURL(url);
      this.toastService.success('Success', 'Audit log exported successfully');
    } catch (error) {
      this.toastService.error('Error', 'Failed to export audit log');
    }
  }
  
  private generateAuditCSV(): string {
    const headers = ['ID', 'Date', 'User', 'Action', 'Resource', 'Status', 'Access Level', 'Justification'];
    const rows = this.filteredAuditRequests.map(request => [
      request.id,
      new Date(request.requestedAt).toLocaleString(),
      request.userName || request.requestedBy || '',
      this.getAuditAction(request.status),
      request.resourceName || '',
      request.status,
      request.requestedAccessLevel || 'read',
      (request.justification || '').replace(/"/g, '""')
    ]);
    
    return [headers, ...rows].map(row => 
      row.map(cell => `"${cell}"`).join(',')
    ).join('\n');
  }

  getGroupsArray(allowedUserGroups: string): string[] {
    return allowedUserGroups ? allowedUserGroups.split(',').map(g => g.trim()).filter(g => g) : [];
  }
  
  getTabTitle(): string {
    switch (this.activeTab) {
      case 'overview': return 'Team Overview';
      case 'members': return 'Team Members';
      case 'resources': return 'Team Resources';
      case 'common-resources': return 'Common Resources';
      case 'manager-resources': return 'Manager Controlled Resources';
      case 'requests': return 'Access Requests';
      case 'uploads': return 'My Uploads';
      case 'create-project': return 'Create New Project';
      case 'audit': return 'Audit Log';
      case 'profile-settings': return 'Profile Settings';
      case 'shared-documents': return 'Shared Documents';
      default: return 'Team Details';
    }
  }
  
  navigateToCommonResources(): void {
    console.log('*** COMMON RESOURCES CLICKED *** Navigating to common resources for team:', this.teamId);
    window.location.href = `/team/${this.teamId}/resources/common`;
  }
  
  navigateToManagerResources(): void {
    console.log('*** MANAGER RESOURCES CLICKED *** Navigating to manager resources for team:', this.teamId);
    window.location.href = `/team/${this.teamId}/resources/manager`;
  }
  
  // Edit Access Methods
  openEditAccessModal(resource: TeamResource): void {
    this.selectedResource = resource;
    this.showEditAccessModal = true;
    this.selectedUserGroups = resource.allowedUserGroups ? 
      resource.allowedUserGroups.split(',').map(g => g.trim()).filter(g => g) : [];
    this.editAccessForm.patchValue({
      accessType: resource.accessType || 'COMMON',
      allowedUserGroups: resource.allowedUserGroups || ''
    });
  }
  
  closeEditAccessModal(): void {
    this.showEditAccessModal = false;
    this.selectedResource = null;
    this.selectedUserGroups = [];
    this.editAccessForm.reset({
      accessType: 'COMMON',
      allowedUserGroups: ''
    });
  }
  
  toggleUserGroup(group: string): void {
    const index = this.selectedUserGroups.indexOf(group);
    if (index > -1) {
      this.selectedUserGroups.splice(index, 1);
    } else {
      this.selectedUserGroups.push(group);
    }
  }
  
  isGroupSelected(group: string): boolean {
    return this.selectedUserGroups.includes(group);
  }
  
  revokeUserAccess(userId: number): void {
    if (!this.selectedResource) return;
    
    const user = this.resourceAccess.find(access => access.userId === userId);
    this.selectedUserForRevoke = {
      userId: userId,
      resourceId: this.selectedResource.id,
      username: user?.username || 'Unknown User'
    };
    this.showRevokeAccessModal = true;
  }
  
  confirmRevokeAccess(): void {
    if (!this.selectedUserForRevoke) return;
    
    this.teamService.revokeUserAccess(this.selectedUserForRevoke.userId, this.selectedUserForRevoke.resourceId).subscribe({
      next: () => {
        // Remove user from local resourceAccess array
        this.resourceAccess = this.resourceAccess.filter(access => access.userId !== this.selectedUserForRevoke!.userId);
        
        this.toastService.success('Success', `Access revoked for ${this.selectedUserForRevoke!.username}`);
        this.refreshRecentActivity();
        this.loadUserUploads();
        this.closeRevokeAccessModal();
      },
      error: (err) => {
        const errorMessage = err.error?.message || 'Failed to revoke access';
        this.toastService.error('Error', errorMessage);
      }
    });
  }
  
  closeRevokeAccessModal(): void {
    this.showRevokeAccessModal = false;
    this.selectedUserForRevoke = null;
  }
  
  refreshRecentActivity(): void {
    this.loadAccessRequests();
    this.loadUserAccessRequests();
    this.loadNotifications();
  }
  
  onUpdateAccessSettings(): void {
    if (this.editAccessForm.invalid || !this.selectedResource) {
      this.toastService.error('Error', 'Please fill in all required fields');
      return;
    }
    
    const accessSettings = {
      accessType: this.editAccessForm.get('accessType')?.value,
      allowedUserGroups: this.selectedUserGroups.join(',')
    };
    
    this.toastService.info('Processing', 'Updating access settings...');
    
    this.teamService.updateResourceAccessSettings(this.selectedResource.id, accessSettings).subscribe({
      next: (updatedResource) => {
        const groupsText = this.selectedUserGroups.length > 0 ? ` Groups: ${this.selectedUserGroups.join(', ')}` : '';
        this.toastService.success('Success', `Access settings updated!${groupsText}`);
        this.closeEditAccessModal();
        this.loadUserUploads();
        this.loadResources();
        this.loadResourceAccess(this.selectedResource!.id);
      },
      error: (err) => {
        const errorMessage = err.error?.message || 'Failed to update access settings';
        this.toastService.error('Error', errorMessage);
      }
    });
  }
  
  // Team Chat Methods
  toggleTeamChat(): void {
    this.showTeamChat = !this.showTeamChat;
    if (this.showTeamChat && this.chatMessages.length === 0) {
      this.loadChatMessages();
    }
  }
  
  loadChatMessages(): void {
    this.teamService.getTeamChatMessages(this.teamId).subscribe({
      next: (messages) => {
        this.chatMessages = messages.map(msg => ({
          ...msg,
          isCurrentUser: msg.sender === this.currentUsername
        }));
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Error loading chat messages:', err);
        // Fallback to mock messages
        this.chatMessages = [
          {
            id: 1,
            teamId: this.teamId,
            sender: 'priya.manager',
            message: 'Welcome to the team chat! Feel free to discuss project updates here.',
            timestamp: new Date(Date.now() - 3600000).toISOString(),
            isCurrentUser: false
          }
        ];
      }
    });
  }
  
  sendMessage(): void {
    if (!this.newMessage.trim()) return;
    
    const messageText = this.newMessage.trim();
    this.newMessage = '';
    
    this.teamService.sendChatMessage(this.teamId, messageText).subscribe({
      next: (sentMessage) => {
        sentMessage.isCurrentUser = true;
        this.chatMessages.push(sentMessage);
        this.scrollToBottom();
        this.toastService.success('Success', 'Message sent successfully');
      },
      error: (err) => {
        console.error('Error sending message:', err);
        this.toastService.error('Error', 'Failed to send message');
        this.newMessage = messageText; // Restore message on error
      }
    });
  }
  
  private scrollToBottom(): void {
    Promise.resolve().then(() => {
      const chatContainer = document.querySelector('.chat-messages');
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    });
  }
  
  onChatKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
  
  loadAllProjects(): void {
    this.teamService.getTeams().subscribe({
      next: (teams) => {
        this.allProjects = teams || [];
        console.log('All projects loaded:', this.allProjects);
        console.log('Current team:', this.team);
        console.log('Team ID match check:', this.allProjects.find(p => p.id === this.team?.id));
      },
      error: (err) => {
        console.error('Error loading all projects:', err);
        this.allProjects = [];
      }
    });
  }
  
  toggleProjectSwitcher(): void {
    this.showProjectSwitcher = !this.showProjectSwitcher;
  }

  closeProjectSwitcher(): void {
    this.showProjectSwitcher = false;
  }

  selectProject(projectId: number): void {
    if (projectId !== this.teamId) {
      const selectedProject = this.allProjects.find(p => p.id === projectId);
      
      if (selectedProject?.status === 'INACTIVE' && (this.currentUsername.includes('.dev') || this.currentUsername.includes('.test'))) {
        this.toastService.error('Access Denied', 'This project is currently inactive and cannot be accessed.');
        return;
      }
      
      this.closeProjectSwitcher();
      this.loadingService.show();
      this.toastService.success('Success', `Switching to project: ${selectedProject?.name || 'Unknown'}`);
      setTimeout(() => {
        this.loadingService.hide();
        this.router.navigate(['/team', projectId]);
      }, 1500);
    }
  }
  
  logout(): void {
    this.toastService.info('Processing', 'Logging out...');
    setTimeout(() => {
      this.teamService.logout();
      this.toastService.success('Success', 'Logged out successfully. Goodbye!');
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 500);
    }, 500);
  }
  
  updateTeamStatus(teamId: number, event: any): void {
    const newStatus = event.target.value;
    this.teamService.updateTeamStatus(teamId, newStatus).subscribe({
      next: () => {
        if (this.team) {
          this.team.status = newStatus;
        }
        this.toastService.success('Success', `Project status updated to ${newStatus}`);
      },
      error: (err) => {
        console.error('Error updating team status:', err);
        this.toastService.error('Error', 'Failed to update project status');
      }
    });
  }
  
  getCurrentUserEmail(): string {
    return `${this.currentUsername}@company.com`;
  }
  
  getCurrentUserRole(): string {
    return this.teamService.getCurrentUserRole() || 'TEAM_MEMBER';
  }
  
  onImageError(event: any): void {
    event.target.style.display = 'none';
  }
  
  getUserAvatar(): string {
    return `https://api.dicebear.com/7.x/initials/svg?seed=${this.currentUsername}&backgroundColor=1976d2,2196f3,4caf50,ff9800,9c27b0&textColor=ffffff`;
  }
  
  showRequestDetailsModal = false;
  selectedRequestDetails: any = null;
  showCreateProjectModal = false;
  createProjectForm!: FormGroup;
  availableManagers: any[] = [];
  filteredManagers: any[] = [];
  selectedManager: any = null;
  availableTeamLeads: any[] = [];
  managerSearchTerm = '';
  showManagerDropdown = false;
  allEmployees: any[] = [];
  filteredEmployees: any[] = [];
  selectedEmployeeIds: number[] = [];
  memberSearchTerm = '';
  memberRoleFilter = '';
  showEmployeeDropdown = false;
  private searchSubject = new Subject<string>();
  isSearchingEmployees = false;
  searchNoResults = false;
  
  // Create User Modal
  showCreateUserModal = false;
  createUserForm!: FormGroup;
  
  // Profile Settings
  profileSettingsForm!: FormGroup;
  
  // Shared Documents
  sharedDocuments: any[] = [];
  showShareDocumentModal = false;
  shareDocumentForm!: FormGroup;
  shareMode: 'url' | 'file' = 'url';
  selectedDocumentFile: File | null = null;
  
  // Remove User Modal
  showRemoveUserModal = false;
  selectedUserToRemove: { id: number, username: string } | null = null;
  
  // Revoke Access Modal
  showRevokeAccessModal = false;
  selectedUserForRevoke: { userId: number, resourceId: number, username: string } | null = null;
  
  // Delete Resource Modal
  showDeleteResourceModal = false;
  selectedResourceToDelete: TeamResource | null = null;
  deleteResourceForm!: FormGroup;
  
  private loadingService = inject(LoadingService);
  
  // Add User to Project Modal
  showAddUserToProjectModal = false;
  availableUsers: any[] = [];
  filteredUsers: any[] = [];
  searchTerm = '';
  selectedUser: any = null;
  
  showRequestDetails(request: any): void {
    this.selectedRequestDetails = request;
    this.showRequestDetailsModal = true;
  }
  
  closeRequestDetailsModal(): void {
    this.showRequestDetailsModal = false;
    this.selectedRequestDetails = null;
  }
  
  openCreateProjectModal(): void {
    this.initializeCreateProjectForm();
    this.loadAvailableManagers();
    this.loadAvailableTeamLeads();
    this.loadAllEmployees();
    this.showCreateProjectModal = true;
  }
  
  closeCreateProjectModal(): void {
    this.showCreateProjectModal = false;
    this.selectedEmployeeIds = [];
    this.memberSearchTerm = '';
    this.showEmployeeDropdown = false;
    this.isSearchingEmployees = false;
    this.searchNoResults = false;
    this.createProjectForm.reset();
  }
  
  initializeCreateProjectForm(): void {
    this.createProjectForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      status: ['ACTIVE', Validators.required],
      managerId: ['', Validators.required],
      teamLeadId: ['']
    });
  }
  
  loadAvailableManagers(): void {
    this.teamService.getAllManagers().subscribe({
      next: (managers) => {
        console.log('Loaded managers:', managers);
        this.availableManagers = managers || [];
        this.filteredManagers = [...this.availableManagers];
        // Fallback if no managers loaded
        if (this.availableManagers.length === 0) {
          this.availableManagers = [
            { id: 2, username: 'priya.manager', email: 'priya@company.com', role: 'PROJECT_MANAGER' },
            { id: 3, username: 'james.manager', email: 'james@company.com', role: 'PROJECT_MANAGER' }
          ];
          this.filteredManagers = [...this.availableManagers];
        }
      },
      error: (err) => {
        console.error('Error loading managers:', err);
        // Fallback managers
        this.availableManagers = [
          { id: 2, username: 'priya.manager', email: 'priya@company.com', role: 'PROJECT_MANAGER' },
          { id: 3, username: 'james.manager', email: 'james@company.com', role: 'PROJECT_MANAGER' }
        ];
        this.filteredManagers = [...this.availableManagers];
      }
    });
  }
  
  loadAvailableTeamLeads(): void {
    this.teamService.getAllTeamLeads().subscribe({
      next: (teamLeads) => {
        this.availableTeamLeads = teamLeads || [];
      },
      error: (err) => {
        console.error('Error loading team leads:', err);
        this.availableTeamLeads = [];
      }
    });
  }
  
  loadAllEmployees(): void {
    this.teamService.getAllEmployees().subscribe({
      next: (employees) => {
        console.log('Loaded employees:', employees);
        this.allEmployees = employees || [];
        this.filteredEmployees = [...this.allEmployees];
      },
      error: (err) => {
        console.error('Error loading employees:', err);
        // Fallback to mock data if API fails
        this.allEmployees = [
          { id: 1, username: 'rajesh.admin', email: 'rajesh@company.com', role: 'ADMIN' },
          { id: 2, username: 'priya.manager', email: 'priya@company.com', role: 'PROJECT_MANAGER' },
          { id: 6, username: 'arjun.dev', email: 'arjun@company.com', role: 'TEAM_MEMBER' },
          { id: 7, username: 'emily.dev', email: 'emily@company.com', role: 'TEAM_MEMBER' },
          { id: 9, username: 'sophia.test', email: 'sophia@company.com', role: 'TEAM_MEMBER' }
        ];
        this.filteredEmployees = [...this.allEmployees];
      }
    });
  }
  
  filterMembers(): void {
    this.showEmployeeDropdown = true;
    this.isSearchingEmployees = true;
    
    if (this.memberSearchTerm.trim()) {
      // Use API search for real-time results
      this.teamService.searchUsers(this.memberSearchTerm.trim()).subscribe({
        next: (searchResults) => {
          console.log('Search API results:', searchResults);
          let employees = searchResults || [];
          
          // Apply role filter
          if (this.memberRoleFilter) {
            employees = employees.filter(emp => emp.role === this.memberRoleFilter);
          }
          
          // Exclude already selected employees and selected manager
          this.filteredEmployees = employees.filter(employee => 
            !this.isEmployeeSelected(employee.id) && 
            employee.id !== this.selectedManager?.id
          );
          
          this.searchNoResults = this.filteredEmployees.length === 0;
          this.isSearchingEmployees = false;
        },
        error: (err) => {
          console.error('Search API error:', err);
          this.filteredEmployees = [];
          this.searchNoResults = true;
          this.isSearchingEmployees = false;
        }
      });
    } else {
      // Use cached employees for empty search
      let employees = [...this.allEmployees];
      
      // Apply role filter
      if (this.memberRoleFilter) {
        employees = employees.filter(emp => emp.role === this.memberRoleFilter);
      }
      
      // Exclude already selected employees and selected manager
      this.filteredEmployees = employees.filter(employee => 
        !this.isEmployeeSelected(employee.id) && 
        employee.id !== this.selectedManager?.id
      );
      
      this.searchNoResults = this.filteredEmployees.length === 0;
      this.isSearchingEmployees = false;
    }
  }
  
  isEmployeeSelected(employeeId: number): boolean {
    return this.selectedEmployeeIds.includes(employeeId);
  }
  
  selectEmployee(employee: any): void {
    if (!this.isEmployeeSelected(employee.id)) {
      this.selectedEmployeeIds.push(employee.id);
    }
    this.memberSearchTerm = '';
    this.showEmployeeDropdown = false;
    this.isSearchingEmployees = false;
    this.searchNoResults = false;
    this.filteredEmployees = [...this.allEmployees];
  }


  

  
  removeEmployee(employeeId: number): void {
    const index = this.selectedEmployeeIds.indexOf(employeeId);
    if (index > -1) {
      this.selectedEmployeeIds.splice(index, 1);
    }
  }
  
  getEmployeeName(employeeId: number): string {
    const employee = this.allEmployees.find(emp => emp.id === employeeId);
    return employee ? employee.username : 'Unknown';
  }
  
  onCreateProject(): void {
    console.log('Form valid:', this.createProjectForm.valid);
    console.log('Form errors:', this.createProjectForm.errors);
    console.log('Form values:', this.createProjectForm.value);
    
    if (this.createProjectForm.valid) {
      const projectData = {
        name: this.createProjectForm.get('name')?.value,
        description: this.createProjectForm.get('description')?.value,
        status: this.createProjectForm.get('status')?.value,
        managerId: parseInt(this.createProjectForm.get('managerId')?.value),
        teamLeadId: this.createProjectForm.get('teamLeadId')?.value ? parseInt(this.createProjectForm.get('teamLeadId')?.value) : null,
        memberIds: this.selectedEmployeeIds
      };
      
      console.log('Creating project with data:', projectData);
      
      this.teamService.createProject(projectData).subscribe({
        next: (response) => {
          console.log('Project created successfully:', response);
          this.toastService.success('Success', `Project "${projectData.name}" created successfully! Manager and ${this.selectedEmployeeIds.length} members assigned.`);
          this.closeCreateProjectModal();
          this.resetForm();
          this.loadAllProjects();
          // Navigate to the new project if created successfully
          if (response && response.id) {
            setTimeout(() => {
              this.router.navigate(['/team', response.id]);
            }, 1500);
          }
        },
        error: (err) => {
          console.error('Error creating project:', err);
          this.toastService.error('Error', err.error?.message || 'Failed to create project');
        }
      });
    } else {
      console.log('Form is invalid');
      Object.keys(this.createProjectForm.controls).forEach(key => {
        const control = this.createProjectForm.get(key);
        if (control && control.invalid) {
          console.log(`${key} is invalid:`, control.errors);
        }
      });
      this.toastService.error('Error', 'Please fill in all required fields');
    }
  }
  
  toggleSidebar(): void {
    this.sidebarHidden = !this.sidebarHidden;
  }
  
  navigateToCreateProject(): void {
    this.router.navigate(['/create-project']);
  }
  
  // Manager Selection Methods
  filterManagers(): void {
    this.showManagerDropdown = true;
    if (!this.managerSearchTerm.trim()) {
      this.filteredManagers = [...this.availableManagers];
    } else {
      const searchTerm = this.managerSearchTerm.toLowerCase();
      this.filteredManagers = this.availableManagers.filter(manager => 
        manager.username.toLowerCase().includes(searchTerm) ||
        manager.email.toLowerCase().includes(searchTerm)
      );
    }
  }
  
  toggleManagerDropdown(): void {
    this.showManagerDropdown = !this.showManagerDropdown;
  }
  
  selectManager(manager: any): void {
    this.selectedManager = manager;
    this.createProjectForm.patchValue({ managerId: manager.id });
    this.managerSearchTerm = manager.username;
    this.showManagerDropdown = false;
  }
  
  removeManager(): void {
    this.selectedManager = null;
    this.createProjectForm.patchValue({ managerId: '' });
    this.managerSearchTerm = '';
  }
  
  // Enhanced Member Selection Methods
  setMemberRoleFilter(role: string): void {
    this.memberRoleFilter = role;
    this.filterMembers();
  }
  
  toggleEmployeeDropdown(): void {
    this.showEmployeeDropdown = !this.showEmployeeDropdown;
    if (this.showEmployeeDropdown) {
      this.filterMembers();
    }
  }
  
  toggleEmployeeSelection(employee: any): void {
    if (this.isEmployeeSelected(employee.id)) {
      this.removeEmployee(employee.id);
    } else {
      this.selectEmployee(employee);
    }
  }
  
  getEmployeeById(id: number): any {
    return this.allEmployees.find(emp => emp.id === id);
  }
  
  clearAllMembers(): void {
    this.selectedEmployeeIds = [];
  }
  
  resetForm(): void {
    this.createProjectForm.reset({
      name: '',
      description: '',
      status: 'ACTIVE',
      managerId: ''
    });
    this.selectedManager = null;
    this.selectedEmployeeIds = [];
    this.managerSearchTerm = '';
    this.memberSearchTerm = '';
    this.memberRoleFilter = '';
  }
  
  // Create User Methods
  openCreateUserModal(): void {
    this.showCreateUserModal = true;
    this.createUserForm.reset();
  }
  
  closeCreateUserModal(): void {
    this.showCreateUserModal = false;
    this.createUserForm.reset();
  }
  
  onCreateUser(): void {
    if (this.createUserForm.invalid) {
      return;
    }
    
    const userData = {
      username: this.createUserForm.get('username')?.value,
      email: this.createUserForm.get('email')?.value,
      fullName: this.createUserForm.get('fullName')?.value,
      role: this.createUserForm.get('role')?.value
    };
    
    this.teamService.createUser(userData).subscribe({
      next: (response) => {
        this.toastService.success('Success', `User ${userData.username} created successfully!`);
        this.closeCreateUserModal();
        this.loadMembers();
        this.loadAllEmployees();
      },
      error: (err) => {
        const errorMessage = err.error?.message || 'Failed to create user';
        this.toastService.error('Error', errorMessage);
      }
    });
  }
  
  // Add User to Project Methods
  openAddUserToProjectModal(): void {
    this.showAddUserToProjectModal = true;
    this.loadAvailableUsers();
    this.searchTerm = '';
    this.selectedUser = null;
  }
  
  closeAddUserToProjectModal(): void {
    this.showAddUserToProjectModal = false;
    this.searchTerm = '';
    this.selectedUser = null;
    this.filteredUsers = [];
  }
  
  loadAvailableUsers(): void {
    const currentMemberIds = this.members.map(m => m.id);
    
    this.teamService.getAllEmployees().subscribe({
      next: (allUsers) => {
        console.log('Fresh user data from API:', allUsers);
        this.availableUsers = (allUsers || []).filter(user => !currentMemberIds.includes(user.id));
        this.filteredUsers = [...this.availableUsers];
        console.log('Available users for selection:', this.availableUsers);
      },
      error: (err) => {
        console.error('Error loading users from API:', err);
        this.availableUsers = [];
        this.filteredUsers = [];
      }
    });
  }
  
  onSearchUsers(): void {
    if (!this.searchTerm.trim()) {
      this.filteredUsers = [...this.availableUsers];
      return;
    }
    
    console.log('Searching for:', this.searchTerm);
    this.teamService.searchUsers(this.searchTerm.trim()).subscribe({
      next: (searchResults) => {
        console.log('Search results from API:', searchResults);
        const currentMemberIds = this.members.map(m => m.id);
        this.filteredUsers = (searchResults || []).filter(user => !currentMemberIds.includes(user.id));
        console.log('Filtered search results:', this.filteredUsers);
      },
      error: (err) => {
        console.error('Search error:', err);
        this.filteredUsers = [];
      }
    });
  }
  
  selectUser(user: any): void {
    this.selectedUser = user;
    this.searchTerm = user.fullName;
  }
  
  addUserToProject(): void {
    if (!this.selectedUser) {
      this.toastService.error('Error', 'Please select a user to add');
      return;
    }
    
    this.toastService.info('Processing', `Adding ${this.selectedUser.fullName} to project...`);
    
    this.teamService.addUserToProject(this.selectedUser.id, this.teamId).subscribe({
      next: () => {
        this.toastService.success('Success', `${this.selectedUser.fullName} added to project successfully!`);
        
        const newMember = {
          id: this.selectedUser.id,
          username: this.selectedUser.username,
          email: this.selectedUser.email,
          role: this.selectedUser.role,
          fullName: this.selectedUser.fullName
        };
        
        if (newMember.role === 'PROJECT_MANAGER') {
          this.manager = newMember;
        } else {
          this.teamMembers.push(newMember);
        }
        this.members.push(newMember);
        
        this.closeAddUserToProjectModal();
      },
      error: (err) => {
        const errorMessage = err.error?.message || 'Failed to add user to project';
        this.toastService.error('Error', errorMessage);
      }
    });
  }
  
  // User Profile Dropdown Methods
  toggleUserDropdown(): void {
    this.showUserDropdown = !this.showUserDropdown;
  }
  
  openProfileSettings(): void {
    this.showUserDropdown = false;
    this.setActiveTab('profile-settings');
    this.initializeProfileForm();
  }
  
  initializeProfileForm(): void {
    this.profileSettingsForm.patchValue({
      username: this.currentUsername,
      email: this.getCurrentUserEmail(),
      fullName: this.currentUsername.split('.')[0] || this.currentUsername,
      role: this.getCurrentUserRole(),
      timezone: 'UTC',
      language: 'en',
      emailNotifications: true
    });
  }
  
  onUpdateProfile(): void {
    if (this.profileSettingsForm.invalid) {
      return;
    }
    
    const profileData = {
      email: this.profileSettingsForm.get('email')?.value,
      fullName: this.profileSettingsForm.get('fullName')?.value,
      timezone: this.profileSettingsForm.get('timezone')?.value,
      language: this.profileSettingsForm.get('language')?.value,
      emailNotifications: this.profileSettingsForm.get('emailNotifications')?.value
    };
    
    // Simulate API call
    setTimeout(() => {
      this.toastService.success('Success', 'Profile updated successfully!');
      this.profileSettingsForm.markAsPristine();
    }, 500);
  }
  
  resetProfileForm(): void {
    this.initializeProfileForm();
    this.profileSettingsForm.markAsPristine();
  }
  
  // Shared Documents Methods
  openSharedDocuments(): void {
    this.setActiveTab('shared-documents');
    this.loadSharedDocuments();
  }
  
  loadSharedDocuments(): void {
    this.teamService.getSharedDocuments().subscribe({
      next: (documents) => {
        this.sharedDocuments = documents || [];
      },
      error: (err) => {
        console.error('Error loading shared documents:', err);
        this.toastService.error('Error', 'Failed to load shared documents');
        this.sharedDocuments = [];
      }
    });
  }
  
  openShareDocumentModal(): void {
    this.showShareDocumentModal = true;
    this.shareDocumentForm.reset({
      title: '',
      message: '',
      type: 'PDF',
      documentUrl: '',
      shareMode: 'url'
    });
  }
  
  closeShareDocumentModal(): void {
    this.showShareDocumentModal = false;
    this.selectedDocumentFile = null;
    this.shareMode = 'url';
  }
  
  setShareMode(mode: 'url' | 'file'): void {
    this.shareMode = mode;
    this.shareDocumentForm.patchValue({ shareMode: mode });
    
    if (mode === 'file') {
      this.shareDocumentForm.get('documentUrl')?.clearValidators();
    } else {
      this.shareDocumentForm.get('documentUrl')?.setValidators([Validators.required]);
    }
    this.shareDocumentForm.get('documentUrl')?.updateValueAndValidity();
  }
  
  onDocumentFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedDocumentFile = file;
    }
  }
  
  onShareDocument(): void {
    if (this.shareDocumentForm.invalid) {
      this.toastService.error('Error', 'Please fill in all required fields');
      return;
    }
    
    const documentData = {
      title: this.shareDocumentForm.get('title')?.value,
      message: this.shareDocumentForm.get('message')?.value,
      documentType: this.shareDocumentForm.get('type')?.value,
      authorId: this.currentUserId,
      authorName: this.currentUsername,
      authorRole: this.getCurrentUserRole(),
      documentUrl: this.shareMode === 'url' ? this.shareDocumentForm.get('documentUrl')?.value : null
    };
    
    if (this.shareMode === 'file' && this.selectedDocumentFile) {
      this.teamService.shareDocumentWithFile(documentData, this.selectedDocumentFile).subscribe({
        next: (response) => {
          this.toastService.success('Success', `Document "${documentData.title}" shared successfully!`);
          this.closeShareDocumentModal();
          this.loadSharedDocuments();
        },
        error: (err) => {
          this.toastService.error('Error', 'Failed to share document');
        }
      });
    } else {
      this.teamService.shareDocumentWithUrl(documentData).subscribe({
        next: (response) => {
          this.toastService.success('Success', `Document "${documentData.title}" shared successfully!`);
          this.closeShareDocumentModal();
          this.loadSharedDocuments();
        },
        error: (err) => {
          this.toastService.error('Error', 'Failed to share document');
        }
      });
    }
  }
  
  viewSharedDocument(doc: any): void {
    if (doc.documentUrl) {
      window.open(doc.documentUrl, '_blank');
    }
  }
  
  downloadSharedDocument(doc: any): void {
    if (doc.documentUrl) {
      window.open(doc.documentUrl, '_blank');
      this.toastService.success('Success', `Opening "${doc.title}"`);
    } else if (doc.fileData || doc.fileName) {
      this.teamService.downloadSharedDocument(doc.id).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = doc.fileName || doc.title;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
          this.toastService.success('Success', `"${doc.title}" downloaded successfully`);
        },
        error: (err) => {
          this.toastService.error('Error', `Failed to download "${doc.title}"`);
        }
      });
    }
  }
  
  deleteSharedDocument(docId: number): void {
    if (confirm('Are you sure you want to delete this shared document?')) {
      this.teamService.deleteSharedDocument(docId, this.currentUserId).subscribe({
        next: () => {
          this.toastService.success('Success', 'Document deleted successfully');
          this.loadSharedDocuments();
        },
        error: (err) => {
          this.toastService.error('Error', 'Failed to delete document');
        }
      });
    }
  }
  
  getDocumentIcon(type: string): string {
    switch (type) {
      case 'PDF': return 'pi pi-file-pdf';
      case 'DOC': return 'pi pi-file-edit';
      case 'XLS': return 'pi pi-table';
      case 'PPT': return 'pi pi-desktop';
      case 'IMAGE': return 'pi pi-image';
      case 'TXT': return 'pi pi-file';
      default: return 'pi pi-file';
    }
  }
  
  removeUserFromTeam(userId: number, username: string): void {
    this.selectedUserToRemove = { id: userId, username: username };
    this.showRemoveUserModal = true;
  }
  
  confirmRemoveUser(): void {
    if (!this.selectedUserToRemove) {
      this.toastService.error('Error', 'No user selected for removal');
      return;
    }
    
    const username = this.selectedUserToRemove.username;
    this.toastService.info('Processing', `Removing ${username} from team...`);
    
    this.teamService.removeUserFromProject(this.teamId, this.selectedUserToRemove.id).subscribe({
      next: () => {
        this.toastService.success('Success', `${username} removed from team successfully`);
        this.loadTeamData();
        this.closeRemoveUserModal();
      },
      error: (err: any) => {
        const errorMessage = err.error?.message || 'Failed to remove user from team';
        this.toastService.error('Error', errorMessage);
      }
    });
  }
  
  closeRemoveUserModal(): void {
    this.showRemoveUserModal = false;
    this.selectedUserToRemove = null;
  }
  
  // Delete Resource Methods
  openDeleteResourceModal(resource: TeamResource): void {
    this.selectedResourceToDelete = resource;
    this.showDeleteResourceModal = true;
    this.deleteResourceForm.reset();
  }
  
  closeDeleteResourceModal(): void {
    this.showDeleteResourceModal = false;
    this.selectedResourceToDelete = null;
    this.deleteResourceForm.reset();
  }
  
  confirmDeleteResource(): void {
    if (this.deleteResourceForm.invalid || !this.selectedResourceToDelete) {
      this.toastService.error('Error', 'Please provide a valid reason for deletion');
      return;
    }
    
    const deletionReason = this.deleteResourceForm.get('deletionReason')?.value;
    const resourceName = this.selectedResourceToDelete.name;
    
    this.toastService.info('Processing', 'Deleting resource...');
    
    this.teamService.deleteResource(this.selectedResourceToDelete.id, deletionReason).subscribe({
      next: () => {
        this.toastService.success('Success', `Resource "${resourceName}" deleted successfully`);
        this.closeDeleteResourceModal();
        this.loadUserUploads();
        this.loadResources();
      },
      error: (err: any) => {
        this.toastService.error('Error', 'Failed to delete resource. Please try again.');
      }
    });
  }
}