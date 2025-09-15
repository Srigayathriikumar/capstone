import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="admin-dashboard">
      <h2>Admin Dashboard</h2>
      
      <!-- System Overview -->
      <div class="dashboard-section">
        <h3>System Overview</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <h4>Total Users</h4>
            <span class="stat-number">{{stats.totalUsers}}</span>
          </div>
          <div class="stat-card">
            <h4>Active Projects</h4>
            <span class="stat-number">{{stats.activeProjects}}</span>
          </div>
          <div class="stat-card">
            <h4>Total Resources</h4>
            <span class="stat-number">{{stats.totalResources}}</span>
          </div>
          <div class="stat-card">
            <h4>Pending Requests</h4>
            <span class="stat-number">{{stats.pendingRequests}}</span>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="dashboard-section">
        <h3>Quick Actions</h3>
        <div class="action-buttons">
          <button class="action-btn primary" (click)="navigateTo('/users')">Manage Users</button>
          <button class="action-btn primary" (click)="navigateTo('/projects')">Manage Projects</button>
          <button class="action-btn primary" (click)="navigateTo('/resources')">Manage Resources</button>
          <button class="action-btn secondary" (click)="navigateTo('/audit-logs')">View Audit Logs</button>
        </div>
      </div>

      <!-- Recent Activities -->
      <div class="dashboard-section">
        <h3>Recent System Activities</h3>
        <div class="activity-list">
          <div *ngFor="let activity of recentActivities" class="activity-item">
            <span class="activity-time">{{activity.timestamp | date:'short'}}</span>
            <span class="activity-description">{{activity.description}}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.css']
})
export class AdminDashboardComponent implements OnInit {
  stats = {
    totalUsers: 0,
    activeProjects: 0,
    totalResources: 0,
    pendingRequests: 0
  };

  recentActivities: any[] = [];

  ngOnInit() {
    this.loadStats();
    this.loadRecentActivities();
  }

  loadStats() {
    // Load system statistics
  }

  loadRecentActivities() {
    // Load recent system activities
  }

  navigateTo(route: string) {
    // Navigation logic
  }
}