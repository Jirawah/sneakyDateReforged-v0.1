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

      padding: 4px 4px;          
      border-radius: 999px;            
      background: #f1f3f3;               
      color: #0b0f10;                     
      border: 1.5px solid #0b0f10;        

      box-shadow:
        0 0 0 3px var(--_accent),         
        0 0 14px var(--_glow);            

      font-weight: 800;
      letter-spacing: .02em;
      line-height: 1;
      font-variant-numeric: tabular-nums; 
      -webkit-font-smoothing: antialiased;
      user-select: none;

      /* stabilité visuelle quand l'heure change */
      min-width: 88px;                 
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
    this.intervalId = setInterval(() => this.updateTimeText(), 30_000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalId);
  }

  private updateTimeText(): void {
    const now = new Date();
    const hh = now.getHours().toString().padStart(2, '0');
    const mm = now.getMinutes().toString().padStart(2, '0');
    this.timeText = `${hh} h ${mm}`;
  }
}
