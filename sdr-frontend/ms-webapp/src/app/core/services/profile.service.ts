// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable, map, catchError, of, shareReplay } from 'rxjs';
// import { environment } from '../../../environments/environment';
// import { AggregatedProfile } from '../../shared/models/profile';
// import { UserSummary } from '../../shared/models/user';
// import { ProfileDetails } from '../../shared/models/profile-details';

// @Injectable({ providedIn: 'root' })
// export class ProfileService {
//   private readonly base = environment.profileApiUrl;

//   constructor(private http: HttpClient) { }

//   /** Vue agrégée de l'utilisateur courant (JWT requis). */
//   getMeFull(): Observable<AggregatedProfile> {
//     return this.http
//       .get<AggregatedProfile>(`${this.base}/profiles/me/full`)
//       .pipe(shareReplay(1));
//   }

//   /** Adapte la vue agrégée au format de ton aside UserSummary */
//   getUserSummaryForAside(): Observable<UserSummary> {
//     const fallback: UserSummary = {
//       id: 0,
//       pseudo: '...',
//       avatarUrl: undefined,
//       countryCode: null,
//       rdvCount: 0,
//       friendsCount: 0
//     };

//     return this.getMeFull().pipe(
//       map(p => ({
//         id: p.userId,
//         pseudo: p.pseudo ?? '...',
//         avatarUrl: p.avatarUrl ?? undefined,
//         countryCode: null,
//         rdvCount: p.nombreRDVs ?? 0,
//         friendsCount: p.nombreAmis ?? 0
//       })),
//       catchError(() => of(fallback))
//     );
//   }
// }
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of, shareReplay } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AggregatedProfile } from '../../shared/models/profile';
import { UserSummary } from '../../shared/models/user';
import { ProfileDetails } from '../../shared/models/profile-details';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  // ← on reste aligné avec ton service actuel
  private readonly base = environment.profileApiUrl;

  constructor(private http: HttpClient) { }

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
      avatarUrl: undefined,
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

  /** Détails pour la page "MON PROFIL" : Discord + Steam + liste des jeux. */
  getMyProfileDetails(): Observable<ProfileDetails> {
    const fallback: ProfileDetails = {
      discord: { pseudo: '—', avatarUrl: null },
      steam:   { pseudo: '—', avatarUrl: null },
      games:   []
    };

    return this.http.get<ProfileDetails>(`${this.base}/profiles/me/details`).pipe(
      // Normalise la réponse pour éviter les null/undefined côté template
      map(res => ({
        discord: { pseudo: res.discord?.pseudo ?? '—', avatarUrl: res.discord?.avatarUrl ?? null },
        steam:   { pseudo: res.steam?.pseudo   ?? '—', avatarUrl: res.steam?.avatarUrl   ?? null },
        games:   Array.isArray(res.games) ? res.games : []
      })),
      catchError(() => of(fallback)),
      shareReplay(1)
    );
  }

  // (Optionnel) Si l'endpoint /profiles/me/details n'est pas prêt,
  // tu peux temporairement dériver depuis getMeFull()
  // getMyProfileDetailsFromFull(): Observable<ProfileDetails> {
  //   return this.getMeFull().pipe(
  //     map(full => ({
  //       discord: { pseudo: full.discordPseudo ?? '—', avatarUrl: full.discordAvatarUrl ?? null },
  //       steam:   { pseudo: full.steamPseudo   ?? '—', avatarUrl: full.steamAvatarUrl   ?? null },
  //       games:   full.games?.map(g => ({ name: g.name, hours: g.hours })) ?? []
  //     })),
  //     shareReplay(1)
  //   );
  // }
}
