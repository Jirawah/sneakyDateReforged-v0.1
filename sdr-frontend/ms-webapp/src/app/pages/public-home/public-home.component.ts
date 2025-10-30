// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterLink } from '@angular/router';

// // horloge réutilisable
// import { ClockComponent } from '../../shared/ui/clock/clock.component';

// @Component({
//   selector: 'app-public-home',
//   standalone: true,
//   imports: [
//     CommonModule,
//     RouterLink,
//     ClockComponent
//   ],
//   templateUrl: './public-home.component.html',
//   styleUrls: ['./public-home.component.scss']
// })
// export class PublicHomeComponent {
//   // Ces valeurs viendront du back plus tard
//   membersCount = 1125;
//   rdvCount = 83;

//   // Texte marketing affiché dans la carte centrale
//   sellingPoints: string[] = [
//     `Les membres sont définis comme "clean" via l’API Web Steam`,
//     `Création d’équipes et de communautés éthiques`,
//     `Connexion vocale simplifiée avec l’API Discord`,
//     `Facilité de recherche de joueurs fairplay`,
//   ];

//   // placeholder pour le switch dark mode
//   toggleDarkMode(): void {
//     console.log('[TODO] toggle dark mode');
//   }
// }
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

// Horloge déjà existante
import { ClockComponent } from '../../shared/ui/clock/clock.component';

@Component({
  selector: 'app-public-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    ClockComponent
  ],
  templateUrl: './public-home.component.html',
  styleUrls: ['./public-home.component.scss'],
})
export class PublicHomeComponent {
  membersCount = 0;
  rdvCount = 0;

  sellingPoints = [
    'Anti-cheat social (Steam + Discord)',
    'Organisation de RDV par jeu',
    'Notes & historique joueurs',
  ];

  toggleDarkMode(): void {
    // placeholder sans style pour l’instant
    console.log('[HOME] toggleDarkMode()');
  }
}
