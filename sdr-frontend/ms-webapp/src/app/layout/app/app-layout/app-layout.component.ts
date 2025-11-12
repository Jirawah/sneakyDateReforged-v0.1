// // import { Component } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { RouterOutlet } from '@angular/router';

// // @Component({
// //   selector: 'app-app-layout',
// //   standalone: true,
// //   imports: [CommonModule, RouterOutlet],
// //   templateUrl: './app-layout.component.html',
// // })
// // export class AppLayoutComponent {}
// // import { Component } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { RouterOutlet } from '@angular/router';

// // import { AppHeaderComponent } from '../../../shared/ui/app-header/app-header.component';
// // import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// // import { ClockComponent } from '../../../shared/ui/clock/clock.component';

// // @Component({
// //   selector: 'app-app-layout',
// //   standalone: true,
// //   imports: [CommonModule, RouterOutlet, AppHeaderComponent, AppLogoComponent, ClockComponent],
// //   templateUrl: './app-layout.component.html',
// //   styleUrls: ['./app-layout.component.scss'],
// // })
// // export class AppLayoutComponent {
// //   isDark = false;

// //   toggleTheme(): void {
// //     this.isDark = !this.isDark;
// //     document.documentElement.classList.toggle('theme-dark', this.isDark);
// //   }
// // }
// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterOutlet } from '@angular/router';

// import { AppHeaderComponent } from '../../../shared/ui/app-header/app-header.component';
// import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// import { ClockComponent } from '../../../shared/ui/clock/clock.component';

// @Component({
//   selector: 'app-app-layout',
//   standalone: true,
//   imports: [CommonModule, RouterOutlet, AppHeaderComponent, AppLogoComponent, ClockComponent],
//   templateUrl: './app-layout.component.html',
//   styleUrls: ['./app-layout.component.scss'],
// })
// export class AppLayoutComponent {
//   isDark = false;

//   toggleTheme(): void {
//     this.isDark = !this.isDark;
//     document.documentElement.classList.toggle('theme-dark', this.isDark);
//   }
// }
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
import { ClockComponent } from '../../../shared/ui/clock/clock.component';
import { AppNavComponent } from '../../../shared/ui/app-nav/app-nav.component';

@Component({
  selector: 'app-app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, AppLogoComponent, ClockComponent, AppNavComponent],
  templateUrl: './app-layout.component.html',
  styleUrls: ['./app-layout.component.scss'],
})
export class AppLayoutComponent {
  isDark = false;
  toggleTheme(): void {
    this.isDark = !this.isDark;
    document.documentElement.classList.toggle('theme-dark', this.isDark);
  }
}
