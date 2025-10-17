import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../../shared/models/auth';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'sdr_jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = environment.apiBaseUrl; // ex: http://localhost:8082
  private discordStatusUrl = `${this.base}${environment.discordStatusEndpoint}`;

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

  // getDiscordStatus(pseudo: string) {
  //   return this.http.get<{ connected: boolean; profile?: any }>(
  //     `${this.base}/auth/discord/status`,
  //     { params: { pseudo } }
  //   );
  // }
  getDiscordStatus(pseudo: string) {
    return this.http.get<{ connected: boolean }>(this.discordStatusUrl, { params: { pseudo } });
  }
}
