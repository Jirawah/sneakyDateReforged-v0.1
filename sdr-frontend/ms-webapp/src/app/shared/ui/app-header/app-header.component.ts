// // import { Component, Input } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { AppLogoComponent } from '../app-logo/app-logo.component';
// // import { ClockComponent } from '../clock/clock.component';
// // import { AppNavComponent } from '../app-nav/app-nav.component';

// // @Component({
// //   selector: 'app-header',
// //   standalone: true,
// //   imports: [CommonModule, AppLogoComponent, ClockComponent, AppNavComponent],
// //   template: `
// //     <header class="app-header">
// //       <app-logo [target]="target"></app-logo>
// //       <div class="app-header__spacer"></div>
// //       <app-clock></app-clock>
// //       <app-nav></app-nav>
// //     </header>
// //   `,
// // })
// // export class AppHeaderComponent {
// //   /** "app" = /home ; "public" = / */
// //   @Input() target: 'app' | 'public' = 'app';
// // }
// import { Component, Input } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { AppLogoComponent } from '../app-logo/app-logo.component';
// import { ClockComponent } from '../clock/clock.component';
// import { AppNavComponent } from '../app-nav/app-nav.component';

// @Component({
//   selector: 'app-header',
//   standalone: true,
//   imports: [CommonModule, AppLogoComponent, ClockComponent, AppNavComponent],
//   templateUrl: './app-header.component.html',
//   styleUrls: ['./app-header.component.scss'],
// })
// export class AppHeaderComponent {
//   /** "app" = /home ; "public" = / */
//   @Input() target: 'app' | 'public' = 'app';

//   // Bouton dark mode (fictif pour l’instant)
//   toggleTheme(): void {
//     // TODO: brancher plus tard sur un ThemeService
//     console.log('[Header] Toggle dark mode');
//   }
// }
// import { Component, Input } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { AppLogoComponent } from '../app-logo/app-logo.component';
// import { ClockComponent } from '../clock/clock.component';
// import { AppNavComponent } from '../app-nav/app-nav.component';

// @Component({
//   selector: 'app-header',
//   standalone: true,
//   imports: [CommonModule, AppLogoComponent, ClockComponent, AppNavComponent],
//   templateUrl: './app-header.component.html',
//   styleUrls: ['./app-header.component.scss'],
// })
// export class AppHeaderComponent {
//   /** "app" = /home ; "public" = / */
//   @Input() target: 'app' | 'public' = 'app';

//   isDark = false;

//   toggleTheme(): void {
//     this.isDark = !this.isDark;
//     document.documentElement.classList.toggle('theme-dark', this.isDark);
//   }
// }
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppNavComponent } from '../app-nav/app-nav.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app-header.component.html',
  styleUrls: ['./app-header.component.scss'],
})
export class AppHeaderComponent {
  @Input() target: 'app' | 'public' = 'app';
}

