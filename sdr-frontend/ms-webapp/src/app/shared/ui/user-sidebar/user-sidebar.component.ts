import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { UserSummary } from '../../models/user';

@Component({
  selector: 'app-user-sidebar',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <aside class="user-sidebar" *ngIf="profile as p">
      <mat-card>
        <div class="user-row">
          <img class="avatar" [src]="p.avatarUrl || placeholder" alt="avatar" />
          <div>
            <div class="pseudo">{{ p.pseudo }}</div>
            <div class="meta">{{ p.countryCode || '??' }}</div>
          </div>
        </div>

        <div class="stats">
          <div class="stat">
            <div class="label">RDV</div>
            <div class="value">{{ p.rdvCount ?? 0 }}</div>
          </div>
          <div class="stat">
            <div class="label">Amis</div>
            <div class="value">{{ p.friendsCount ?? 0 }}</div>
          </div>
        </div>
      </mat-card>
    </aside>
  `,
})
export class UserSidebarComponent {
  @Input() profile!: UserSummary | null;
  placeholder = '/assets/placeholders/avatar.png'; // mets une image si tu veux
}
