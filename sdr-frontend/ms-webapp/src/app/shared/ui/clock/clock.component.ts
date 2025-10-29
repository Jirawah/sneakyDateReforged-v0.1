import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-clock',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="clock">
      {{ timeText }}
    </div>
  `
})
export class ClockComponent implements OnInit, OnDestroy {

  timeText = '';
  private intervalId: any;

  ngOnInit(): void {
    this.updateTimeText(); // valeur immédiate
    this.intervalId = setInterval(() => {
      this.updateTimeText();
    }, 1000 * 30); // on rafraîchit toutes les 30s, pas besoin chaque seconde
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalId);
  }

  private updateTimeText(): void {
    const now = new Date();

    // heures avec zéro devant si besoin
    const hh = now.getHours().toString().padStart(2, '0');

    // minutes avec zéro devant si besoin
    const mm = now.getMinutes().toString().padStart(2, '0');

    // format "13 h 53"
    this.timeText = `${hh} h ${mm}`;
  }
}
