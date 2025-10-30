import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppLogoComponent } from '../app-logo/app-logo.component'; // tu l'as déjà
import { ClockComponent } from '../clock/clock.component';          // tu l’as déjà (ou ajuste le chemin)

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, AppLogoComponent, ClockComponent],
  template: `
    <header class="app-header">
      <app-logo [target]="target"></app-logo>
      <div class="app-header__spacer"></div>
      <app-clock></app-clock>
    </header>
  `,
})
export class AppHeaderComponent {
  /** "app" = /home ; "public" = / */
  @Input() target: 'app' | 'public' = 'app';
}
