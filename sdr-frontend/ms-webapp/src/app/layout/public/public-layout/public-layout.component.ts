import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, NavigationEnd, ActivatedRoute } from '@angular/router';
// import { AppLogoComponent } from '../../../shared/ui/app-logo/app-logo.component';
// import { filter, map, startWith } from 'rxjs/operators';
// import { Observable } from 'rxjs';

@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './public-layout.component.html',
  styleUrls: ['./public-layout.component.scss'],
})
export class PublicLayoutComponent {
  private router = inject(Router);
  private route  = inject(ActivatedRoute);
}
