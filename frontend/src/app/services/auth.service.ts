import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface User {
  id?: number;
  username: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    if (token) {
      this.loadUserFromToken(token);
    }
  }

  login(credentials: LoginRequest): Observable<boolean> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        map(response => {
          if (response.token) {
            localStorage.setItem('token', response.token);
            this.loadUserFromToken(response.token);
            return true;
          }
          return false;
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    return !!token && !this.isTokenExpired(token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  private loadUserFromToken(token: string): void {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log('Token payload:', payload);
      
      let role = 'USER';
      const username = payload.sub || payload.username;
      
      // Check username-based roles
      if (username === 'superadmin') {
        role = 'SUPER_ADMIN';
      } else if (username === 'rajesh.admin') {
        role = 'ADMIN';
      } else if (username.includes('.manager')) {
        role = 'PROJECT_MANAGER';
      } else if (username.includes('.teamlead')) {
        role = 'TEAMLEAD';
      } else {
        role = 'USER';
      }
      
      this.currentUserSubject.next({
        id: payload.userId || payload.id || 1,
        username: username,
        role: role
      });
      
      console.log('User loaded:', { username, role });
    } catch (error) {
      console.error('Error parsing token:', error);
      this.currentUserSubject.next({
        id: 1,
        username: 'Unknown',
        role: 'USER'
      });
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }
}