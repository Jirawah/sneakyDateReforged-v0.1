// src/app/pages/reset-password/reset-password.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { PasswordResetService } from '../../core/services/password-reset.service';

// Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule }      from '@angular/material/input';
import { MatButtonModule }     from '@angular/material/button';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent implements OnInit {

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private resetService = inject(PasswordResetService);

  loading = false;
  error: string | null = null;
  success: string | null = null;

  token: string | null = null;

  form: FormGroup = this.fb.group(
    {
      newPassword: ['', [Validators.required, Validators.minLength(12)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: [passwordsMatchValidator()] }
  );

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    // Si pas de token dans l'URL → c'est louche → on bloque tout
    if (!this.token) {
      this.error = 'Lien de réinitialisation invalide ou expiré.';
    }
  }

  goBack(): void {
    // bouton "RETOUR" → on renvoie vers la page login
    this.router.navigateByUrl('/auth/login');
  }

  submit(): void {
    if (!this.token) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    const newPwd = this.form.get('newPassword')!.value;

    this.resetService.confirmReset(this.token, newPwd).subscribe({
      next: () => {
        // backend PasswordResetService.resetPassword()
        // -> si OK, il set le token used=true etc.
        this.success = 'Mot de passe mis à jour. Redirection vers la connexion…';

        // petite redirection après succès :
        this.router.navigateByUrl('/auth/login');
      },
      error: (err) => {
        console.error('[RESET-PASSWORD] error', err);

        // Ton GlobalExceptionHandler renvoie { message: "..."}
        // par ex "Token expiré ou déjà utilisé."
        this.error =
          err?.error?.message ||
          'Impossible de réinitialiser le mot de passe.';
        this.loading = false;
      },
    });
  }
}

/** Vérif que les 2 mots de passe matchent */
export function passwordsMatchValidator() {
  return (group: FormGroup) => {
    const p1 = group.get('newPassword')?.value;
    const p2 = group.get('confirmPassword')?.value;
    return p1 && p2 && p1 === p2 ? null : { passwordsMismatch: true };
  };
}
