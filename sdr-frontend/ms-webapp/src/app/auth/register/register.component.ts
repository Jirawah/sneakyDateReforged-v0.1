import { Component, OnDestroy, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';
import { Subscription, interval, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnDestroy {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  loading = false;
  error: string | null = null;

  private discordPollSub?: Subscription;
  private discordState?: string; // ✅ on garde le "state" renvoyé par le back

  // ✅ plus de champ "pseudo"
  form: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    steamId: [''],
    discordConnected: [{ value: false, disabled: true }], // 🔒 non cliquable
    password: ['', [Validators.required, Validators.minLength(12)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: [passwordsMatchValidator()] });

  ngOnDestroy(): void {
    this.discordPollSub?.unsubscribe();
  }

  openDiscordInvite(): void {
    this.error = null;

    // 1) Demande un "state" au backend
    this.authService.createDiscordPending().subscribe({
      next: ({ state }) => {
        this.discordState = state;

        // 2) Ouvre Discord
        const url = environment.discordInviteUrl || 'https://discord.com/app';
        window.open(url, '_blank', 'noopener,noreferrer');

        // 3) Démarre le polling par state
        const start = Date.now();
        this.discordPollSub?.unsubscribe();

        this.discordPollSub = interval(2000).pipe(
          switchMap(() => this.authService.getDiscordStatusByState(state)),
          catchError(() => of({ connected: false }))
        ).subscribe(res => {
          if (res?.connected) {
            this.form.get('discordConnected')?.setValue(true, { emitEvent: false });
            this.discordPollSub?.unsubscribe();
          }
          if (Date.now() - start > 120000) { // 2 min de polling max
            this.discordPollSub?.unsubscribe();
          }
        });
      },
      error: () => {
        this.error = 'Impossible de démarrer la connexion Discord (pending).';
      }
    });
  }

  async submit(): Promise<void> {
    if (this.form.invalid || !this.form.get('discordConnected')?.value) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    try {
      // Exemple quand tu brancheras vraiment l’AuthService.register :
      // const payload = this.form.getRawValue(); // inclut les disabled
      // await this.authService.register(payload).toPromise();
      // this.router.navigateByUrl('/auth/login');
    } catch (e: any) {
      this.error = e?.error?.message || 'Une erreur est survenue.';
    } finally {
      this.loading = false;
    }
  }
}

/** Validator : password === confirmPassword */
export function passwordsMatchValidator() {
  return (group: FormGroup) => {
    const p = group.get('password')?.value;
    const c = group.get('confirmPassword')?.value;
    return p && c && p === c ? null : { passwordsMismatch: true };
  };
}
