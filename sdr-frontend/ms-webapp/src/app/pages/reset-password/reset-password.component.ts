// // src/app/pages/reset-password/reset-password.component.ts
// import { Component, inject, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import {
//   FormBuilder,
//   FormGroup,
//   Validators,
//   ReactiveFormsModule
// } from '@angular/forms';
// import { Router, ActivatedRoute, RouterLink } from '@angular/router';
// import { PasswordResetService } from '../../core/services/password-reset.service';

// // Material
// import { MatFormFieldModule } from '@angular/material/form-field';
// import { MatInputModule }      from '@angular/material/input';
// import { MatButtonModule }     from '@angular/material/button';

// @Component({
//   selector: 'app-reset-password',
//   standalone: true,
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     RouterLink,
//     MatFormFieldModule,
//     MatInputModule,
//     MatButtonModule,
//   ],
//   templateUrl: './reset-password.component.html',
//   styleUrls: ['./reset-password.component.scss'],
// })
// export class ResetPasswordComponent implements OnInit {

//   private fb = inject(FormBuilder);
//   private router = inject(Router);
//   private route = inject(ActivatedRoute);
//   private resetService = inject(PasswordResetService);

//   loading = false;
//   error: string | null = null;
//   success: string | null = null;

//   token: string | null = null;

//   form: FormGroup = this.fb.group(
//     {
//       newPassword: ['', [Validators.required, Validators.minLength(12)]],
//       confirmPassword: ['', [Validators.required]],
//     },
//     { validators: [passwordsMatchValidator()] }
//   );

//   ngOnInit(): void {
//     this.token = this.route.snapshot.queryParamMap.get('token');
//     // Si pas de token dans l'URL → c'est louche → on bloque tout
//     if (!this.token) {
//       this.error = 'Lien de réinitialisation invalide ou expiré.';
//     }
//   }

//   goBack(): void {
//     // bouton "RETOUR" → on renvoie vers la page login
//     this.router.navigateByUrl('/auth/login');
//   }

//   submit(): void {
//     if (!this.token) return;
//     if (this.form.invalid) {
//       this.form.markAllAsTouched();
//       return;
//     }

//     this.loading = true;
//     this.error = null;
//     this.success = null;

//     const newPwd = this.form.get('newPassword')!.value;

//     this.resetService.confirmReset(this.token, newPwd).subscribe({
//       next: () => {
//         // backend PasswordResetService.resetPassword()
//         // -> si OK, il set le token used=true etc.
//         this.success = 'Mot de passe mis à jour. Redirection vers la connexion…';

//         // petite redirection après succès :
//         this.router.navigateByUrl('/auth/login');
//       },
//       error: (err) => {
//         console.error('[RESET-PASSWORD] error', err);

//         // Ton GlobalExceptionHandler renvoie { message: "..."}
//         // par ex "Token expiré ou déjà utilisé."
//         this.error =
//           err?.error?.message ||
//           'Impossible de réinitialiser le mot de passe.';
//         this.loading = false;
//       },
//     });
//   }
// }

// /** Vérif que les 2 mots de passe matchent */
// export function passwordsMatchValidator() {
//   return (group: FormGroup) => {
//     const p1 = group.get('newPassword')?.value;
//     const p2 = group.get('confirmPassword')?.value;
//     return p1 && p2 && p1 === p2 ? null : { passwordsMismatch: true };
//   };
// }
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { PasswordResetService } from '../../core/services/password-reset.service';

function passwordsMatchValidator() {
  return (group: FormGroup) => {
    const p = group.get('newPassword')?.value;
    const c = group.get('confirmPassword')?.value;
    return p && c && p === c ? null : { passwordsMismatch: true };
  };
}

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private service = inject(PasswordResetService);

  token = this.route.snapshot.queryParamMap.get('token');
  loading = false;
  error: string | null = null;
  success: string | null = null;

  form: FormGroup = this.fb.group(
    {
      newPassword: ['', [Validators.required, Validators.minLength(12)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: [passwordsMatchValidator()] }
  );

  submit(): void {
    if (!this.token || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = null;
    this.success = null;

    this.service.confirmReset(this.token, this.form.get('newPassword')!.value).subscribe({
      next: () => {
        // this.success = 'Mot de passe réinitialisé. Tu peux te connecter.';
        // this.loading = false;
        // this.router.navigateByUrl('/auth/login');
        setTimeout(() => this.router.navigateByUrl('/auth/login'), 1500);
      },
      error: (err) => {
        this.error = err?.error?.message || 'Impossible de réinitialiser le mot de passe.';
        this.loading = false;
      },
    });
  }

  goBack(): void {
    this.router.navigateByUrl('/auth/login');
  }
}
