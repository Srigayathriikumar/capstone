import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TeamService, Team } from '../services/team.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  teams: Team[] = [];
  loading = true;
  currentUser: any;

  constructor(
    private teamService: TeamService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    this.loadTeams();
  }

  loadTeams(): void {
    this.teamService.getTeams().subscribe({
      next: (teams) => {
        this.teams = teams;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading teams:', err);
        this.loading = false;
      }
    });
  }

  viewTeamDetails(teamId: number): void {
    this.router.navigate(['/team', teamId]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusClass(status: string): string {
    return `status-${status.toLowerCase()}`;
  }

  isAdmin(): boolean {
    console.log('Current user:', this.currentUser);
    return this.currentUser?.role === 'ADMIN' || this.currentUser?.role === 'SUPER_ADMIN' || this.currentUser?.username === 'admin' || this.currentUser?.username === 'superadmin';
  }
  
  isManager(): boolean {
    return this.currentUser?.role === 'PROJECT_MANAGER' || this.currentUser?.role === 'MANAGER';
  }

  updateTeamStatus(teamId: number, event: any): void {
    const newStatus = event.target.value;
    this.teamService.updateTeamStatus(teamId, newStatus).subscribe({
      next: () => {
        const team = this.teams.find(t => t.id === teamId);
        if (team) {
          team.status = newStatus;
        }
      },
      error: (err) => {
        console.error('Error updating team status:', err);
        alert('Failed to update team status');
      }
    });
  }
  
  getTotalMembers(): number {
    return this.teams.reduce((total, team) => total + (team.memberCount || 0), 0);
  }
  
  getTotalResources(): number {
    return this.teams.reduce((total, team) => total + (team.resourceCount || 0), 0);
  }
  
  getActiveProjects(): number {
    return this.teams.filter(team => team.status === 'ACTIVE').length;
  }
  
  getRecentActivity(): any[] {
    return [
      {
        type: 'team',
        icon: 'pi pi-users',
        text: 'New team "Mobile App Development" created',
        time: '2 hours ago'
      },
      {
        type: 'resource',
        icon: 'pi pi-file',
        text: 'Resource "API Documentation" uploaded',
        time: '4 hours ago'
      },
      {
        type: 'user',
        icon: 'pi pi-user-plus',
        text: 'New user joined the platform',
        time: '6 hours ago'
      },
      {
        type: 'access',
        icon: 'pi pi-key',
        text: 'Access request approved for Database',
        time: '1 day ago'
      }
    ];
  }
  
  getOnlineUsers(): number {
    return Math.floor(Math.random() * 15) + 5; // Mock data
  }
  
  getLastBackup(): string {
    return new Date(Date.now() - 3600000).toLocaleTimeString('en-US', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }
  
  createNewTeam(): void {
    alert('Create new team functionality would be implemented here');
  }
  
  viewAllTeams(): void {
    alert('View all teams functionality would be implemented here');
  }
  
  viewReports(): void {
    alert('View reports functionality would be implemented here');
  }
  
  getCurrentMonth(): string {
    return new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  }
  
  getCurrentTime(): string {
    return new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  }
  
  getCalendarDays(): any[] {
    const today = new Date();
    const currentMonth = today.getMonth();
    const currentYear = today.getFullYear();
    const firstDay = new Date(currentYear, currentMonth, 1);
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - firstDay.getDay());
    
    const days = [];
    for (let i = 0; i < 42; i++) {
      const date = new Date(startDate);
      date.setDate(startDate.getDate() + i);
      days.push({
        date: date.getDate(),
        isToday: date.toDateString() === today.toDateString(),
        isOtherMonth: date.getMonth() !== currentMonth
      });
    }
    return days;
  }
}
