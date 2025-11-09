import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

// Angular Material (minimum)
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

// Horloge existante
import { ClockComponent } from '../../shared/ui/clock/clock.component';

@Component({
  selector: 'app-public-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatButtonModule,
    MatIconModule,
    ClockComponent
  ],
  templateUrl: './public-home.component.html',
  styleUrls: ['./public-home.component.scss'],
})
export class PublicHomeComponent {
  membersStatSrc = 'assets/home/asideLeft/membersImg.png';
  rdvStatSrc = 'assets/home/asideLeft/rdvImg.png';

  heroLeftSrc = 'assets/home/center/amongUsLeft.png';
  heroRightSrc = 'assets/home/center/amongUsRight.png';

  promoCards = [
    {
      alt: 'Among Us',
      logo: 'assets/home/asideRight/amongUsLogo.png',
      image: 'assets/home/asideRight/amongUsImg.png'
    },
    {
      alt: 'Rust',
      logo: 'assets/home/asideRight/rustLogo.png',
      image: 'assets/home/asideRight/rustImg.png'
    },
    {
      alt: 'PUBG',
      logo: 'assets/home/asideRight/pubgLogo.png',
      image: 'assets/home/asideRight/pubgImg.png'
    }
  ];

  partners = [
    { src: 'assets/brand/facepunch.png', alt: 'Facepunch Studios' },
    { src: 'assets/brand/krafton.png', alt: 'KRAFTON' },
    { src: 'assets/brand/innerslot.png', alt: 'Innersloth' }
  ];

  sellingPoints = [
    'Les membres sont définis comme “clean” via l’API Web Steam',
    'Création d’équipes et de communautés éthiques',
    'Connexion vocale simplifiée avec l’API Discord',
    'Facilité de recherche de joueurs fairplay',
  ];

  toggleDarkMode(): void {
    console.log('[HOME] toggleDarkMode()');
  }
}
