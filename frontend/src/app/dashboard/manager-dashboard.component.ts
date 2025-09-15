import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="manager-dashboard">
      <h2>Project Manager Dashboard</h2>
      
      <!-- My Projects Overview -->
      <div class="dashboard-section">
        <h3>My Projects</h3>
        <div class="projects-grid">
          <div *ngFor="let project of myProjects" class="project-card">
            <h4>{{project.name}}</h4>
            <p>{{project.description}}</p>
            <div class="project-stats">
              <span>Team: {{project.teamSize}} members</span>
              <span>Resources: {{project.resourceCount}}</span>
            </div>
            <button class="btn-small" (click)="viewProject(project.id)">Manage</button>
          </div>
        </div>
      </div>

      <!-- Access Requests -->
      <div class="dashboard-section">
        <h3>Pending Access Requests</h3>
        <div class="requests-table">
          <div *ngFor="let request of pendingRequests" class="request-row">
            <span class="requester">{{request.userName}}</span>
            <span class="resource">{{request.resourceName}}</span>
            <span class="access-level">{{request.accessLevel}}</span>
            <div class="request-actions">
              <button class="btn-approve" (click)="approveRequest(request.id)">Approve</button>
              <button class="btn-reject" (click)="rejectRequest(request.id)">Reject</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="dashboard-section">
        <h3>Quick Actions</h3>
        <div class="action-buttons">
          <button class="action-btn primary" (click)="navigateTo('/my-projects')">View All Projects</button>
          <button class="action-btn primary" (click)="navigateTo('/team-members')">Manage Team</button>
          <button class="action-btn secondary" (click)="navigateTo('/resources/create')">Add Resource</button>
          <button class="action-btn secondary" (click)="navigateTo('/access-requests')">All Requests</button>
        </div>
      </div>

      <!-- Team Performance -->
      <div class="dashboard-section">
        <h3>Team Activity</h3>
        <div class="activity-summary">
          <div class="metric">
            <span class="metric-label">Active Team Members</span>
            <span class="metric-value">{{teamStats.activeMembers}}</span>
          </div>
          <div class="metric">
            <span class="metric-label">Resources Accessed Today</span>
            <span class="metric-value">{{teamStats.resourcesAccessed}}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.css']
})
export class ManagerDashboardComponent implements OnInit {
  myProjects: any[] = [];
  pendingRequests: any[] = [];
  teamStats = {
    activeMembers: 0,
    resourcesAccessed: 0
  };

  ngOnInit() {
    this.loadMyProjects();
    this.loadPendingRequests();
    this.loadTeamStats();
  }

  loadMyProjects() {
    // Load projects managed by this manager
  }

  loadPendingRequests() {
    // Load access requests for manager's projects
  }

  loadTeamStats() {
    // Load team activity statistics
  }

  approveRequest(requestId: number) {
    // Approve access request
  }

  rejectRequest(requestId: number) {
    // Reject access request
  }

  viewProject(projectId: number) {
    // Navigate to project details
  }

  navigateTo(route: string) {
    // Navigation logic
  }
}