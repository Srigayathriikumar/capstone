import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-viewer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="viewer-dashboard">
      <h2>Viewer Dashboard</h2>
      
      <!-- Read-Only Project Information -->
      <div class="dashboard-section">
        <h3>Projects Overview</h3>
        <div class="projects-readonly">
          <div *ngFor="let project of visibleProjects" class="project-view">
            <h4>{{project.name}}</h4>
            <p>{{project.description}}</p>
            <div class="project-details">
              <span class="status">Status: {{project.status}}</span>
              <span class="team-size">Team: {{project.teamSize}} members</span>
              <span class="resources">Resources: {{project.resourceCount}}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Available Resources (Read-Only) -->
      <div class="dashboard-section">
        <h3>Available Resources</h3>
        <div class="resources-readonly">
          <div *ngFor="let resource of viewableResources" class="resource-view">
            <h4>{{resource.name}}</h4>
            <p>{{resource.description}}</p>
            <div class="resource-details">
              <span class="type">Type: {{resource.type}}</span>
              <span class="category">Category: {{resource.category}}</span>
              <span class="access">Access: Read Only</span>
            </div>
          </div>
        </div>
      </div>

      <!-- System Statistics (Read-Only) -->
      <div class="dashboard-section">
        <h3>System Overview</h3>
        <div class="stats-readonly">
          <div class="stat-view">
            <span class="stat-label">Total Projects</span>
            <span class="stat-value">{{systemStats.totalProjects}}</span>
          </div>
          <div class="stat-view">
            <span class="stat-label">Active Users</span>
            <span class="stat-value">{{systemStats.activeUsers}}</span>
          </div>
          <div class="stat-view">
            <span class="stat-label">Available Resources</span>
            <span class="stat-value">{{systemStats.totalResources}}</span>
          </div>
        </div>
      </div>

      <!-- Recent Activities (Read-Only) -->
      <div class="dashboard-section">
        <h3>Recent Activities</h3>
        <div class="activities-readonly">
          <div *ngFor="let activity of recentActivities" class="activity-view">
            <span class="activity-time">{{activity.timestamp | date:'short'}}</span>
            <span class="activity-description">{{activity.description}}</span>
            <span class="activity-type">{{activity.type}}</span>
          </div>
        </div>
      </div>

      <!-- Limited Actions -->
      <div class="dashboard-section">
        <h3>Available Actions</h3>
        <div class="action-buttons">
          <button class="action-btn secondary" (click)="navigateTo('/projects/view')">View Projects</button>
          <button class="action-btn secondary" (click)="navigateTo('/resources/view')">View Resources</button>
          <button class="action-btn secondary" (click)="navigateTo('/reports')">View Reports</button>
        </div>
      </div>

      <!-- Access Limitation Notice -->
      <div class="dashboard-section">
        <div class="access-notice">
          <h4>Access Level: Viewer</h4>
          <p>You have read-only access to system information. Contact your administrator for additional permissions.</p>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.css']
})
export class ViewerDashboardComponent implements OnInit {
  visibleProjects: any[] = [];
  viewableResources: any[] = [];
  systemStats = {
    totalProjects: 0,
    activeUsers: 0,
    totalResources: 0
  };
  recentActivities: any[] = [];

  ngOnInit() {
    this.loadVisibleProjects();
    this.loadViewableResources();
    this.loadSystemStats();
    this.loadRecentActivities();
  }

  loadVisibleProjects() {
    // Load projects visible to viewer
  }

  loadViewableResources() {
    // Load resources visible to viewer (read-only)
  }

  loadSystemStats() {
    // Load basic system statistics
  }

  loadRecentActivities() {
    // Load recent system activities (read-only)
  }

  navigateTo(route: string) {
    // Navigation logic (limited routes)
  }
}