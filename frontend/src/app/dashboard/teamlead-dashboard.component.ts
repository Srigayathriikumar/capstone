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
      
      <!-- My Project Overview -->
      <div class="dashboard-section">
        <h3>My Project</h3>
        <div class="project-overview">
          <div *ngIf="myProject" class="project-card">
            <h4>{{myProject.name}}</h4>
            <p>{{myProject.description}}</p>
            <div class="project-stats">
              <div class="stat-item">
                <span class="stat-label">Team Members</span>
                <span class="stat-value">{{myProject.teamSize}}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Resources</span>
                <span class="stat-value">{{myProject.resourceCount}}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Pending Requests</span>
                <span class="stat-value">{{teamRequests.length}}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Team Members -->
      <div class="dashboard-section">
        <h3>My Team Members</h3>
        <div class="team-members-grid">
          <div *ngFor="let member of teamMembers" class="member-card">
            <div class="member-info">
              <span class="member-name">{{member.fullName}}</span>
              <span class="member-role">{{member.role}}</span>
              <span class="member-email">{{member.email}}</span>
            </div>
            <span class="member-status" [class]="'status-' + member.status">{{member.status}}</span>
          </div>
        </div>
      </div>

      <!-- Access Requests to Review -->
      <div class="dashboard-section">
        <h3>Pending Access Requests</h3>
        <div class="requests-table">
          <div *ngFor="let request of teamRequests" class="request-row">
            <span class="requester">{{request.userName}}</span>
            <span class="resource">{{request.resourceName}}</span>
            <span class="access-level">{{request.accessLevel}}</span>
            <span class="justification">{{request.justification}}</span>
            <div class="request-actions">
              <button class="btn-approve" (click)="approveRequest(request.id)">Approve</button>
              <button class="btn-reject" (click)="rejectRequest(request.id)">Reject</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Resources I Manage -->
      <div class="dashboard-section">
        <h3>Resources I Manage</h3>
        <div class="resources-grid">
          <div *ngFor="let resource of managedResources" class="resource-card">
            <h4>{{resource.name}}</h4>
            <p>{{resource.type}} - {{resource.category}}</p>
            <div class="resource-stats">
              <span>Access Level: {{resource.myAccessLevel}}</span>
              <span>Active Users: {{resource.userCount}}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Team Activity Summary -->
      <div class="dashboard-section">
        <h3>Team Activity</h3>
        <div class="activity-summary">
          <div class="metric">
            <span class="metric-label">Resources Accessed Today</span>
            <span class="metric-value">{{teamStats.resourcesAccessed}}</span>
          </div>
          <div class="metric">
            <span class="metric-label">Requests This Week</span>
            <span class="metric-value">{{teamStats.weeklyRequests}}</span>
          </div>
          <div class="metric">
            <span class="metric-label">Team Productivity</span>
            <span class="metric-value">{{teamStats.productivity}}%</span>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="dashboard-section">
        <h3>Quick Actions</h3>
        <div class="action-buttons">
          <button class="action-btn primary" (click)="navigateTo('/my-project')">View Project</button>
          <button class="action-btn primary" (click)="navigateTo('/team-members')">Manage Team</button>
          <button class="action-btn secondary" (click)="navigateTo('/access-requests')">All Requests</button>
          <button class="action-btn secondary" (click)="navigateTo('/audit-logs')">Audit Logs</button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.css']
})
export class TeamleadDashboardComponent implements OnInit {
  myProject: any = null;
  teamStats = {
    resourcesAccessed: 0,
    weeklyRequests: 0,
    productivity: 0
  };
  
  teamMembers: any[] = [];
  teamRequests: any[] = [];
  managedResources: any[] = [];

  ngOnInit() {
    this.loadMyProject();
    this.loadTeamStats();
    this.loadTeamMembers();
    this.loadTeamRequests();
    this.loadManagedResources();
  }

  loadMyProject() {
    // Load project managed by this team lead
    this.myProject = {
      name: 'Employee360',
      description: 'Comprehensive employee management and engagement platform',
      teamSize: 5,
      resourceCount: 8
    };
  }

  loadTeamStats() {
    // Load team activity statistics
    this.teamStats = {
      resourcesAccessed: 24,
      weeklyRequests: 8,
      productivity: 87
    };
  }

  loadTeamMembers() {
    // Load team members under this lead
    this.teamMembers = [
      { fullName: 'Vikram Gupta', role: 'Developer', email: 'vikram.dev@company.com', status: 'active' },
      { fullName: 'Michael Brown', role: 'Developer', email: 'michael.dev@company.com', status: 'active' },
      { fullName: 'Ravi Mehta', role: 'Tester', email: 'ravi.test@company.com', status: 'active' }
    ];
  }

  loadTeamRequests() {
    // Load access requests from team members
    this.teamRequests = [
      {
        id: 1,
        userName: 'Vikram Gupta',
        resourceName: 'Employee Database',
        accessLevel: 'READ',
        justification: 'Need access for profile management feature'
      },
      {
        id: 2,
        userName: 'Michael Brown',
        resourceName: 'Employee API',
        accessLevel: 'WRITE',
        justification: 'Need API access for employee data integration'
      }
    ];
  }

  loadManagedResources() {
    // Load resources with ADMIN access
    this.managedResources = [
      {
        name: 'Employee Database',
        type: 'DATABASE',
        category: 'DATABASE',
        myAccessLevel: 'ADMIN',
        userCount: 5
      },
      {
        name: 'Employee API',
        type: 'API',
        category: 'API',
        myAccessLevel: 'ADMIN',
        userCount: 3
      }
    ];
  }

  approveRequest(requestId: number) {
    // Approve team member's access request
    console.log('Approving request:', requestId);
  }

  rejectRequest(requestId: number) {
    // Reject team member's access request
    console.log('Rejecting request:', requestId);
  }

  navigateTo(route: string) {
    // Navigation logic
    console.log('Navigating to:', route);
  }
}