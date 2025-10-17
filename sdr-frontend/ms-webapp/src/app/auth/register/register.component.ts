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
import { catchError, switchMap, takeWhile, timeout } from 'rxjs/operators';
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

  // ✅ discordConnected désactivé (non cliquable) et sans requiredTrue
  form: FormGroup = this.fb.group({
    pseudo: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    steamId: [''],
    discordConnected: [{ value: false, disabled: true }], // 🔒 UI non cliquable
    password: ['', [Validators.required, Validators.minLength(12)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: [passwordsMatchValidator()] });

  ngOnDestroy(): void {
    this.discordPollSub?.unsubscribe();
  }

  openDiscordInvite(): void {
    const url = environment.discordInviteUrl || 'https://discord.com/app';
    window.open(url, '_blank', 'noopener,noreferrer');

    // (re)démarre le polling
    this.error = null;
    this.discordPollSub?.unsubscribe();

    const maxSeconds = 120; // 2 min
    let elapsed = 0;

    this.discordPollSub = interval(2000)
      .pipe(
        takeWhile(() => elapsed <= maxSeconds),
        switchMap(() => {
          elapsed += 2;
          const pseudo = (this.form.get('pseudo')?.value ?? '').toString().trim();
          // 🔁 Appel back: /discord/status?pseudo=...
          return this.authService.getDiscordStatus(pseudo).pipe(
            timeout(1800),
            catchError(() => of({ connected: false } as { connected: boolean }))
          );
        })
      )
      .subscribe((res: { connected: boolean; profile?: any }) => {
        if (res?.connected) {
          // ✅ coche le contrôle (toujours disabled en UI)
          this.form.get('discordConnected')?.setValue(true, { emitEvent: false });
          this.discordPollSub?.unsubscribe();
        }
      });
  }

  async submit(): Promise<void> {
    // Bloque tant que discordConnected n'est pas validé côté back
    if (this.form.invalid || !this.form.get('discordConnected')?.value) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    try {
      // Exemple si tu branches vraiment l'AuthService.register :
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
