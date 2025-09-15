import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { ManagerDashboardComponent } from './manager-dashboard.component';
import { TeamleadDashboardComponent } from './teamlead-dashboard.component';
import { MemberDashboardComponent } from './member-dashboard.component';
import { ViewerDashboardComponent } from './viewer-dashboard.component';

@Component({
  selector: 'app-dashboard-router',
  standalone: true,
  imports: [
    CommonModule,
    AdminDashboardComponent,
    ManagerDashboardComponent,
    TeamleadDashboardComponent,
    MemberDashboardComponent,
    ViewerDashboardComponent
  ],
  template: `
    <div class="dashboard-container">
      <!-- Navigation Header -->
      <header class="dashboard-header">
        <div class="header-content">
          <h1>Team Resource Access Management</h1>
          <div class="user-info">
            <span class="user-name">{{currentUser?.fullName}}</span>
            <span class="user-role">{{currentUser?.role}}</span>
            <button class="logout-btn" (click)="logout()">Logout</button>
          </div>
        </div>
      </header>

      <!-- Role-based Dashboard Content -->
      <main class="dashboard-main">
        <app-admin-dashboard *ngIf="userRole === 'ADMIN'"></app-admin-dashboard>
        <app-manager-dashboard *ngIf="userRole === 'PROJECT_MANAGER'"></app-manager-dashboard>
        <app-teamlead-dashboard *ngIf="userRole === 'TEAMLEAD'"></app-teamlead-dashboard>
        <app-member-dashboard *ngIf="userRole === 'TEAM_MEMBER'"></app-member-dashboard>
        <app-viewer-dashboard *ngIf="userRole === 'VIEWER'"></app-viewer-dashboard>
      </main>
    </div>
  `,
  styles: [`
    .dashboard-container {
      min-height: 100vh;
      background-color: #f5f5f5;
    }

    .dashboard-header {
      background-color: #2c3e50;
      color: white;
      padding: 1rem 0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .header-content h1 {
      margin: 0;
      font-size: 1.5rem;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .user-name {
      font-weight: 600;
    }

    .user-role {
      background-color: #34495e;
      padding: 0.25rem 0.75rem;
      border-radius: 1rem;
      font-size: 0.875rem;
    }

    .logout-btn {
      background-color: #e74c3c;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 0.25rem;
      cursor: pointer;
      transition: background-color 0.2s;
    }

    .logout-btn:hover {
      background-color: #c0392b;
    }

    .dashboard-main {
      max-width: 1200px;
      margin: 0 auto;
      padding: 2rem;
    }
  `]
})
export class DashboardRouterComponent implements OnInit {
  currentUser: any = null;
  userRole: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.userRole = user?.role || '';
    });
  }

  logout() {
    this.authService.logout();
  }
}