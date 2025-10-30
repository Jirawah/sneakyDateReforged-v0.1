// // import { Component } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { RouterOutlet } from '@angular/router';

// // @Component({
// //   selector: 'app-public-layout',
// //   standalone: true,
// //   imports: [CommonModule, RouterOutlet],
// //   templateUrl: './public-layout.component.html',
// // })
// // export class PublicLayoutComponent {}
// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterOutlet } from '@angular/router';
// import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';

// @Component({
//   selector: 'app-public-layout',
//   standalone: true,
//   imports: [CommonModule, RouterOutlet, AppLogoComponent],
//   templateUrl: './public-layout.component.html',
//   styleUrls: ['./public-layout.component.scss'],
// })
// export class PublicLayoutComponent {
  
// }
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, NavigationEnd, ActivatedRoute } from '@angular/router';
import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
import { filter, map, startWith } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, AppLogoComponent],
  templateUrl: './public-layout.component.html',
  styleUrls: ['./public-layout.component.scss'],
})
export class PublicLayoutComponent {
  private router = inject(Router);
  private route  = inject(ActivatedRoute);

  // true = on affiche le logo ; false = on le cache
  showLogo$: Observable<boolean> = this.router.events.pipe(
    filter(e => e instanceof NavigationEnd),
    startWith(null), // calcule aussi au premier rendu
    map(() => {
      let r = this.route;
      while (r.firstChild) r = r.firstChild;
      const hideLogo = r.snapshot.data?.['hideLogo'] === true;
      return !hideLogo;
    })
  );
}
