import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService, TeamResource, CreateResourceRequest } from '../../../services/team.service';

@Component({
  selector: 'app-manager-resources',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './manager-resources.component.html',
  styleUrls: ['./manager-resources.component.css', '../../../teamdetails/teamdetails.css']
})
export class ManagerResourcesComponent implements OnInit {
  teamId: number = 0;
  resources: TeamResource[] = [];
  filteredResources: TeamResource[] = [];
  paginatedResources: TeamResource[] = [];
  loading = true;
  
  // Filters and search
  searchTerm = '';
  selectedType = '';
  selectedCategory = '';
  accessFilter = 'all';
  
  // Sorting
  sortField = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';
  
  // Pagination
  currentPage = 1;
  pageSize = 12;
  totalPages = 1;
  
  // Modal
  showAddResourceModal = false;
  showRequestAccessModal = false;
  showResourceAccessModal = false;
  selectedResource: TeamResource | null = null;
  addResourceForm: FormGroup;
  requestAccessForm: FormGroup;
  selectedFile: File | null = null;
  uploadMode: 'url' | 'file' = 'url';
  resourceAccess: any[] = [];
  loadingResourceAccess = false;

  userAccessRequests: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teamService: TeamService,
    private fb: FormBuilder
  ) {
    this.addResourceForm = this.fb.group({
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      type: ['OTHER', [Validators.required]],
      category: ['OTHER', [Validators.required]],
      resourceUrl: [''],
      allowedUserGroups: ['']
    });
    
    this.requestAccessForm = this.fb.group({
      justification: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {
    this.currentUsername = this.teamService.getCurrentUsername();
    this.route.params.subscribe(params => {
      console.log('Route params:', params);
      this.teamId = +params['teamId'];
      console.log('Extracted teamId:', this.teamId);
      this.loadTeam();
      this.loadResources();
      this.loadUserAccessRequests();
      this.loadAllProjects();
    });
  }
  
  loadTeam(): void {
    this.teamService.getTeamById(this.teamId).subscribe({
      next: (team) => {
        this.team = team;
        console.log('Loaded team:', team);
      },
      error: (err) => {
        console.error('Error loading team:', err);
      }
    });
  }

  loadUserAccessRequests(): void {
    this.teamService.getAccessRequests(this.teamId).subscribe({
      next: (requests) => {
        const currentUserId = this.teamService.getCurrentUserId();
        this.userAccessRequests = (requests || []).filter(req => req.userId === currentUserId);
      },
      error: (err) => console.error('Error loading user access requests:', err)
    });
  }

  loadResources(): void {
    this.loading = true;
    this.teamService.getManagerControlledResources(this.teamId).subscribe({
      next: (resources) => {
        this.resources = resources || [];
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading resources:', err);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredResources = this.resources.filter(resource => {
      const matchesSearch = !this.searchTerm || 
        resource.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        resource.description.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesType = !this.selectedType || resource.type === this.selectedType;
      const matchesCategory = !this.selectedCategory || resource.category === this.selectedCategory;
      const matchesAccess = this.matchesAccessFilter(resource);
      
      return matchesSearch && matchesType && matchesCategory && matchesAccess;
    });
    
    this.applySorting();
    this.updatePagination();
  }

  applySorting(): void {
    this.filteredResources.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (this.sortField) {
        case 'name':
          aValue = a.name.toLowerCase();
          bValue = b.name.toLowerCase();
          break;
        case 'type':
          aValue = a.type;
          bValue = b.type;
          break;
        case 'category':
          aValue = a.category || '';
          bValue = b.category || '';
          break;
        default:
          return 0;
      }
      
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredResources.length / this.pageSize);
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedResources = this.filteredResources.slice(startIndex, endIndex);
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applyFilters();
  }

  onSort(field: string): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.applySorting();
    this.updatePagination();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  openAddResourceModal(): void {
    this.showAddResourceModal = true;
  }

  closeAddResourceModal(): void {
    this.showAddResourceModal = false;
    this.addResourceForm.reset({
      name: '',
      description: '',
      type: 'OTHER',
      category: 'OTHER',
      resourceUrl: '',
      allowedUserGroups: ''
    });
    this.selectedFile = null;
    this.uploadMode = 'url';
  }

  onAddResource(): void {
    if (this.addResourceForm.invalid) {
      console.log('Form is invalid:', this.addResourceForm.errors);
      return;
    }
    
    if (this.uploadMode === 'file' && !this.selectedFile) {
      alert('Please select a file to upload');
      return;
    }
    
    if (this.uploadMode === 'url' && !this.addResourceForm.get('resourceUrl')?.value) {
      alert('Please enter a resource URL');
      return;
    }
    
    const resourceData: CreateResourceRequest = {
      name: this.addResourceForm.get('name')?.value,
      description: this.addResourceForm.get('description')?.value,
      type: this.addResourceForm.get('type')?.value,
      category: this.addResourceForm.get('category')?.value,

      isGlobal: false,
      accessType: 'MANAGER_CONTROLLED',
      projectId: this.teamId,
      allowedUserGroups: this.addResourceForm.get('allowedUserGroups')?.value || ''
    };

    if (this.uploadMode === 'url') {
      resourceData.resourceUrl = this.addResourceForm.get('resourceUrl')?.value;
    }

    console.log('Creating resource:', resourceData);

    if (this.uploadMode === 'file' && this.selectedFile) {
      // Show loading state
      const originalText = 'Add Resource';
      const submitBtn = document.querySelector('.modal-actions .btn-primary') as HTMLButtonElement;
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Uploading...';
      }
      
      this.teamService.createResourceWithFile(this.selectedFile, resourceData).subscribe({
        next: (response) => {
          console.log('Resource created successfully:', response);
          alert('Resource created successfully!');
          this.closeAddResourceModal();
          this.loadResources();
        },
        error: (err) => {
          console.error('Error creating resource with file:', {
            status: err.status,
            statusText: err.statusText,
            error: err.error,
            message: err.message,
            url: err.url,
            headers: err.headers,
            fileInfo: {
              name: this.selectedFile?.name,
              size: this.selectedFile?.size,
              type: this.selectedFile?.type
            },
            resourceData: resourceData
          });
          
          let errorMessage = 'Failed to create resource';
          
          if (err.status === 413) {
            errorMessage = 'File too large. Please select a smaller file.';
          } else if (err.status === 415) {
            errorMessage = 'Unsupported file type.';
          } else if (err.status === 500) {
            errorMessage = `Server error during file upload. Backend error: ${err.error?.message || 'Unknown server error'}. Please check the server logs.`;
          } else if (err.status === 403) {
            errorMessage = 'Session expired or access denied. Please refresh the page and login again.';
            setTimeout(() => {
              window.location.href = '/login';
            }, 3000);
          } else if (err.error?.message) {
            errorMessage = `Error: ${err.error.message}`;
          }
          
          alert(errorMessage);
          console.log('Full error object:', err);
        },
        complete: () => {
          // Reset button state
          if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
          }
        }
      });
    } else {
      this.teamService.createResource(resourceData).subscribe({
        next: (response) => {
          console.log('Resource created successfully:', response);
          alert('Resource created successfully!');
          this.closeAddResourceModal();
          this.loadResources();
        },
        error: (err) => {
          console.error('Error creating resource:', err);
          alert('Failed to create resource: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Check file size (limit to 10MB)
      const maxSize = 10 * 1024 * 1024; // 10MB in bytes
      if (file.size > maxSize) {
        alert('File size too large. Please select a file smaller than 10MB.');
        event.target.value = '';
        this.selectedFile = null;
        return;
      }
      
      // Check file type
      const allowedTypes = [
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/pdf',
        'text/plain',
        'image/jpeg',
        'image/png',
        'image/gif'
      ];
      
      if (!allowedTypes.includes(file.type)) {
        console.warn('File type not in allowed list:', file.type);
        // Allow anyway but log warning
      }
      
      this.selectedFile = file;
      console.log('File selected:', {
        name: file.name,
        size: file.size,
        type: file.type
      });
    }
  }

  setUploadMode(mode: 'url' | 'file'): void {
    this.uploadMode = mode;
    if (mode === 'url') {
      this.addResourceForm.get('resourceUrl')?.setValidators([Validators.required]);
    } else {
      this.addResourceForm.get('resourceUrl')?.clearValidators();
    }
    this.addResourceForm.get('resourceUrl')?.updateValueAndValidity();
  }

  goBack(): void {
    this.router.navigate(['/team', this.teamId]);
  }
  
  navigateToCommonResources(): void {
    this.router.navigate(['/team', this.teamId, 'resources', 'common']);
  }

  getFileIcon(resource: TeamResource): string {
    if (resource.filePath) {
      const extension = resource.fileExtension?.toLowerCase() || '';
      if (['.pdf'].includes(extension)) return 'pi pi-file-pdf';
      if (['.jpg', '.jpeg', '.png', '.gif'].includes(extension)) return 'pi pi-image';
      if (['.mp4', '.avi', '.mov'].includes(extension)) return 'pi pi-video';
      return 'pi pi-file';
    }
    return 'pi pi-link';
  }

  getUniqueTypes(): string[] {
    return [...new Set(this.resources.map(r => r.type))].sort();
  }

  getUniqueCategories(): string[] {
    return [...new Set(this.resources.map(r => r.category).filter(c => c))].sort();
  }

  // Role and access methods
  isManagerOrAdmin(): boolean {
    const role = this.teamService.getCurrentUserRole();
    return role === 'PROJECT_MANAGER' || role === 'ADMIN' || role === 'MANAGER' || role === 'TEAMLEAD';
  }

  isManagerOrAdminOnly(): boolean {
    const role = this.teamService.getCurrentUserRole();
    return role === 'PROJECT_MANAGER' || role === 'ADMIN' || role === 'MANAGER';
  }

  canViewFile(resource: TeamResource): boolean {
    return !!(resource.filePath || resource.resourceUrl || resource.fileData);
  }

  viewFile(resource: TeamResource): void {
    if (resource.filePath || resource.fileData) {
      this.teamService.downloadFile(resource.id).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          window.open(url, '_blank');
          setTimeout(() => window.URL.revokeObjectURL(url), 1000);
        },
        error: (err) => console.error('Error viewing file:', err)
      });
    } else if (resource.resourceUrl) {
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
        error: (err) => console.error('Error downloading file:', err)
      });
    } else if (resource.resourceUrl) {
      window.open(resource.resourceUrl, '_blank');
    }
  }

  showResourceAccess(resource: TeamResource): void {
    this.selectedResource = resource;
    this.showResourceAccessModal = true;
    this.loadResourceAccess(resource.id);
  }

  loadResourceAccess(resourceId: number): void {
    this.loadingResourceAccess = true;
    this.teamService.getResourceAccess(resourceId).subscribe({
      next: (access) => {
        this.resourceAccess = access || [];
        this.loadingResourceAccess = false;
      },
      error: (err) => {
        console.error('Error loading resource access:', err);
        this.resourceAccess = [];
        this.loadingResourceAccess = false;
      }
    });
  }

  closeResourceAccessModal(): void {
    this.showResourceAccessModal = false;
    this.selectedResource = null;
    this.resourceAccess = [];
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  // Access control methods for manager resources
  hasApprovedAccess(resource: TeamResource): boolean {
    // 1. Managers always have access
    if (this.isManagerOrAdmin()) return true;
    
    // 2. Resource creator (owner) always has access
    const currentUsername = this.teamService.getCurrentUsername();
    if ((resource as any).createdBy === currentUsername) return true;
    
    // 3. Check user group access
    if (this.hasUserGroupAccess(resource)) return true;
    
    // 4. Other users need approved access requests
    return this.getUserAccessRequests().some(req => 
      req.resourceId === resource.id && req.status === 'APPROVED'
    );
  }

  hasUserGroupAccess(resource: TeamResource): boolean {
    const allowedGroups = resource.allowedUserGroups;
    if (!allowedGroups) return false;
    
    const currentUsername = this.teamService.getCurrentUsername();
    if (!currentUsername) return false;
    
    const groups = allowedGroups.split(',').map(g => g.trim());
    return groups.some(group => {
      if (group && currentUsername.toLowerCase().includes('.' + group.toLowerCase())) {
        return true;
      }
      return false;
    });
  }

  hasPendingRequest(resource: TeamResource): boolean {
    // Managers and owners don't see pending status
    if (this.isManagerOrAdmin()) return false;
    
    const currentUsername = this.teamService.getCurrentUsername();
    if ((resource as any).createdBy === currentUsername) return false;
    
    // Users with group access don't see pending status
    if (this.hasUserGroupAccess(resource)) return false;
    
    // Only other users see pending status
    return this.getUserAccessRequests().some(req => 
      req.resourceId === resource.id && req.status === 'PENDING'
    );
  }

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
    if (this.requestAccessForm.invalid || !this.selectedResource) return;
    
    const requestData = {
      resourceId: this.selectedResource.id,
      requestedAccessLevel: 'read',
      justification: this.requestAccessForm.get('justification')?.value
    };
    
    this.teamService.createAccessRequest(
      this.teamService.getCurrentUsername(), 
      requestData, 
      this.teamId
    ).subscribe({
      next: () => {
        alert('Access request submitted successfully!');
        this.closeRequestAccessModal();
        this.loadUserAccessRequests();
      },
      error: (err) => {
        console.error('Error submitting request:', err);
        alert('Failed to submit access request');
      }
    });
  }

  private getUserAccessRequests(): any[] {
    return this.userAccessRequests;
  }

  isResourceCreator(resource: TeamResource): boolean {
    const currentUsername = this.teamService.getCurrentUsername();
    const createdBy = (resource as any).createdBy;
    
    // If createdBy is not set, assume current user owns resources they can see
    // This handles legacy resources without createdBy field
    if (!createdBy && !this.isManagerOrAdmin()) {
      return true; // Non-managers seeing the resource likely created it
    }
    
    return createdBy === currentUsername;
  }

  updateExistingResources(): void {
    if (confirm('This will update all existing manager-controlled resources to allow access for dev, test, and QA users. Continue?')) {
      this.teamService.updateExistingResourcesWithUserGroups().subscribe({
        next: (response) => {
          alert('Successfully updated existing resources with user group access!');
          this.loadResources();
        },
        error: (err) => {
          console.error('Error updating resources:', err);
          alert('Failed to update resources: ' + (err.error?.message || err.message));
        }
      });
    }
  }
  
  revokeUserAccess(userId: number): void {
    if (!this.selectedResource || !confirm('Are you sure you want to revoke access for this user?')) {
      return;
    }
    
    this.teamService.revokeUserAccess(userId, this.selectedResource.id).subscribe({
      next: () => {
        alert('User access revoked successfully!');
        this.loadResourceAccess(this.selectedResource!.id);
      },
      error: (err) => {
        const errorMessage = err.error?.message || 'Failed to revoke access';
        alert(errorMessage);
      }
    });
  }
  
  setActiveTab(tab: string): void {
    this.router.navigate(['/team', this.teamId], { queryParams: { tab: tab } });
  }
  
  team: any = null;
  allProjects: any[] = [];
  sidebarHidden = false;
  currentUsername: string = '';
  showProjectSwitcher = false;
  showNotifications = false;
  showUserDropdown = false;
  unreadNotificationCount = 0;
  notifications: any[] = [];
  
  getStatusClass(status: string): string {
    return status ? `status-${status.toLowerCase()}` : 'status-unknown';
  }
  
  toggleSidebar(): void {
    this.sidebarHidden = !this.sidebarHidden;
  }
  
  switchProject(event: any): void {
    const projectId = +event.target.value;
    if (projectId && projectId !== this.teamId) {
      this.router.navigate(['/team', projectId, 'resources', 'manager']).then(() => {
        window.location.reload();
      });
    }
  }
  
  loadAllProjects(): void {
    this.teamService.getTeams().subscribe({
      next: (teams) => {
        this.allProjects = teams || [];
      },
      error: (err) => {
        console.error('Error loading all projects:', err);
        this.allProjects = [];
      }
    });
  }
  
  logout(): void {
    this.teamService.logout();
    this.router.navigate(['/login']);
  }
  
  updateTeamStatus(teamId: number, event: any): void {
    const newStatus = event.target.value;
    this.teamService.updateTeamStatus(teamId, newStatus).subscribe({
      next: () => {
        if (this.team) {
          this.team.status = newStatus;
        }
      },
      error: (err) => {
        console.error('Error updating team status:', err);
      }
    });
  }
  
  setAccessFilter(filter: string): void {
    this.accessFilter = filter;
    this.applyFilters();
  }
  
  matchesAccessFilter(resource: TeamResource): boolean {
    switch (this.accessFilter) {
      case 'locked':
        return !this.hasApprovedAccess(resource) && !this.hasPendingRequest(resource);
      case 'pending':
        return this.hasPendingRequest(resource);
      case 'open':
        return this.hasApprovedAccess(resource);
      case 'all':
      default:
        return true;
    }
  }
  
  toggleProjectSwitcher(): void {
    this.showProjectSwitcher = !this.showProjectSwitcher;
  }

  closeProjectSwitcher(): void {
    this.showProjectSwitcher = false;
  }

  selectProject(projectId: number): void {
    if (projectId && projectId !== this.teamId) {
      this.router.navigate(['/team', projectId, 'resources', 'manager']).then(() => {
        window.location.reload();
      });
    }
    this.closeProjectSwitcher();
  }

  // Navbar methods
  openSharedDocuments(): void {
    this.router.navigate(['/team', this.teamId], { queryParams: { tab: 'shared-documents' } });
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  toggleUserDropdown(): void {
    this.showUserDropdown = !this.showUserDropdown;
  }

  getCurrentUserEmail(): string {
    return 'user@example.com'; // Placeholder implementation
  }

  getCurrentUserRole(): string {
    return this.teamService.getCurrentUserRole();
  }

  openProfileSettings(): void {
    this.router.navigate(['/team', this.teamId], { queryParams: { tab: 'profile-settings' } });
  }

  markNotificationAsRead(id: number): void {
    // Implementation for marking notification as read
  }


}