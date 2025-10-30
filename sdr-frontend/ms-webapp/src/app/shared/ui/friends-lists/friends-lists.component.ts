import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { Friend } from '../../models/friend';

@Component({
  selector: 'app-friends-lists',
  standalone: true,
  imports: [CommonModule, MatListModule, MatButtonModule],
  template: `
    <section class="friends-lists">
      <h3>Mes amis</h3>
      <mat-nav-list>
        <a mat-list-item *ngFor="let f of friends">{{ f.pseudo }}</a>
      </mat-nav-list>

      <h3>Invitations</h3>
      <div class="invitation" *ngFor="let inv of invitations">
        <span>{{ inv.pseudo }}</span>
        <span class="spacer"></span>
        <button mat-stroked-button (click)="accept.emit(inv)">Accepter</button>
        <button mat-button color="warn" (click)="decline.emit(inv)">Refuser</button>
      </div>
    </section>
  `,
})
export class FriendsListsComponent {
  @Input() friends: Friend[] = [];
  @Input() invitations: Friend[] = [];

  @Output() accept = new EventEmitter<Friend>();
  @Output() decline = new EventEmitter<Friend>();
}
