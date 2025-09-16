import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../../services/loading.service';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (loadingService.isLoading()) {
      <div class="loader-overlay">
        <div class="loader-content">
          <div class="loader-spinner"></div>
          <p class="loader-text">Just a moment...</p>
        </div>
      </div>
    }
  `,
  styles: [`
    .loader-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      background: rgba(255, 255, 255, 0.85);
      backdrop-filter: blur(8px);
      -webkit-backdrop-filter: blur(8px);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 9999;
    }
    
    .loader-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
    }
    
    .loader-spinner {
      width: 80px;
      height: 80px;
      background-image: url('/loader.png');
      background-size: contain;
      background-repeat: no-repeat;
      background-position: center;
      animation: spin 1s linear infinite;
    }
    
    .loader-text {
      margin: 0;
      font-size: 16px;
      color: #64748b;
      font-weight: 500;
    }
    
    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }
  `]
})
export class LoaderComponent {
  protected loadingService = inject(LoadingService);
}