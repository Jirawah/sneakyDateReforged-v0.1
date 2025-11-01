import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of, shareReplay } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AggregatedProfile } from '../../shared/models/profile';
import { UserSummary } from '../../shared/models/user';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly base = environment.profileApiUrl;

  constructor(private http: HttpClient) {}

  /** Vue agrégée de l'utilisateur courant (JWT requis). */
  getMeFull(): Observable<AggregatedProfile> {
    return this.http
      .get<AggregatedProfile>(`${this.base}/profiles/me/full`)
      .pipe(shareReplay(1));
  }

  /** Adapte la vue agrégée au format de ton aside UserSummary */
  getUserSummaryForAside(): Observable<UserSummary> {
    const fallback: UserSummary = {
      id: 0,
      pseudo: '...',
      avatarUrl: undefined,   // laisse le composant afficher son placeholder
      countryCode: null,
      rdvCount: 0,
      friendsCount: 0
    };

    return this.getMeFull().pipe(
      map(p => ({
        id: p.userId,
        pseudo: p.pseudo ?? '...',
        avatarUrl: p.avatarUrl ?? undefined,
        countryCode: null,
        rdvCount: p.nombreRDVs ?? 0,
        friendsCount: p.nombreAmis ?? 0
      })),
      catchError(() => of(fallback))
    );
  }
}
