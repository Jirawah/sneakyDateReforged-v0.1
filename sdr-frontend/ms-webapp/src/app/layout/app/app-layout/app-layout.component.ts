// // // // import { Component } from '@angular/core';
// // // // import { CommonModule } from '@angular/common';
// // // // import { RouterOutlet } from '@angular/router';

// // // // @Component({
// // // //   selector: 'app-app-layout',
// // // //   standalone: true,
// // // //   imports: [CommonModule, RouterOutlet],
// // // //   templateUrl: './app-layout.component.html',
// // // // })
// // // // export class AppLayoutComponent {}
// // // // import { Component } from '@angular/core';
// // // // import { CommonModule } from '@angular/common';
// // // // import { RouterOutlet } from '@angular/router';

// // // // import { AppHeaderComponent } from '../../../shared/ui/app-header/app-header.component';
// // // // import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// // // // import { ClockComponent } from '../../../shared/ui/clock/clock.component';

// // // // @Component({
// // // //   selector: 'app-app-layout',
// // // //   standalone: true,
// // // //   imports: [CommonModule, RouterOutlet, AppHeaderComponent, AppLogoComponent, ClockComponent],
// // // //   templateUrl: './app-layout.component.html',
// // // //   styleUrls: ['./app-layout.component.scss'],
// // // // })
// // // // export class AppLayoutComponent {
// // // //   isDark = false;

// // // //   toggleTheme(): void {
// // // //     this.isDark = !this.isDark;
// // // //     document.documentElement.classList.toggle('theme-dark', this.isDark);
// // // //   }
// // // // }
// // // import { Component } from '@angular/core';
// // // import { CommonModule } from '@angular/common';
// // // import { RouterOutlet } from '@angular/router';

// // // import { AppHeaderComponent } from '../../../shared/ui/app-header/app-header.component';
// // // import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// // // import { ClockComponent } from '../../../shared/ui/clock/clock.component';

// // // @Component({
// // //   selector: 'app-app-layout',
// // //   standalone: true,
// // //   imports: [CommonModule, RouterOutlet, AppHeaderComponent, AppLogoComponent, ClockComponent],
// // //   templateUrl: './app-layout.component.html',
// // //   styleUrls: ['./app-layout.component.scss'],
// // // })
// // // export class AppLayoutComponent {
// // //   isDark = false;

// // //   toggleTheme(): void {
// // //     this.isDark = !this.isDark;
// // //     document.documentElement.classList.toggle('theme-dark', this.isDark);
// // //   }
// // // }
// // import { Component } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { RouterOutlet } from '@angular/router';

// // import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// // import { ClockComponent } from '../../../shared/ui/clock/clock.component';
// // import { AppNavComponent } from '../../../shared/ui/app-nav/app-nav.component';

// // @Component({
// //   selector: 'app-app-layout',
// //   standalone: true,
// //   imports: [CommonModule, RouterOutlet, AppLogoComponent, ClockComponent, AppNavComponent],
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
// // import { Component, OnDestroy, OnInit } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { RouterOutlet, Router, ActivatedRoute, NavigationEnd } from '@angular/router';
// // import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// // import { ClockComponent } from '../../../shared/ui/clock/clock.component';
// // import { AppNavComponent } from '../../../shared/ui/app-nav/app-nav.component';
// // import { filter, Subscription } from 'rxjs';

// // @Component({
// //   selector: 'app-app-layout',
// //   standalone: true,
// //   imports: [CommonModule, RouterOutlet, AppLogoComponent, ClockComponent, AppNavComponent],
// //   templateUrl: './app-layout.component.html',
// //   styleUrls: ['./app-layout.component.scss'],
// // })
// // export class AppLayoutComponent implements OnInit, OnDestroy {
// //   isDark = false;
// //   pageTitle = '';
// //   private sub?: Subscription;

// //   constructor(private router: Router, private route: ActivatedRoute) {}

// //   ngOnInit(): void {
// //     const readDeepestTitle = () => {
// //       let r = this.route;
// //       while (r.firstChild) r = r.firstChild;
// //       const t = (r.snapshot.data['pageTitle'] ?? r.snapshot.data['title'] ?? '') as string;
// //       this.pageTitle = (t || '').toUpperCase();
// //     };

// //     // au premier affichage
// //     readDeepestTitle();

// //     // aux navigations suivantes
// //     this.sub = this.router.events.pipe(filter(e => e instanceof NavigationEnd))
// //       .subscribe(readDeepestTitle);
// //   }

// //   ngOnDestroy(): void {
// //     this.sub?.unsubscribe();
// //   }

// //   toggleTheme(): void {
// //     this.isDark = !this.isDark;
// //     document.documentElement.classList.toggle('theme-dark', this.isDark);
// //   }
// // }
// import { Component, OnDestroy, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterOutlet, Router, ActivatedRoute, NavigationEnd } from '@angular/router';
// import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// import { ClockComponent } from '../../../shared/ui/clock/clock.component';
// import { AppNavComponent } from '../../../shared/ui/app-nav/app-nav.component';
// import { filter, map, Subscription } from 'rxjs';

// @Component({
//   selector: 'app-app-layout',
//   standalone: true,
//   imports: [CommonModule, RouterOutlet, AppLogoComponent, ClockComponent, AppNavComponent],
//   templateUrl: './app-layout.component.html',
//   styleUrls: ['./app-layout.component.scss'],
// })
// export class AppLayoutComponent implements OnInit, OnDestroy {
//   isDark = false;
//   pageTitle = '';
//   private sub?: Subscription;

//   constructor(private router: Router, private route: ActivatedRoute) {}

//   /** Lit le titre sur la route la plus profonde à partir de la racine du router. */
//   private readDeepestTitle(): string {
//     let r = this.router.routerState.root; // ← important: partir de la racine
//     let title = '';
//     while (r.firstChild) {
//       r = r.firstChild;
//       const data = r.snapshot.data as Record<string, unknown>;
//       title = (data['pageTitle'] ?? data['title'] ?? title) as string;
//     }
//     return title;
//   }

//   ngOnInit(): void {
//     // 1) premier rendu
//     this.pageTitle = (this.readDeepestTitle() || '').toUpperCase();

//     // 2) navigations suivantes
//     this.sub = this.router.events.pipe(
//       filter((e): e is NavigationEnd => e instanceof NavigationEnd),
//       map(() => (this.readDeepestTitle() || '').toUpperCase())
//     ).subscribe(t => (this.pageTitle = t));
//   }

//   ngOnDestroy(): void {
//     this.sub?.unsubscribe();
//   }

//   toggleTheme(): void {
//     this.isDark = !this.isDark;
//     document.documentElement.classList.toggle('theme-dark', this.isDark);
//   }
// }
import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
import { ClockComponent } from '../../../shared/ui/clock/clock.component';
import { AppNavComponent } from '../../../shared/ui/app-nav/app-nav.component';
import { filter, map, mergeMap, Subscription } from 'rxjs';

@Component({
  selector: 'app-app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, AppLogoComponent, ClockComponent, AppNavComponent],
  templateUrl: './app-layout.component.html',
  styleUrls: ['./app-layout.component.scss'],
})
export class AppLayoutComponent implements OnInit, OnDestroy {
  isDark = false;
  pageTitle = '';
  private sub?: Subscription;

  constructor(private router: Router, private route: ActivatedRoute) {}

  /** Récupère le snapshot .data de la route la plus profonde (chargement initial). */
  private getInitialDeepestData(): Record<string, unknown> {
    let r = this.route;
    while (r.firstChild) r = r.firstChild;
    return r.snapshot.data ?? {};
  }

  ngOnInit(): void {
    // 1) Premier rendu (refresh / entrée directe sur l’URL)
    {
      const d = this.getInitialDeepestData();
      const t = (d['pageTitle'] ?? d['title'] ?? '') as string;
      this.pageTitle = t ? t.toUpperCase() : '';
    }

    // 2) Navigations suivantes (écoute NavigationEnd → route la plus profonde → data)
    this.sub = this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map(() => this.route),
      map((r) => { while (r.firstChild) r = r.firstChild; return r; }),
      filter(r => r.outlet === 'primary'),
      mergeMap(r => r.data)
    ).subscribe((data) => {
      const t = (data['pageTitle'] ?? data['title'] ?? '') as string;
      this.pageTitle = t ? t.toUpperCase() : '';
      // console.log('PAGE TITLE =', this.pageTitle, data); // décommente au besoin
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  toggleTheme(): void {
    this.isDark = !this.isDark;
    document.documentElement.classList.toggle('theme-dark', this.isDark);
  }
}
