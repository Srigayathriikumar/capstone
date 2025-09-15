import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-teamlead-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="teamlead-dashboard">
      <h2>Team Lead Dashboard</h2>
      
      <!-- My Team Overview -->
      <div class="dashboard-section">
        <h3>My Team</h3>
        <div class="team-overview">
          <div class="team-stats">
            <div class="stat-item">
              <span class="stat-label">Team Members</span>
              <span class="stat-value">{{teamStats.totalMembers}}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">Active Today</span>
              <span class="stat-value">{{teamStats.activeToday}}</span>
            </div>
          </div>
          <div class="team-members">
            <div *ngFor="let member of teamMembers" class="member-card">
              <span class="member-name">{{member.name}}</span>
              <span class="member-role">{{member.role}}</span>
              <span class="member-status" [class]="member.status">{{member.status}}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Access Requests to Review -->
      <div class="dashboard-section">
        <h3>Access Requests to Review</h3>
        <div class="requests-list">
          <div *ngFor="let request of teamRequests" class="request-item">
            <div class="request-info">
              <span class="requester">{{request.memberName}}</span>
              <span class="resource">{{request.resourceName}}</span>
              <span class="justification">{{request.justification}}</span>
            </div>
            <div class="request-actions">
              <button class="btn-approve" (click)="approveRequest(request.id)">Approve</button>
              <button class="btn-reject" (click)="rejectRequest(request.id)">Reject</button>
            </div>
          </div>
        </div>
      </div>

      <!-- My Resources -->
      <div class="dashboard-section">
        <h3>Resources I Manage</h3>
        <div class="resources-grid">
          <div *ngFor="let resource of managedResources" class="resource-card">
            <h4>{{resource.name}}</h4>
            <p>{{resource.type}} - {{resource.category}}</p>
            <div class="resource-stats">
              <span>Access Level: {{resource.myAccessLevel}}</span>
              <span>Users: {{resource.userCount}}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="dashboard-section">
        <h3>Quick Actions</h3>
        <div class="action-buttons">
          <button class="action-btn primary" (click)="navigateTo('/team-members')">Manage Team</button>
          <button class="action-btn primary" (click)="navigateTo('/my-resources')">View Resources</button>
          <button class="action-btn secondary" (click)="navigateTo('/access-requests')">All Requests</button>
          <button class="action-btn secondary" (click)="navigateTo('/team-activity')">Team Activity</button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.css']
})
export class TeamleadDashboardComponent implements OnInit {
  teamStats = {
    totalMembers: 0,
    activeToday: 0
  };
  
  teamMembers: any[] = [];
  teamRequests: any[] = [];
  managedResources: any[] = [];

  ngOnInit() {
    this.loadTeamStats();
    this.loadTeamMembers();
    this.loadTeamRequests();
    this.loadManagedResources();
  }

  loadTeamStats() {
    // Load team statistics
  }

  loadTeamMembers() {
    // Load team members under this lead
  }

  loadTeamRequests() {
    // Load access requests from team members
  }

  loadManagedResources() {
    // Load resources with WRITE access
  }

  approveRequest(requestId: number) {
    // Approve team member's access request
  }

  rejectRequest(requestId: number) {
    // Reject team member's access request
  }

  navigateTo(route: string) {
    // Navigation logic
  }
}