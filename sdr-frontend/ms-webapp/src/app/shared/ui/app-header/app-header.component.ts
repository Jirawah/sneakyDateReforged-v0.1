import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppLogoComponent } from '../app-logo/app-logo.component';
import { ClockComponent } from '../clock/clock.component';

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
