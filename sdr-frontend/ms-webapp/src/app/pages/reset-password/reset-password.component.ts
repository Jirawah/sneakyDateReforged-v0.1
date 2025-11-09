import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
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

    const newPwd = this.form.get('newPassword')!.value;

    this.service.confirmReset(this.token, newPwd).subscribe({
      next: () => {
        this.success = 'Mot de passe mis à jour. Redirection vers la connexion…';
        setTimeout(() => this.router.navigateByUrl('/auth/login'), 1200);
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
