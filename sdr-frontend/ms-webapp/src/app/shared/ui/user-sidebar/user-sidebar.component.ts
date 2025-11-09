// import { Component, Input } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { MatCardModule } from '@angular/material/card';
// import { UserSummary } from '../../models/user';

// @Component({
//   selector: 'app-user-sidebar',
//   standalone: true,
//   imports: [CommonModule, MatCardModule],
//   template: `
//     <aside class="user-sidebar" *ngIf="profile as p">
//       <mat-card>
//         <div class="user-row">
//           <img class="avatar" [src]="p.avatarUrl || placeholder" alt="avatar" />
//           <div>
//             <div class="pseudo">{{ p.pseudo }}</div>
//             <div class="meta">{{ p.countryCode || '??' }}</div>
//           </div>
//         </div>

//         <div class="stats">
//           <div class="stat">
//             <div class="label">RDV</div>
//             <div class="value">{{ p.rdvCount ?? 0 }}</div>
//           </div>
//           <div class="stat">
//             <div class="label">Amis</div>
//             <div class="value">{{ p.friendsCount ?? 0 }}</div>
//           </div>
//         </div>
//       </mat-card>
//     </aside>
//   `,
// })
// export class UserSidebarComponent {
//   @Input() profile!: UserSummary | null;
//   placeholder = '/assets/placeholders/avatar.png';
// }
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserSummary } from '../../models/user';

@Component({
  selector: 'app-user-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-sidebar.component.html',
  styleUrls: ['./user-sidebar.component.scss'],
})
export class UserSidebarComponent {
  @Input() profile!: UserSummary | null;
  @Output() logout = new EventEmitter<void>();

  placeholder = '/assets/placeholders/avatar.png';

  // Valeurs temporaires tant que le backend ne les fournit pas
  fakeNextRdvDate = '2 | 12';
  fakeNextRdvTime = this.computeTime();

  private computeTime(): string {
    const now = new Date();
    const hh = String(now.getHours()).padStart(2, '0');
    const mm = String(now.getMinutes()).padStart(2, '0');
    return `${hh} H ${mm}`;
  }
}