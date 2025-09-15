import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../services/team.service';
import { ToastService } from '../../services/toast.service';

export interface AuditLogEntry {
  id: number;
  userId: number;
  username: string;
  resourceId: number;
  resourceName: string;
  action: string;
  actionType: string;
  timestamp: string;
  details: string;
  ipAddress?: string;
  userAgent?: string;
}

@Component({
  selector: 'app-audit-log',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="audit-log-page">
      <!-- Header -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-left">
            <button class="back-button" (click)="goBack()">
              <span>←</span> Back to Team
            </button>
            <div class="header-title">
              <h1>Audit Log</h1>
              <p>Resource access and usage history</p>
            </div>
          </div>
          <div class="header-stats">
            <div class="stat-item">
              <span class="stat-number">{{ totalEntries }}</span>
              <span class="stat-label">Total Entries</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ uniqueUsers }}</span>
              <span class="stat-label">Active Users</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ uniqueResources }}</span>
              <span class="stat-label">Resources Accessed</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters and Search -->
      <div class="filters-section">
        <div class="search-box">
          <input 
            type="text" 
            placeholder="Search audit logs..." 
            [(ngModel)]="searchTerm"
            (input)="onSearch()"
            class="search-input"
          >
          <span class="search-icon">🔍</span>
        </div>
        
        <div class="filter-controls">
          <select [(ngModel)]="selectedUser" (change)="onFilterChange()" class="filter-select">
            <option value="">All Users</option>
            <option *ngFor="let user of uniqueUserList" [value]="user">{{ user }}</option>
          </select>
          
          <select [(ngModel)]="selectedResource" (change)="onFilterChange()" class="filter-select">
            <option value="">All Resources</option>
            <option *ngFor="let resource of uniqueResourceList" [value]="resource">{{ resource }}</option>
          </select>
          
          <select [(ngModel)]="selectedAction" (change)="onFilterChange()" class="filter-select">
            <option value="">All Actions</option>
            <option value="VIEW">View</option>
            <option value="DOWNLOAD">Download</option>
            <option value="UPLOAD">Upload</option>
            <option value="CREATE">Create</option>
            <option value="UPDATE">Update</option>
            <option value="DELETE">Delete</option>
            <option value="ACCESS_REQUEST">Access Request</option>
            <option value="ACCESS_APPROVED">Access Approved</option>
            <option value="ACCESS_REJECTED">Access Rejected</option>
          </select>
          
          <select [(ngModel)]="dateRange" (change)="onFilterChange()" class="filter-select">
            <option value="">All Time</option>
            <option value="today">Today</option>
            <option value="week">This Week</option>
            <option value="month">This Month</option>
            <option value="quarter">This Quarter</option>
            <option value="year">This Year</option>
          </select>
          
          <select [(ngModel)]="sortBy" (change)="onSortChange()" class="filter-select">
            <option value="timestamp">Sort by Date (Newest)</option>
            <option value="timestamp_asc">Sort by Date (Oldest)</option>
            <option value="user">Sort by User</option>
            <option value="resource">Sort by Resource</option>
            <option value="action">Sort by Action</option>
          </select>
        </div>
      </div>

      <!-- Audit Log Entries -->
      <div class="audit-container">
        <div *ngIf="loading" class="loading-state">
          <div class="loading-spinner">⏳</div>
          <p>Loading audit logs...</p>
        </div>
        
        <div *ngIf="!loading && filteredEntries.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <h3>No audit entries found</h3>
          <p>Try adjusting your search or filters</p>
        </div>
        
        <div class="audit-entries" *ngIf="!loading && filteredEntries.length > 0">
          <div 
            *ngFor="let entry of paginatedEntries; trackBy: trackByEntryId"
            class="audit-entry"
            [class]="getEntryClass(entry.actionType)"
          >
            <div class="entry-header">
              <div class="entry-icon">{{ getActionIcon(entry.actionType) }}</div>
              <div class="entry-info">
                <h4 class="entry-title">{{ entry.action }}</h4>
                <p class="entry-subtitle">
                  <strong>{{ entry.username }}</strong> {{ getActionDescription(entry.actionType) }} 
                  <strong>{{ entry.resourceName }}</strong>
                </p>
              </div>
              <div class="entry-meta">
                <span class="entry-time">{{ formatTimestamp(entry.timestamp) }}</span>
                <span class="entry-date">{{ formatDate(entry.timestamp) }}</span>
              </div>
            </div>
            
            <div class="entry-details" *ngIf="entry.details">
              <p class="entry-description">{{ entry.details }}</p>
            </div>
            
            <div class="entry-footer">
              <div class="entry-tags">
                <span class="action-tag" [class]="getActionTagClass(entry.actionType)">
                  {{ entry.actionType }}
                </span>
                <span class="resource-tag">{{ entry.resourceName }}</span>
              </div>
              
              <div class="entry-actions">
                <button class="view-details-btn" (click)="viewEntryDetails(entry)">
                  View Details
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="pagination-container" *ngIf="totalPages > 1">
        <div class="pagination">
          <button 
            class="pagination-btn"
            [disabled]="currentPage === 1"
            (click)="goToPage(currentPage - 1)"
          >
            ← Previous
          </button>
          
          <div class="pagination-pages">
            <button 
              *ngFor="let page of getPageNumbers()"
              class="pagination-page"
              [class.active]="page === currentPage"
              (click)="goToPage(page)"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            class="pagination-btn"
            [disabled]="currentPage === totalPages"
            (click)="goToPage(currentPage + 1)"
          >
            Next →
          </button>
        </div>
        
        <div class="pagination-info">
          Showing {{ startIndex + 1 }}-{{ endIndex }} of {{ filteredEntries.length }} entries
        </div>
      </div>

      <!-- Entry Details Modal -->
      <div *ngIf="showDetailsModal" class="modal-overlay" (click)="closeDetailsModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>Audit Entry Details</h3>
            <button class="modal-close" (click)="closeDetailsModal()">×</button>
          </div>
          
          <div class="modal-body" *ngIf="selectedEntry">
            <div class="details-grid">
              <div class="detail-item">
                <label>User:</label>
                <span>{{ selectedEntry.username }}</span>
              </div>
              <div class="detail-item">
                <label>Resource:</label>
                <span>{{ selectedEntry.resourceName }}</span>
              </div>
              <div class="detail-item">
                <label>Action:</label>
                <span class="action-badge" [class]="getActionTagClass(selectedEntry.actionType)">
                  {{ selectedEntry.actionType }}
                </span>
              </div>
              <div class="detail-item">
                <label>Timestamp:</label>
                <span>{{ formatFullTimestamp(selectedEntry.timestamp) }}</span>
              </div>
              <div class="detail-item" *ngIf="selectedEntry.ipAddress">
                <label>IP Address:</label>
                <span>{{ selectedEntry.ipAddress }}</span>
              </div>
              <div class="detail-item" *ngIf="selectedEntry.userAgent">
                <label>User Agent:</label>
                <span class="user-agent">{{ selectedEntry.userAgent }}</span>
              </div>
            </div>
            
            <div class="details-description" *ngIf="selectedEntry.details">
              <label>Details:</label>
              <p>{{ selectedEntry.details }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./audit-log.component.css']
})
export class AuditLogComponent implements OnInit {
  teamId: number = 0;
  auditEntries: AuditLogEntry[] = [];
  filteredEntries: AuditLogEntry[] = [];
  paginatedEntries: AuditLogEntry[] = [];
  loading = true;
  
  // Filtering and search
  searchTerm: string = '';
  selectedUser: string = '';
  selectedResource: string = '';
  selectedAction: string = '';
  dateRange: string = '';
  sortBy: string = 'timestamp';
  
  // Pagination
  currentPage: number = 1;
  pageSize: number = 20;
  totalPages: number = 0;
  totalEntries: number = 0;
  
  // Modal
  showDetailsModal = false;
  selectedEntry: AuditLogEntry | null = null;
  
  get startIndex(): number {
    return (this.currentPage - 1) * this.pageSize;
  }
  
  get endIndex(): number {
    return Math.min(this.startIndex + this.pageSize, this.filteredEntries.length);
  }
  
  get uniqueUsers(): number {
    return new Set(this.auditEntries.map(entry => entry.username)).size;
  }
  
  get uniqueResources(): number {
    return new Set(this.auditEntries.map(entry => entry.resourceName)).size;
  }
  
  get uniqueUserList(): string[] {
    return Array.from(new Set(this.auditEntries.map(entry => entry.username))).sort();
  }
  
  get uniqueResourceList(): string[] {
    return Array.from(new Set(this.auditEntries.map(entry => entry.resourceName))).sort();
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teamService: TeamService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.teamId = +params['id'];
      this.loadAuditLogs();
    });
  }

  loadAuditLogs(): void {
    this.loading = true;
    // Mock data for now - in a real app, this would call the backend
    this.auditEntries = this.generateMockAuditData();
    this.applyFilters();
    this.loading = false;
  }

  generateMockAuditData(): AuditLogEntry[] {
    const users = ['emily.dev', 'arjun.dev', 'vikram.dev', 'sophia.test', 'ravi.test'];
    const resources = ['Project Documentation.pdf', 'Database Schema.sql', 'API Documentation.md', 'User Manual.docx', 'Code Repository'];
    const actions = ['VIEW', 'DOWNLOAD', 'UPLOAD', 'CREATE', 'UPDATE', 'ACCESS_REQUEST', 'ACCESS_APPROVED', 'ACCESS_REJECTED'];
    
    const entries: AuditLogEntry[] = [];
    const now = new Date();
    
    for (let i = 0; i < 150; i++) {
      const user = users[Math.floor(Math.random() * users.length)];
      const resource = resources[Math.floor(Math.random() * resources.length)];
      const action = actions[Math.floor(Math.random() * actions.length)];
      const timestamp = new Date(now.getTime() - Math.random() * 30 * 24 * 60 * 60 * 1000); // Last 30 days
      
      entries.push({
        id: i + 1,
        userId: Math.floor(Math.random() * 10) + 1,
        username: user,
        resourceId: Math.floor(Math.random() * 20) + 1,
        resourceName: resource,
        action: this.getActionDescription(action),
        actionType: action,
        timestamp: timestamp.toISOString(),
        details: this.getActionDetails(action, user, resource),
        ipAddress: `192.168.1.${Math.floor(Math.random() * 255)}`,
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
      });
    }
    
    return entries.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
  }

  getActionDescription(actionType: string): string {
    const descriptions: { [key: string]: string } = {
      'VIEW': 'viewed',
      'DOWNLOAD': 'downloaded',
      'UPLOAD': 'uploaded',
      'CREATE': 'created',
      'UPDATE': 'updated',
      'DELETE': 'deleted',
      'ACCESS_REQUEST': 'requested access to',
      'ACCESS_APPROVED': 'was granted access to',
      'ACCESS_REJECTED': 'was denied access to'
    };
    return descriptions[actionType] || 'performed action on';
  }

  getActionDetails(actionType: string, user: string, resource: string): string {
    const details: { [key: string]: string } = {
      'VIEW': `User ${user} opened ${resource} for viewing`,
      'DOWNLOAD': `User ${user} downloaded ${resource}`,
      'UPLOAD': `User ${user} uploaded a new version of ${resource}`,
      'CREATE': `User ${user} created new resource: ${resource}`,
      'UPDATE': `User ${user} updated ${resource}`,
      'ACCESS_REQUEST': `User ${user} requested access to ${resource}`,
      'ACCESS_APPROVED': `Access to ${resource} was approved for ${user}`,
      'ACCESS_REJECTED': `Access to ${resource} was rejected for ${user}`
    };
    return details[actionType] || `User ${user} performed ${actionType} on ${resource}`;
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applyFilters();
  }

  onFilterChange(): void {
    this.currentPage = 1;
    this.applyFilters();
  }

  onSortChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = [...this.auditEntries];

    // Search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(entry => 
        entry.username.toLowerCase().includes(term) ||
        entry.resourceName.toLowerCase().includes(term) ||
        entry.action.toLowerCase().includes(term) ||
        entry.details.toLowerCase().includes(term)
      );
    }

    // User filter
    if (this.selectedUser) {
      filtered = filtered.filter(entry => entry.username === this.selectedUser);
    }

    // Resource filter
    if (this.selectedResource) {
      filtered = filtered.filter(entry => entry.resourceName === this.selectedResource);
    }

    // Action filter
    if (this.selectedAction) {
      filtered = filtered.filter(entry => entry.actionType === this.selectedAction);
    }

    // Date range filter
    if (this.dateRange) {
      const now = new Date();
      const filterDate = new Date();
      
      switch (this.dateRange) {
        case 'today':
          filterDate.setHours(0, 0, 0, 0);
          break;
        case 'week':
          filterDate.setDate(now.getDate() - 7);
          break;
        case 'month':
          filterDate.setMonth(now.getMonth() - 1);
          break;
        case 'quarter':
          filterDate.setMonth(now.getMonth() - 3);
          break;
        case 'year':
          filterDate.setFullYear(now.getFullYear() - 1);
          break;
      }
      
      filtered = filtered.filter(entry => new Date(entry.timestamp) >= filterDate);
    }

    // Sort
    filtered.sort((a, b) => {
      switch (this.sortBy) {
        case 'timestamp':
          return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
        case 'timestamp_asc':
          return new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime();
        case 'user':
          return a.username.localeCompare(b.username);
        case 'resource':
          return a.resourceName.localeCompare(b.resourceName);
        case 'action':
          return a.actionType.localeCompare(b.actionType);
        default:
          return 0;
      }
    });

    this.filteredEntries = filtered;
    this.totalEntries = filtered.length;
    this.totalPages = Math.ceil(this.totalEntries / this.pageSize);
    this.updatePaginatedEntries();
  }

  updatePaginatedEntries(): void {
    const start = this.startIndex;
    const end = this.endIndex;
    this.paginatedEntries = this.filteredEntries.slice(start, end);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePaginatedEntries();
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible - 1);
    
    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1);
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  trackByEntryId(index: number, entry: AuditLogEntry): number {
    return entry.id;
  }

  getActionIcon(actionType: string): string {
    const icons: { [key: string]: string } = {
      'VIEW': '👁️',
      'DOWNLOAD': '⬇️',
      'UPLOAD': '⬆️',
      'CREATE': '➕',
      'UPDATE': '✏️',
      'DELETE': '🗑️',
      'ACCESS_REQUEST': '🔓',
      'ACCESS_APPROVED': '✅',
      'ACCESS_REJECTED': '❌'
    };
    return icons[actionType] || '📋';
  }

  getEntryClass(actionType: string): string {
    return `entry-${actionType.toLowerCase()}`;
  }

  getActionTagClass(actionType: string): string {
    return `action-tag-${actionType.toLowerCase()}`;
  }

  formatTimestamp(timestamp: string): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  }

  formatDate(timestamp: string): string {
    return new Date(timestamp).toLocaleDateString();
  }

  formatFullTimestamp(timestamp: string): string {
    return new Date(timestamp).toLocaleString();
  }

  viewEntryDetails(entry: AuditLogEntry): void {
    this.selectedEntry = entry;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedEntry = null;
  }

  goBack(): void {
    this.router.navigate(['/team', this.teamId]);
  }
}
