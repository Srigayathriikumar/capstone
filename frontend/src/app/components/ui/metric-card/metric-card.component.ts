import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-metric-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './metric-card.component.html',
  styleUrl: './metric-card.component.css'
})
export class MetricCardComponent {
  @Input() title = '';
  @Input() value = '';
  @Input() change = '';
  @Input() changeType: 'positive' | 'negative' | 'neutral' = 'neutral';
  @Input() icon = '';
  @Input() color: 'purple' | 'blue' | 'teal' | 'orange' | 'green' | 'red' | 'pink' | 'indigo' = 'purple';
  @Input() loading = false;

  getChangeIcon(): string {
    switch (this.changeType) {
      case 'positive':
        return 'pi pi-arrow-up';
      case 'negative':
        return 'pi pi-arrow-down';
      default:
        return 'pi pi-minus';
    }
  }
}
