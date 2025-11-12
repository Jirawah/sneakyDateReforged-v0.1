// // import { Component, inject } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { Observable, catchError, of } from 'rxjs';
// // import { ProfileService } from '../../core/services/profile.service';
// // import { ProfileDetails } from '../../shared/models/profile-details';

// // @Component({
// //   selector: 'app-profile',
// //   standalone: true,
// //   imports: [CommonModule],
// //   templateUrl: './profile.component.html',
// //   styleUrls: ['./profile.component.scss'],
// // })
// // export class ProfileComponent {
// //   private profileService = inject(ProfileService);

// //   details$: Observable<ProfileDetails> = this.profileService.getMyProfileDetails().pipe(
// //     catchError(() => of({
// //       discord: { pseudo: '—', avatarUrl: null },
// //       steam:   { pseudo: '—', avatarUrl: null },
// //       games:   []
// //     }))
// //   );

// //   placeholder = '/assets/placeholders/avatar.png';
// // }
// import { Component, inject } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { Observable, catchError, of } from 'rxjs';

// import { ProfileService } from '../../core/services/profile.service';
// import { AuthService } from '../../core/services/auth.service';

// import { ProfileDetails } from '../../shared/models/profile-details';
// import { UserSummary } from '../../shared/models/user';
// import { Friend } from '../../shared/models/friend';

// import { UserSidebarComponent } from '../../shared/ui/user-sidebar/user-sidebar.component';
// import { FriendsListsComponent } from '../../shared/ui/friends-lists/friends-lists.component';

// @Component({
//   selector: 'app-profile',
//   standalone: true,
//   imports: [CommonModule, UserSidebarComponent, FriendsListsComponent],
//   templateUrl: './profile.component.html',
//   styleUrls: ['./profile.component.scss'],
// })
// export class ProfileComponent {
//   private profileService = inject(ProfileService);
//   private auth = inject(AuthService);

//   // Aside gauche
//   profile$: Observable<UserSummary> = this.profileService.getUserSummaryForAside();

//   // Centre (déjà présent)
//   details$: Observable<ProfileDetails> = this.profileService.getMyProfileDetails().pipe(
//     catchError(() => of({
//       discord: { pseudo: '—', avatarUrl: null },
//       steam:   { pseudo: '—', avatarUrl: null },
//       games:   []
//     }))
//   );

//   // Aside droite (dummy pour l’instant)
//   friends: Friend[] = [
//     { id: 2, pseudo: 'Teammate1' },
//     { id: 3, pseudo: 'Teammate2' },
//   ];
//   invitations: Friend[] = [
//     { id: 9, pseudo: 'NewGuy' },
//   ];

//   placeholder = '/assets/placeholders/avatar.png';

//   onLogout(): void { this.auth.logout(); }
//   onAccept(_f: Friend) { /* TODO: brancher service */ }
//   onDecline(_f: Friend) { /* TODO: brancher service */ }
// }
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable, catchError, of } from 'rxjs';

import { ProfileService } from '../../core/services/profile.service';
import { AuthService } from '../../core/services/auth.service';

import { ProfileDetails } from '../../shared/models/profile-details';
import { UserSummary } from '../../shared/models/user';
import { Friend } from '../../shared/models/friend';

import { UserSidebarComponent } from '../../shared/ui/user-sidebar/user-sidebar.component';
import { FriendsListsComponent } from '../../shared/ui/friends-lists/friends-lists.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, UserSidebarComponent, FriendsListsComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent {
  private profileService = inject(ProfileService);
  private auth = inject(AuthService);

  // Aside gauche
  profile$: Observable<UserSummary> = this.profileService.getUserSummaryForAside();

  // Centre
  details$: Observable<ProfileDetails> = this.profileService.getMyProfileDetails().pipe(
    catchError(() => of({
      discord: { pseudo: '—', avatarUrl: null },
      steam:   { pseudo: '—', avatarUrl: null },
      games:   []
    }))
  );

  // Aside droite : 10 amis pour forcer le scroll
  friends: Friend[] = [
    { id: 2,  pseudo: 'AlphaCrest',    online: true  },
    { id: 3,  pseudo: 'CodeXplorer',   online: true  },
    { id: 4,  pseudo: 'BugHunter666',  online: true  },
    { id: 5,  pseudo: 'DevNinja',      online: true  },
    { id: 6,  pseudo: 'Compilatio759', online: false },
    { id: 7,  pseudo: 'ScriptedByte',  online: false },
    { id: 8,  pseudo: 'Teammate7',     online: false },
    { id: 9,  pseudo: 'Teammate8',     online: true  },
    { id: 10, pseudo: 'Teammate9',     online: false },
    { id: 11, pseudo: 'Teammate10',    online: true  },
  ];

  invitations: Friend[] = []; // non utilisé pour l’instant

  placeholder = '/assets/placeholders/avatar.png';

  onLogout(): void { this.auth.logout(); }
  onAccept(_f: Friend) { /* TODO */ }
  onDecline(_f: Friend) { /* TODO */ }
}
