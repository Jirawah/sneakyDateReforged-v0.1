// import { Injectable, inject } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable, tap } from 'rxjs';
// import { LoginRequest, RegisterRequest, AuthResponse } from '../../shared/models/auth';
// import { environment } from '../../../environments/environment';

// const TOKEN_KEY = 'sdr_jwt';

// @Injectable({ providedIn: 'root' })
// export class AuthService {
//   private http = inject(HttpClient);
//   private base = environment.apiBaseUrl; // ex: http://localhost:8082

//   // ✅ URLs centralisées via environment
//   private discordStatusUrl  = `${this.base}${environment.discordStatusEndpoint}`;
//   private discordPendingUrl = `${this.base}${environment.discordPendingEndpoint}`;

//   login(payload: LoginRequest): Observable<AuthResponse> {
//     return this.http.post<AuthResponse>(`${this.base}/auth/login`, payload)
//       .pipe(tap(res => sessionStorage.setItem(TOKEN_KEY, res.token)));
//   }

//   register(payload: RegisterRequest): Observable<AuthResponse> {
//     return this.http.post<AuthResponse>(`${this.base}/auth/register`, payload)
//       .pipe(tap(res => sessionStorage.setItem(TOKEN_KEY, res.token)));
//   }

//   logout(): void { sessionStorage.removeItem(TOKEN_KEY); }
//   get token(): string | null { return sessionStorage.getItem(TOKEN_KEY); }
//   isAuthenticated(): boolean { return !!this.token; }

//   // ✅ (nouveau) crée un state côté back pour corréler bot <-> navigateur
//   createDiscordPending(): Observable<{ state: string }> {
//     return this.http.post<{ state: string }>(
//       this.discordPendingUrl,
//       {}
//     );
//   }

//   // ✅ (nouveau) interroge le statut par state
//   getDiscordStatusByState(state: string): Observable<{ connected: boolean; discordPseudo: string | null }> {
//     return this.http.get<{ connected: boolean; discordPseudo: string | null }>(
//       this.discordStatusUrl,
//       { params: { state } }
//     );
//   }

//   // (optionnel) legacy: statut par pseudo — garde-le si tu l’utilises encore
//   getDiscordStatus(pseudo: string): Observable<{ connected: boolean; discordPseudo: string | null }> {
//     return this.http.get<{ connected: boolean; discordPseudo: string | null }>(
//       this.discordStatusUrl,
//       { params: { pseudo } }
//     );
//   }
// }
// import { Injectable, inject } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable, tap } from 'rxjs';
// import { LoginRequest, RegisterRequest, AuthResponse } from '../../shared/models/auth';
// import { environment } from '../../../environments/environment';

// const TOKEN_KEY = 'sdr_jwt';

// @Injectable({ providedIn: 'root' })
// export class AuthService {
//   private http = inject(HttpClient);

//   // ex: http://localhost:8082
//   private base = environment.apiBaseUrl;

//   // ex: /api/auth/discord/status et /api/auth/discord/pending
//   private discordStatusUrl  = `${this.base}${environment.discordStatusEndpoint}`;
//   private discordPendingUrl = `${this.base}${environment.discordPendingEndpoint}`;

//   // --------------------
//   // AUTH CLASSIQUE
//   // --------------------

//   login(payload: LoginRequest): Observable<AuthResponse> {
//     return this.http.post<AuthResponse>(`${this.base}/auth/login`, payload)
//       .pipe(
//         tap(res => {
//           // on stocke le JWT retourné par ms-auth pour la session
//           sessionStorage.setItem(TOKEN_KEY, res.token);
//         })
//       );
//   }

//   register(payload: RegisterRequest): Observable<AuthResponse> {
//     return this.http.post<AuthResponse>(`${this.base}/auth/register`, payload)
//       .pipe(
//         tap(res => {
//           // le backend renvoie déjà un token à l'inscription
//           sessionStorage.setItem(TOKEN_KEY, res.token);
//         })
//       );
//   }

//   logout(): void {
//     sessionStorage.removeItem(TOKEN_KEY);
//   }

//   get token(): string | null {
//     return sessionStorage.getItem(TOKEN_KEY);
//   }

//   isAuthenticated(): boolean {
//     return !!this.token;
//   }

//   // --------------------
//   // FLUX DISCORD
//   // --------------------

//   /**
//    * Demande au backend de créer un "state" et de le mémoriser.
//    * -> POST /api/auth/discord/pending
//    * <- { state: "uuid..." }
//    */
//   createDiscordPending(): Observable<{ state: string }> {
//     return this.http.post<{ state: string }>(
//       this.discordPendingUrl,
//       {}
//     );
//   }

//   /**
//    * Vérifie si l'état 'state' a été marqué "connected" côté backend.
//    * Le backend renvoie aussi le pseudo Discord détecté + l'id Discord
//    * pour qu'on les réutilise dans l'inscription.
//    *
//    * -> GET /api/auth/discord/status?state=...
//    * <- { connected: boolean, discordPseudo: string|null, discordId: string|null }
//    */
//   getDiscordStatusByState(state: string): Observable<{
//     connected: boolean;
//     discordPseudo: string | null;
//     discordId: string | null;
//   }> {
//     return this.http.get<{
//       connected: boolean;
//       discordPseudo: string | null;
//       discordId: string | null;
//     }>(
//       this.discordStatusUrl,
//       { params: { state } }
//     );
//   }

//   /**
//    * (optionnel/legacy) statut par pseudo si tu en as encore besoin ailleurs.
//    */
//   getDiscordStatus(pseudo: string): Observable<{
//     connected: boolean;
//     discordPseudo: string | null;
//     discordId: string | null;
//   }> {
//     return this.http.get<{
//       connected: boolean;
//       discordPseudo: string | null;
//       discordId: string | null;
//     }>(
//       this.discordStatusUrl,
//       { params: { pseudo } }
//     );
//   }
// }
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse
} from '../../shared/models/auth';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'sdr_jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  // ex: http://localhost:8082
  private base = environment.apiBaseUrl;

  // ex: /api/auth/discord/status etc.
  private discordStatusUrl = `${this.base}${environment.discordStatusEndpoint}`;
  private discordPendingUrl = `${this.base}${environment.discordPendingEndpoint}`;

  // ---------------------------
  // LOGIN
  // ---------------------------
  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/auth/login`, payload)
      .pipe(
        tap(res => {
          // ici on garde le token -> c'est le moment normal pour "connecter"
          sessionStorage.setItem(TOKEN_KEY, res.token);
        })
      );
  }

  // ---------------------------
  // REGISTER
  // ---------------------------
  register(payload: RegisterRequest): Observable<AuthResponse> {
    // ❗ très important :
    // PAS de .tap(...) ici -> on NE stocke PAS le token lors du register,
    // comme ça l'utilisateur n'est pas considéré "authentifié" tout de suite.
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

  // 1) Demande au backend un "state" unique pour cette tentative de liaison Discord
  createDiscordPending(): Observable<{ state: string }> {
    return this.http.post<{ state: string }>(
      this.discordPendingUrl,
      {}
    );
  }

  // 2) Polling: "est-ce que l'utilisateur a bien rejoint le vocal Discord ?"
  //    Le backend renvoie maintenant:
  //    { connected: boolean, discordPseudo: string|null, discordId: string|null }
  getDiscordStatusByState(
    state: string
  ): Observable<{ connected: boolean; discordPseudo: string | null; discordId: string | null }> {
    return this.http.get<{
      connected: boolean;
      discordPseudo: string | null;
      discordId: string | null;
    }>(this.discordStatusUrl, {
      params: { state }
    });
  }

  // (legacy) Polling par pseudo Discord, on le garde si tu l'utilises encore ailleurs
  getDiscordStatus(
    pseudo: string
  ): Observable<{ connected: boolean; discordPseudo: string | null; discordId: string | null }> {
    return this.http.get<{
      connected: boolean;
      discordPseudo: string | null;
      discordId: string | null;
    }>(this.discordStatusUrl, {
      params: { pseudo }
    });
  }
}

