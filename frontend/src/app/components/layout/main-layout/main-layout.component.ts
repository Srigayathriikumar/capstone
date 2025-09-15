import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent, NavSection } from '../sidebar/sidebar.component';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent, NavbarComponent],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {
  @Input() currentUser: any = null;
  @Input() breadcrumbs: string[] = [];
  @Input() showSearch = true;
  @Input() searchPlaceholder = 'Type here...';
  @Input() navSections: NavSection[] = [];
  @Input() brandName = 'TRAM';
  @Input() brandIcon = 'pi pi-sitemap';
  @Output() search = new EventEmitter<string>();
  @Output() logout = new EventEmitter<void>();
  @Output() userMenuClick = new EventEmitter<void>();

  sidebarCollapsed = false;
  sidebarOpen = false;

  onToggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  onToggleMobileSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  onSearch(searchTerm: string): void {
    this.search.emit(searchTerm);
  }

  onLogout(): void {
    this.logout.emit();
  }

  onUserMenuClick(): void {
    this.userMenuClick.emit();
  }
}
