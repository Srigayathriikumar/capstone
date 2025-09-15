import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

export interface NavItem {
  label: string;
  icon: string;
  route?: string;
  children?: NavItem[];
  badge?: string;
  badgeColor?: string;
  active?: boolean;
}

export interface NavSection {
  title: string;
  items: NavItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  @Input() brandName = 'TRAM';
  @Input() brandIcon = 'pi pi-sitemap';
  @Input() navSections: NavSection[] = [];
  @Input() currentUser: any = null;
  @Input() isCollapsed = false;
  @Output() toggleCollapse = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();

  onLogout(): void {
    this.logout.emit();
  }

  onToggleCollapse(): void {
    this.toggleCollapse.emit();
  }
}
