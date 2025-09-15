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
  @Output() search = new EventEmitter<string>();
  @Output() toggleSidebar = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();
  @Output() userMenuClick = new EventEmitter<void>();

  searchTerm = '';

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
}
