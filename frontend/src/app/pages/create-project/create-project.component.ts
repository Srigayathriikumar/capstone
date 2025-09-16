import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TeamService } from '../../services/team.service';
import { ToastService } from '../../services/toast.service';
import { ToastComponent } from '../../components/toast/toast.component';

@Component({
  selector: 'app-create-project',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, ToastComponent],
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent implements OnInit {
  createProjectForm!: FormGroup;
  allMembers: any[] = [];
  filteredMembers: any[] = [];
  selectedMemberIds: number[] = [];
  memberSearchTerm = '';
  showMembersDropdown = false;
  currentUserId: number = 0;
  currentUserRole: string = '';
  managerProjects: any[] = [];
  availableTeamLeads: any[] = [];

  constructor(
    private fb: FormBuilder,
    private teamService: TeamService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.initializeUser();
    this.loadTeamMembers();
    this.loadAvailableTeamLeads();
    this.loadManagerProjects();
  }

  initializeForm(): void {
    this.createProjectForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      status: ['ACTIVE', Validators.required],
      teamLeadId: ['']
    });
  }
  
  initializeUser(): void {
    this.currentUserId = this.teamService.getCurrentUserId();
    this.currentUserRole = this.teamService.getCurrentUserRole();
  }

  loadTeamMembers(): void {
    this.teamService.getAllEmployees().subscribe({
      next: (employees) => {
        // Filter out managers and admins, keep only team members
        this.allMembers = (employees || []).filter(emp => 
          emp.role === 'TEAM_MEMBER' && emp.id !== this.currentUserId
        );
        this.filteredMembers = [...this.allMembers];
      },
      error: () => {
        this.allMembers = [
          { id: 6, username: 'arjun.dev', email: 'arjun@company.com', role: 'TEAM_MEMBER' },
          { id: 7, username: 'emily.dev', email: 'emily@company.com', role: 'TEAM_MEMBER' },
          { id: 9, username: 'sophia.test', email: 'sophia@company.com', role: 'TEAM_MEMBER' }
        ];
        this.filteredMembers = [...this.allMembers];
      }
    });
  }

  filterMembers(): void {
    this.showMembersDropdown = true;
    let members = [...this.allMembers];
    
    // Apply search filter
    if (this.memberSearchTerm.trim()) {
      const searchTerm = this.memberSearchTerm.toLowerCase();
      members = members.filter(member => 
        member.username.toLowerCase().includes(searchTerm) ||
        member.email.toLowerCase().includes(searchTerm)
      );
    }
    
    this.filteredMembers = members;
  }

  isMemberSelected(memberId: number): boolean {
    return this.selectedMemberIds.includes(memberId);
  }

  toggleMemberSelection(member: any): void {
    if (this.isMemberSelected(member.id)) {
      this.removeMember(member.id);
    } else {
      this.selectedMemberIds.push(member.id);
    }
  }

  removeMember(memberId: number): void {
    const index = this.selectedMemberIds.indexOf(memberId);
    if (index > -1) {
      this.selectedMemberIds.splice(index, 1);
    }
  }

  getMemberById(memberId: number): any {
    return this.allMembers.find(member => member.id === memberId);
  }
  
  toggleMembersDropdown(): void {
    this.showMembersDropdown = !this.showMembersDropdown;
  }
  
  clearAllMembers(): void {
    this.selectedMemberIds = [];
  }

  onCreateProject(): void {
    if (this.createProjectForm.valid) {
      const projectData = {
        name: this.createProjectForm.get('name')?.value,
        description: this.createProjectForm.get('description')?.value,
        status: this.createProjectForm.get('status')?.value,
        managerId: this.currentUserId, // Current user becomes the manager
        teamLeadId: this.createProjectForm.get('teamLeadId')?.value ? parseInt(this.createProjectForm.get('teamLeadId')?.value) : null,
        memberIds: this.selectedMemberIds
      };

      this.teamService.createProject(projectData).subscribe({
        next: (response) => {
          this.toastService.success('Success', `Project "${projectData.name}" created successfully! ${this.selectedMemberIds.length} members assigned.`);
          this.loadManagerProjects(); // Refresh projects data
          if (response && response.id) {
            this.router.navigate(['/team', response.id]);
          } else {
            this.goBack();
          }
        },
        error: (err) => {
          const errorMessage = err.error?.message || 'Failed to create project';
          this.toastService.error('Error', errorMessage);
        }
      });
    }
  }

  goBack(): void {
    window.history.back();
  }
  

  
  resetForm(): void {
    this.createProjectForm.reset({
      name: '',
      description: '',
      status: 'ACTIVE',
      teamLeadId: ''
    });
    this.selectedMemberIds = [];
    this.memberSearchTerm = '';
  }
  
  loadAvailableTeamLeads(): void {
    this.teamService.getAllTeamLeads().subscribe({
      next: (teamLeads) => {
        this.availableTeamLeads = teamLeads || [];
      },
      error: (err) => {
        console.error('Error loading team leads:', err);
        this.availableTeamLeads = [];
      }
    });
  }

  loadManagerProjects(): void {
    // Try to get projects by manager first
    this.teamService.getProjectsByManager(this.currentUserId).subscribe({
      next: (projects) => {
        this.managerProjects = projects || [];
        // If no projects found, try to get user's teams as fallback
        if (this.managerProjects.length === 0) {
          this.loadUserTeams();
        }
      },
      error: () => {
        // Fallback to user's teams
        this.loadUserTeams();
      }
    });
  }

  loadUserTeams(): void {
    this.teamService.getTeams().subscribe({
      next: (teams) => {
        this.managerProjects = teams || [];
      },
      error: () => {
        this.managerProjects = [];
      }
    });
  }

  // Projects Overview Methods
  getActiveProjectsCount(): number {
    return this.managerProjects.filter(p => p.status === 'ACTIVE').length;
  }

  getCompletedProjectsCount(): number {
    return this.managerProjects.filter(p => p.status === 'COMPLETED').length;
  }

  getTotalProjectsCount(): number {
    return this.managerProjects.length;
  }

  getRecentProjects(): any[] {
    return this.managerProjects
      .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
      .slice(0, 5)
      .map(project => ({
        name: project.name,
        description: project.description,
        status: project.status,
        managerName: 'You',
        memberCount: project.memberCount || 0
      }));
  }

  getStatusClass(status: string): string {
    return status.toLowerCase();
  }
}