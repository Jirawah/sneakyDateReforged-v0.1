import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './app-logo.component.html',
  styleUrls: ['./app-logo.component.scss'],
})
export class AppLogoComponent {
  /**
   * Où renvoyer au clic :
   * - "public" → page d’accueil non connectée "/"
   * - "app"    → page d’accueil connectée   "/home"
   */
  @Input() target: 'public' | 'app' = 'public';

  readonly logoSrc = '/assets/brand/sneakyDateReforged-Logo.svg';

  get targetRoute(): string {
    return this.target === 'app' ? '/home' : '/';
  }
}
