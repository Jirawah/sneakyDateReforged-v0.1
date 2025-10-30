import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AppHeaderComponent } from '../../shared/ui/app-header/app-header.component';
import { AppNavComponent } from '../../shared/ui/app-nav/app-nav.component';
import { UserSidebarComponent } from '../../shared/ui/user-sidebar/user-sidebar.component';
import { FriendsListsComponent } from '../../shared/ui/friends-lists/friends-lists.component';

import { UserSummary } from '../../shared/models/user';
import { Friend } from '../../shared/models/friend';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    AppHeaderComponent,
    AppNavComponent,
    UserSidebarComponent,
    FriendsListsComponent
  ],
  templateUrl: './app-home.component.html',
  styleUrls: ['./app-home.component.scss'],
})
export class AppHomeComponent {
  // 🔹 Données mock pour l’instant (on branchera ms-profil / ms-friend ensuite)
  profile: UserSummary | null = {
    id: 1,
    pseudo: 'AlwaysFailed',
    avatarUrl: null,
    countryCode: 'FR',
    rdvCount: 12,
    friendsCount: 5,
  };

  friends: Friend[] = [
    { id: 2, pseudo: 'Teammate1' },
    { id: 3, pseudo: 'Teammate2' },
    { id: 4, pseudo: 'Teammate3' },
  ];

  invitations: Friend[] = [
    { id: 9, pseudo: 'NewGuy' },
    { id: 10, pseudo: 'AnotherOne' },
  ];

  // 🔹 Callbacks (brancheront des services plus tard)
  onAccept(friend: Friend) {
    console.log('[INVITE] accept', friend);
    // TODO: appel ms-friend (PATCH/POST) puis refresh listes
  }

  onDecline(friend: Friend) {
    console.log('[INVITE] decline', friend);
    // TODO: appel ms-friend (PATCH/POST) puis refresh listes
  }
}
