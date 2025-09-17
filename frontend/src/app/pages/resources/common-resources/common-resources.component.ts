import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService, TeamResource, CreateResourceRequest } from '../../../services/team.service';
import { LoadingService } from '../../../services/loading.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-common-resources',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './common-resources.component.html',
  styleUrls: ['./common-resources.component.css', '../../../teamdetails/teamdetails.css']
})
export class CommonResourcesComponent implements OnInit {
  teamId: number = 0;
  resources: TeamResource[] = [];
  filteredResources: TeamResource[] = [];
  paginatedResources: TeamResource[] = [];
  loading = true;
  currentUsername: string = '';
  
  // Filters and search
  searchTerm = '';
  selectedType = '';
  selectedCategory = '';
  
  // Sorting
  sortField = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';
  
  // Pagination
  currentPage = 1;
  pageSize = 12;
  totalPages = 1;
  
  // Modal
  showAddResourceModal = false;
  addResourceForm: FormGroup;
  selectedFile: File | null = null;
  uploadMode: 'url' | 'file' = 'url';
  
  private loadingService = inject(LoadingService);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public teamService: TeamService,
    private fb: FormBuilder,
    private toastService: ToastService
  ) {
    this.addResourceForm = this.fb.group({
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      type: ['OTHER', [Validators.required]],
      category: ['OTHER', [Validators.required]],
      resourceUrl: ['']
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

  loadResources(): void {
    this.loading = true;
    this.loadingService.show();
    this.teamService.getCommonResources(this.teamId).subscribe({
      next: (resources) => {
        this.resources = resources || [];
        this.applyFilters();
        this.loading = false;
        this.loadingService.hide();
      },
      error: (err) => {
        console.error('Error loading resources:', err);
        this.loading = false;
        this.loadingService.hide();
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
      
      return matchesSearch && matchesType && matchesCategory;
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
    this.addResourceForm.reset();
    this.selectedFile = null;
    this.uploadMode = 'url';
  }

  onAddResource(): void {
    if (this.addResourceForm.invalid) {
      console.log('Form is invalid:', this.addResourceForm.errors);
      return;
    }
    
    if (this.uploadMode === 'file' && !this.selectedFile) {
      this.toastService.error('Error', 'Please select a file to upload');
      return;
    }
    
    if (this.uploadMode === 'url' && !this.addResourceForm.get('resourceUrl')?.value) {
      this.toastService.error('Error', 'Please enter a resource URL');
      return;
    }
    
    const resourceData: CreateResourceRequest = {
      name: this.addResourceForm.get('name')?.value,
      description: this.addResourceForm.get('description')?.value,
      type: this.addResourceForm.get('type')?.value,
      category: this.addResourceForm.get('category')?.value,
      accessType: 'COMMON',
      isGlobal: false,
      projectId: this.teamId
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
      
      this.toastService.info('Processing', 'Adding resource...');
      
      this.teamService.createResourceWithFile(this.selectedFile, resourceData).subscribe({
        next: (response) => {
          this.toastService.success('Success', `Resource "${resourceData.name}" uploaded successfully!`);
          this.closeAddResourceModal();
          this.loadResources();
        },
        error: (err) => {
          this.toastService.error('Error', 'Failed to upload resource. Please try again.');
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
      this.toastService.info('Processing', 'Adding resource...');
      
      this.teamService.createResource(resourceData).subscribe({
        next: (response) => {
          this.toastService.success('Success', `Resource "${resourceData.name}" added successfully!`);
          this.closeAddResourceModal();
          this.loadResources();
        },
        error: (err) => {
          this.toastService.error('Error', 'Failed to add resource. Please try again.');
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
        this.toastService.error('Error', 'File size too large. Please select a file smaller than 10MB.');
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
  
  navigateToManagerResources(): void {
    this.router.navigate(['/team', this.teamId, 'resources', 'manager']);
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
    // Implementation for showing resource access
    console.log('Show resource access for:', resource);
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }
  
  setActiveTab(tab: string): void {
    this.router.navigate(['/team', this.teamId], { queryParams: { tab: tab } });
  }
  
  team: any = null;
  allProjects: any[] = [];
  sidebarHidden = false;
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
      this.router.navigate(['/team', projectId, 'resources', 'common']).then(() => {
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
  
  toggleProjectSwitcher(): void {
    this.showProjectSwitcher = !this.showProjectSwitcher;
  }

  closeProjectSwitcher(): void {
    this.showProjectSwitcher = false;
  }

  selectProject(projectId: number): void {
    if (projectId && projectId !== this.teamId) {
      this.router.navigate(['/team', projectId, 'resources', 'common']).then(() => {
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