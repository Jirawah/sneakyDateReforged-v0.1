import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../../shared/models/auth';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'sdr_jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = environment.msAuthApi; // ex: http://localhost:8082

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/login`, payload)
      .pipe(tap(res => sessionStorage.setItem(TOKEN_KEY, res.token)));
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/register`, payload)
      .pipe(tap(res => sessionStorage.setItem(TOKEN_KEY, res.token)));
  }

  logout(): void { sessionStorage.removeItem(TOKEN_KEY); }
  get token(): string | null { return sessionStorage.getItem(TOKEN_KEY); }
  isAuthenticated(): boolean { return !!this.token; }
}
