// src/app/core/services/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../../shared/models/auth';
import { environment } from '../../../envoronments/environment';

const TOKEN_KEY = 'sdr_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  /** Ex: http://localhost:8082 (défini dans environment.ts) */
  private readonly base = environment.msAuthApi;

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/login`, payload).pipe(
      tap(res => this.setToken(res.token))
    );
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/register`, payload).pipe(
      tap(res => this.setToken(res.token))
    );
  }

  /** Helpers token */
  private setToken(t: string) { localStorage.setItem(TOKEN_KEY, t); }
  get token(): string | null { return localStorage.getItem(TOKEN_KEY); }
  isAuthenticated(): boolean { return !!this.token; }
  logout(): void { localStorage.removeItem(TOKEN_KEY); }
}
