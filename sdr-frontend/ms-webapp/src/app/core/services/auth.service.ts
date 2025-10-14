import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../../shared/models/auth';

const TOKEN_KEY = 'sdr_jwt';
const AUTH_API  = 'http://localhost:8082/auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${AUTH_API}/login`, payload).pipe(
      tap(res => localStorage.setItem(TOKEN_KEY, res.token))
    );
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${AUTH_API}/register`, payload).pipe(
      tap(res => localStorage.setItem(TOKEN_KEY, res.token))
    );
  }

  logout(): void { localStorage.removeItem(TOKEN_KEY); }
  get token(): string | null { return localStorage.getItem(TOKEN_KEY); }
  isAuthenticated(): boolean { return !!this.token; }
}
