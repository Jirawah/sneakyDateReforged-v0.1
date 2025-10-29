// import { Component, OnInit, OnDestroy } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterLink } from '@angular/router';

// @Component({
//   standalone: true,
//   selector: 'app-public-home',
//   imports: [CommonModule, RouterLink],
//   templateUrl: './public-home.component.html',
//   styleUrls: ['./public-home.component.scss'],
// })
// export class PublicHomeComponent implements OnInit, OnDestroy {

//   // heure affichée en haut à gauche
//   currentTime = '';

//   // stats
//   membersCount = 1125; // TODO: viendra du back plus tard
//   rdvCount = 83;       // TODO: idem

//   // puces marketing
//   sellingPoints: string[] = [
//     `Les membres sont définis comme "clean" via l’API Web Steam`,
//     `Création d’équipes et de communautés éthiques`,
//     `Connexion vocale simplifiée avec l’API Discord`,
//     `Facilité de recherche de joueurs fairplay`,
//   ];

//   private clockIntervalId: any;

//   ngOnInit(): void {
//     this.updateTime();
//     this.clockIntervalId = setInterval(() => {
//       this.updateTime();
//     }, 60_000);
//   }

//   ngOnDestroy(): void {
//     if (this.clockIntervalId) {
//       clearInterval(this.clockIntervalId);
//     }
//   }

//   private updateTime(): void {
//     const now = new Date();
//     const hours = now.getHours().toString().padStart(2, '0');
//     const mins = now.getMinutes().toString().padStart(2, '0');
//     this.currentTime = `${hours} h ${mins}`;
//   }

//   toggleDarkMode(): void {
//     console.log('[TODO] toggle dark mode');
//   }
// }
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

// horloge réutilisable
import { ClockComponent } from '../../shared/ui/clock/clock.component';

@Component({
  selector: 'app-public-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ClockComponent
  ],
  templateUrl: './public-home.component.html',
  styleUrls: ['./public-home.component.scss']
})
export class PublicHomeComponent {
  // Ces valeurs viendront du back plus tard
  membersCount = 1125;
  rdvCount = 83;

  // Texte marketing affiché dans la carte centrale
  sellingPoints: string[] = [
    `Les membres sont définis comme "clean" via l’API Web Steam`,
    `Création d’équipes et de communautés éthiques`,
    `Connexion vocale simplifiée avec l’API Discord`,
    `Facilité de recherche de joueurs fairplay`,
  ];

  // placeholder pour le switch dark mode
  toggleDarkMode(): void {
    console.log('[TODO] toggle dark mode');
  }
}
