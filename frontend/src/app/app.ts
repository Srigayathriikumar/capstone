import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoaderComponent } from './components/ui/loader/loader.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, LoaderComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('tramfrontend');
  
  ngOnInit(): void {
    this.checkThemeApplication();
  }
  
  private checkThemeApplication(): void {
    setTimeout(() => {
      const primaryColor = getComputedStyle(document.documentElement).getPropertyValue('--primary-color');
      const surfaceColor = getComputedStyle(document.documentElement).getPropertyValue('--surface-0');
      
      if (primaryColor && surfaceColor) {
        console.log('✅ PrimeNG Lara Theme Applied Successfully!');
        console.log('Primary Color:', primaryColor.trim());
        console.log('Surface Color:', surfaceColor.trim());
      } else {
        console.log('❌ PrimeNG Lara Theme NOT Applied - CSS variables not found');
      }
    }, 100);
  }
}
