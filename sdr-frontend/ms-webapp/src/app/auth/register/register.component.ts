import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

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
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  // injecte ici ton AuthService si tu l’utilises dans submit()

  form: FormGroup = this.fb.group({
    pseudo: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    steamId: [''],
    // UX : l’utilisateur confirme avoir ouvert Discord/accepté l’invite
    discordConnected: [false, Validators.requiredTrue],
    password: ['', [Validators.required, Validators.minLength(12)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: [passwordsMatchValidator()] });

  loading = false;
  error: string | null = null;

  openDiscordInvite(): void {
    const url = environment.discordInviteUrl || 'https://discord.com/app';
    window.open(url, '_blank', 'noopener,noreferrer');
  }

  async submit(): Promise<void> {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = null;

    // Exemple d’appel si tu utilises un AuthService :
    // try {
    //   const payload = {
    //     pseudo: this.form.value.pseudo,
    //     email: this.form.value.email,
    //     steamId: this.form.value.steamId || null,
    //     password: this.form.value.password,
    //     confirmPassword: this.form.value.confirmPassword,
    //     discordId: null // le bot fera le /auth/discord/sync plus tard
    //   };
    //   await this.authService.register(payload).toPromise();
    //   this.router.navigateByUrl('/auth/login');
    // } catch (e: any) {
    //   this.error = e?.error?.message || 'Une erreur est survenue.';
    // } finally {
    //   this.loading = false;
    // }

    // Si tu n’as pas encore branché le service, retire le loader :
    this.loading = false;
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
