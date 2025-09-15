import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div [class]="getCardClasses()">
      <div *ngIf="title || headerActions" class="card-header">
        <h3 *ngIf="title" class="card-title">{{ title }}</h3>
        <div *ngIf="headerActions" class="card-actions">
          <ng-content select="[slot=header-actions]"></ng-content>
        </div>
      </div>
      <div class="card-content">
        <ng-content></ng-content>
      </div>
      <div *ngIf="footer" class="card-footer">
        <ng-content select="[slot=footer]"></ng-content>
      </div>
    </div>
  `,
  styleUrls: ['./card.component.css']
})
export class CardComponent {
  @Input() title: string = '';
  @Input() variant: 'default' | 'elevated' | 'outlined' | 'glass' = 'default';
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() hoverable: boolean = false;
  @Input() headerActions: boolean = false;
  @Input() footer: boolean = false;

  getCardClasses(): string {
    const classes = [
      'card',
      `card-${this.variant}`,
      `card-${this.size}`,
      this.hoverable ? 'card-hoverable' : ''
    ].filter(Boolean);
    
    return classes.join(' ');
  }
}
