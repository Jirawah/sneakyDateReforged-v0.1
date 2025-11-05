import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-clock',
  standalone: true,
  imports: [CommonModule],
  encapsulation: ViewEncapsulation.Emulated, // styles scoped au composant
  template: `
    <div class="clock" aria-live="polite" aria-label="Heure locale">
      {{ timeText }}
    </div>
  `,
  styles: [`
    :host { display: inline-block; }

    .clock{
      /* Tokens (hérite de la page si présents, sinon fallbacks) */
      --_accent: var(--accent, #2CEAC6);
      --_glow:   var(--accent-glow, rgba(44,234,198,.30));

      display: inline-flex;
      align-items: center;
      justify-content: center;

      padding: 4px 4px;                 /* épaisseur de la pilule */
      border-radius: 999px;               /* forme pilule */
      background: #f1f3f3;                /* intérieur CLAIR (comme la maquette) */
      color: #0b0f10;                      /* texte noir */
      border: 1.5px solid #0b0f10;        /* fin liseré noir interne */

      /* anneau vert net + glow doux autour (maquette) */
      box-shadow:
        0 0 0 3px var(--_accent),         /* anneau vert (épaisseur) */
        0 0 14px var(--_glow);            /* halo néon */

      font-weight: 800;
      letter-spacing: .02em;
      line-height: 1;
      font-variant-numeric: tabular-nums; /* chiffres monospaces, évite les sauts */
      -webkit-font-smoothing: antialiased;
      user-select: none;

      /* stabilité visuelle quand l'heure change */
      min-width: 88px;                    /* ~ largeur “23 h 59” */
    }

    /* Variante compacte sur écrans étroits */
    @media (max-width: 640px){
      .clock{ padding: 5px 12px; min-width: 80px; }
    }
  `]
})
export class ClockComponent implements OnInit, OnDestroy {

  timeText = '';
  private intervalId: any;

  ngOnInit(): void {
    this.updateTimeText();
    this.intervalId = setInterval(() => this.updateTimeText(), 30_000); // refresh toutes les 30s
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalId);
  }

  private updateTimeText(): void {
    const now = new Date();
    const hh = now.getHours().toString().padStart(2, '0');
    const mm = now.getMinutes().toString().padStart(2, '0');
    this.timeText = `${hh} h ${mm}`; // ex: "13 h 53"
  }
}
