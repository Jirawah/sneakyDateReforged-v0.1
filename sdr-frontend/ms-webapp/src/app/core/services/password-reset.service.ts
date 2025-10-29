import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PasswordResetService {
  private http = inject(HttpClient);
  private base = environment.apiBaseUrl;

  /**
   * Étape A :
   * Appel POST /auth/reset-request
   * Body attendu par le back : { email: string }
   */
  requestReset(email: string): Observable<any> {
    return this.http.post<any>(`${this.base}/auth/reset-request`, { email });
  }

  /**
   * Étape B :
   * Appel POST /auth/reset-password
   * Body attendu par le back :
   * { token: string, newPassword: string }
   */
  confirmReset(token: string, newPassword: string): Observable<any> {
    return this.http.post<any>(`${this.base}/auth/reset-password`, {
      token,
      newPassword,
    });
  }
}
