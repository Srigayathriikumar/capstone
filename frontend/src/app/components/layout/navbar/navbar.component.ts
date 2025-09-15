import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  @Input() breadcrumbs: string[] = [];
  @Input() currentUser: any = null;
  @Input() showSearch = true;
  @Input() searchPlaceholder = 'Type here...';
  @Input() showProjectSwitcher = false;
  @Input() currentProject: any = null;
  @Input() allProjects: any[] = [];
  @Output() search = new EventEmitter<string>();
  @Output() toggleSidebar = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();
  @Output() userMenuClick = new EventEmitter<void>();
  @Output() projectSelected = new EventEmitter<any>();

  searchTerm = '';
  showProjectSwitcherModal = false;

  onSearch(): void {
    this.search.emit(this.searchTerm);
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  onLogout(): void {
    this.logout.emit();
  }

  onUserMenuClick(): void {
    this.userMenuClick.emit();
  }

  toggleProjectSwitcher(): void {
    this.showProjectSwitcherModal = !this.showProjectSwitcherModal;
  }

  closeProjectSwitcher(): void {
    this.showProjectSwitcherModal = false;
  }

  selectProject(project: any): void {
    this.projectSelected.emit(project);
    this.closeProjectSwitcher();
  }

  getStatusClass(status: string): string {
    return status?.toLowerCase() || 'inactive';
  }
}
