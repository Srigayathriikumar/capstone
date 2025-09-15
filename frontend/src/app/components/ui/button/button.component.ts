import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button 
      [class]="getButtonClasses()"
      [disabled]="disabled"
      (click)="onClick.emit($event)"
      [type]="type"
    >
      <span *ngIf="icon" class="button-icon">{{ icon }}</span>
      <span *ngIf="loading" class="button-spinner">⏳</span>
      <span class="button-text">{{ text }}</span>
    </button>
  `,
  styleUrls: ['./button.component.css']
})
export class ButtonComponent {
  @Input() text: string = '';
  @Input() variant: 'primary' | 'secondary' | 'danger' | 'success' | 'warning' | 'ghost' = 'primary';
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() disabled: boolean = false;
  @Input() loading: boolean = false;
  @Input() icon: string = '';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() fullWidth: boolean = false;
  @Output() onClick = new EventEmitter<Event>();

  getButtonClasses(): string {
    const classes = [
      'btn',
      `btn-${this.variant}`,
      `btn-${this.size}`,
      this.fullWidth ? 'btn-full-width' : '',
      this.disabled ? 'btn-disabled' : '',
      this.loading ? 'btn-loading' : ''
    ].filter(Boolean);
    
    return classes.join(' ');
  }
}
