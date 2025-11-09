import { Component, OnDestroy, HostListener, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Subscription, interval, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest } from '../../shared/models/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnDestroy {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  loading = false;
  error: string | null = null;

  // --- Popup Steam Help ---
  steamHelpOpen = false;

  openSteamHelp(): void {
    this.steamHelpOpen = true;
    document.body.style.overflow = 'hidden';
  }
  closeSteamHelp(): void {
    this.steamHelpOpen = false;
    document.body.style.overflow = '';
  }
  @HostListener('document:keydown.escape')
  onEsc() { this.closeSteamHelp(); }

  // polling Discord
  private discordPollSub?: Subscription;
  private discordState?: string;

  // infos détectées côté backend
  public discordPseudoFromBackend: string | null = null;
  public discordIdFromBackend: string | null = null;

  // Formulaire
  form: FormGroup = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      steamId: ['', [Validators.required]],
      discordConnected: [{ value: false, disabled: true }],
      password: ['', [Validators.required, Validators.minLength(12)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: [passwordsMatchValidator()] }
  );

  ngOnDestroy(): void {
    this.discordPollSub?.unsubscribe();
    this.closeSteamHelp();
  }

  openDiscordInvite(): void {
    this.error = null;
    this.authService.createDiscordPending().subscribe({
      next: ({ state }) => {
        this.discordState = state;
        const url = environment.discordInviteUrl || 'https://discord.com/app';
        window.open(url, '_blank', 'noopener,noreferrer');

        const start = Date.now();
        this.discordPollSub?.unsubscribe();
        this.discordPollSub = interval(2000)
          .pipe(
            switchMap(() => this.authService.getDiscordStatusByState(state)),
            catchError(() => of({ connected: false, discordPseudo: null, discordId: null }))
          )
          .subscribe((status) => {
            if (status?.connected) {
              this.form.get('discordConnected')?.setValue(true, { emitEvent: false });
              this.discordPseudoFromBackend = status.discordPseudo || null;
              this.discordIdFromBackend = status.discordId || null;
              this.discordPollSub?.unsubscribe();
            }
            if (Date.now() - start > 120000) this.discordPollSub?.unsubscribe();
          });
      },
      error: () => (this.error = 'Impossible de démarrer la connexion Discord (pending).'),
    });
  }

  submit(): void {
    if (this.form.invalid || !this.form.get('discordConnected')?.value) {
      this.form.markAllAsTouched();
      return;
    }
    if (!this.discordIdFromBackend) {
      this.error = "Discord non détecté. Rejoignez le salon vocal d'authentification.";
      return;
    }

    this.loading = true;
    const payload: RegisterRequest = {
      pseudo: (this.discordPseudoFromBackend || 'player').trim(),
      email: this.form.get('email')!.value,
      steamId: this.form.get('steamId')!.value,
      password: this.form.get('password')!.value,
      confirmPassword: this.form.get('confirmPassword')!.value,
      discordId: this.discordIdFromBackend,
    };

    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl('/auth/login');
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Impossible de créer le compte.';
      },
    });
  }
}

export function passwordsMatchValidator() {
  return (group: FormGroup) => {
    const p = group.get('password')?.value;
    const c = group.get('confirmPassword')?.value;
    return p && c && p === c ? null : { passwordsMismatch: true };
  };
}

