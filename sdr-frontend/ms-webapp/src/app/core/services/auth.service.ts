import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../../shared/models/auth';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'sdr_jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = environment.apiBaseUrl;

  private discordStatusUrl = `${this.base}${environment.discordStatusEndpoint}`;
  private discordPendingUrl = `${this.base}${environment.discordPendingEndpoint}`;

  // ---------------------------
  // LOGIN
  // ---------------------------
  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/login`, payload).pipe(
      tap(res => {
        if (!res?.token) throw new Error('Réponse de login invalide (pas de token).');
        sessionStorage.setItem(TOKEN_KEY, res.token);
      })
    );
  }

  // ---------------------------
  // REGISTER (NE stocke PAS le token)
  // ---------------------------
  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/auth/register`, payload);
  }

  // ---------------------------
  // DISCONNECT
  // ---------------------------
  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
  }

  get token(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }

  // ---------------------------
  // DISCORD LINKING FLOW
  // ---------------------------
  createDiscordPending(): Observable<{ state: string }> {
    return this.http.post<{ state: string }>(this.discordPendingUrl, {});
  }

  getDiscordStatusByState(
    state: string
  ): Observable<{ connected: boolean; discordPseudo: string | null; discordId: string | null }> {
    return this.http.get<{ connected: boolean; discordPseudo: string | null; discordId: string | null }>(
      this.discordStatusUrl,
      { params: { state } }
    );
  }

  getDiscordStatus(
    pseudo: string
  ): Observable<{ connected: boolean; discordPseudo: string | null; discordId: string | null }> {
    return this.http.get<{ connected: boolean; discordPseudo: string | null; discordId: string | null }>(
      this.discordStatusUrl,
      { params: { pseudo } }
    );
  }
}
