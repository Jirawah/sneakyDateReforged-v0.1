import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';

import { AppHeaderComponent } from '../../shared/ui/app-header/app-header.component';
// import { AppNavComponent } from '../../shared/ui/app-nav/app-nav.component';
import { UserSidebarComponent } from '../../shared/ui/user-sidebar/user-sidebar.component';
import { FriendsListsComponent } from '../../shared/ui/friends-lists/friends-lists.component';

import { UserSummary } from '../../shared/models/user';
import { Friend } from '../../shared/models/friend';
import { ProfileService } from '../../core/services/profile.service';
import { AuthService } from '../../core/services/auth.service'; 

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    AppHeaderComponent,
    //AppNavComponent,
    UserSidebarComponent,
    FriendsListsComponent
  ],
  templateUrl: './app-home.component.html',
  styleUrls: ['./app-home.component.scss'],
})
export class AppHomeComponent {
  private profileService = inject(ProfileService);
  private auth = inject(AuthService);
  profile$!: Observable<UserSummary>;

  friends: Friend[] = [
    { id: 2, pseudo: 'Teammate1' },
    { id: 3, pseudo: 'Teammate2' },
    { id: 4, pseudo: 'Teammate3' },
  ];
  invitations: Friend[] = [
    { id: 9, pseudo: 'NewGuy' },
    { id: 10, pseudo: 'AnotherOne' },
  ];

  constructor() {
    this.profile$ = this.profileService.getUserSummaryForAside();
  }

  onAccept(friend: Friend) {/* TODO */ }
  onDecline(friend: Friend) {/* TODO */ }

  onLogout(): void {
    this.auth.logout(); // ⬅️ vide le token et redirige vers '/'
  }
}

