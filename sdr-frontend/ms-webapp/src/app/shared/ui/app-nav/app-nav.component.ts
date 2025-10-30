import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule],
  template: `
    <nav class="app-nav">
      <a routerLink="/home" class="app-nav__link">Accueil</a>
      <a routerLink="/rdv" class="app-nav__link">Mes RDV</a>
      <a routerLink="/friends" class="app-nav__link">Amis</a>

      <span class="app-nav__spacer"></span>

      <!-- Bouton Dark Mode (fictif pour le moment) -->
      <button mat-stroked-button type="button">
        DARK MODE
      </button>
    </nav>
  `,
})
export class AppNavComponent {}
