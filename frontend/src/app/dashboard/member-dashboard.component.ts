import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-member-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="member-dashboard">
      <h2>Team Member Dashboard</h2>
      
      <!-- My Projects -->
      <div class="dashboard-section">
        <h3>My Projects</h3>
        <div class="projects-list">
          <div *ngFor="let project of myProjects" class="project-item">
            <h4>{{project.name}}</h4>
            <p>{{project.description}}</p>
            <span class="project-role">Role: {{project.myRole}}</span>
            <button class="btn-small" (click)="viewProject(project.id)">View Details</button>
          </div>
        </div>
      </div>

      <!-- Available Resources -->
      <div class="dashboard-section">
        <h3>My Resources</h3>
        <div class="resources-grid">
          <div *ngFor="let resource of availableResources" class="resource-card">
            <h4>{{resource.name}}</h4>
            <p>{{resource.type}} - {{resource.category}}</p>
            <div class="resource-info">
              <span class="access-level">Access: {{resource.accessLevel}}</span>
              <span class="project">Project: {{resource.projectName}}</span>
            </div>
            <button class="btn-small" (click)="accessResource(resource.id)">Access</button>
          </div>
        </div>
      </div>

      <!-- Request Access -->
      <div class="dashboard-section">
        <h3>Request New Access</h3>
        <div class="request-form">
          <select [(ngModel)]="selectedResource" class="form-select">
            <option value="">Select Resource</option>
            <option *ngFor="let resource of requestableResources" [value]="resource.id">
              {{resource.name}} ({{resource.type}})
            </option>
          </select>
          <select [(ngModel)]="requestedAccessLevel" class="form-select">
            <option value="READ">Read Access</option>
            <option value="WRITE">Write Access</option>
          </select>
          <textarea [(ngModel)]="justification" placeholder="Justification for access" class="form-textarea"></textarea>
          <button class="btn-primary" (click)="submitRequest()" [disabled]="!selectedResource || !justification">
            Submit Request
          </button>
        </div>
      </div>

      <!-- My Access Requests -->
      <div class="dashboard-section">
        <h3>My Access Requests</h3>
        <div class="requests-list">
          <div *ngFor="let request of myRequests" class="request-item">
            <div class="request-details">
              <span class="resource-name">{{request.resourceName}}</span>
              <span class="access-level">{{request.accessLevel}}</span>
              <span class="status" [class]="request.status.toLowerCase()">{{request.status}}</span>
            </div>
            <div class="request-meta">
              <span class="request-date">{{request.requestDate | date:'short'}}</span>
              <span *ngIf="request.approverComments" class="comments">{{request.approverComments}}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="dashboard-section">
        <h3>Quick Actions</h3>
        <div class="action-buttons">
          <button class="action-btn primary" (click)="navigateTo('/my-resources')">View All Resources</button>
          <button class="action-btn secondary" (click)="navigateTo('/request-history')">Request History</button>
          <button class="action-btn secondary" (click)="navigateTo('/profile')">My Profile</button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.css']
})
export class MemberDashboardComponent implements OnInit {
  myProjects: any[] = [];
  availableResources: any[] = [];
  requestableResources: any[] = [];
  myRequests: any[] = [];
  
  selectedResource: string = '';
  requestedAccessLevel: string = 'READ';
  justification: string = '';

  ngOnInit() {
    this.loadMyProjects();
    this.loadAvailableResources();
    this.loadRequestableResources();
    this.loadMyRequests();
  }

  loadMyProjects() {
    // Load projects user is assigned to
  }

  loadAvailableResources() {
    // Load resources user has access to
  }

  loadRequestableResources() {
    // Load resources user can request access to
  }

  loadMyRequests() {
    // Load user's access request history
  }

  viewProject(projectId: number) {
    // Navigate to project details
  }

  accessResource(resourceId: number) {
    // Access the resource
  }

  submitRequest() {
    // Submit new access request
    if (this.selectedResource && this.justification) {
      // API call to submit request
      this.selectedResource = '';
      this.justification = '';
      this.loadMyRequests(); // Refresh requests
    }
  }

  navigateTo(route: string) {
    // Navigation logic
  }
}