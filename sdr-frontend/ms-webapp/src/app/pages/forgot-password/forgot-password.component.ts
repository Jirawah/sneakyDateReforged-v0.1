import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PasswordResetService } from '../../core/services/password-reset.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private resetService = inject(PasswordResetService);

  loading = false;
  error: string | null = null;
  success: string | null = null;

  form: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = null;
    this.success = null;

    const email = this.form.get('email')!.value;
    this.resetService.requestReset(email).subscribe({
      next: (res) => {
        this.success = res?.message ?? 'Si cette adresse existe, un email de réinitialisation a été envoyé.';
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Impossible d’envoyer la demande pour le moment.';
        this.loading = false;
      },
    });
  }

  goBack(): void {
    this.router.navigateByUrl('/auth/login');
  }
}
