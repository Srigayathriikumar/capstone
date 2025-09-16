import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { TeamService } from '../services/team.service';
import { LoadingService } from '../services/loading.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  loginForm: FormGroup;
  loading = false;
  error = '';

  private loadingService = inject(LoadingService);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private teamService: TeamService
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading = true;
      this.error = '';
      
      this.authService.login(this.loginForm.value).subscribe({
        next: (success) => {
          if (success) {
            this.loadingService.show();
            this.teamService.getTeams().subscribe({
              next: (teams) => {
                if (teams && teams.length > 0) {
                  setTimeout(() => {
                    this.loadingService.hide();
                    this.router.navigate(['/team', teams[0].id]);
                  }, 2000);
                } else {
                  this.loadingService.hide();
                  this.error = 'No teams available';
                  this.loading = false;
                }
              },
              error: () => {
                this.loadingService.hide();
                this.error = 'Failed to load teams';
                this.loading = false;
              }
            });
          } else {
            this.error = 'Invalid credentials';
            this.loading = false;
          }
        },
        error: (err) => {
          this.error = 'Login failed. Please try again.';
          this.loading = false;
        }
      });
    }
  }
}
