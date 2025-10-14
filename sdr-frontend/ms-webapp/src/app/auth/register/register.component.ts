import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

// Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

// import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  // private auth = inject(AuthService);

  loading = false;
  error: string | null = null;

  form = this.fb.group({
    pseudo: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    steamId: [''],
    password: ['', [Validators.required, Validators.minLength(12)]],
    confirmPassword: ['', Validators.required],
  });

  submit() {
    if (this.form.invalid) return;

    // Validation locale simple
    const v = this.form.value;
    if (v.password !== v.confirmPassword) {
      this.error = 'Les mots de passe ne correspondent pas.'; return;
    }

    // Étape A (UI seule)
    // console.log('register payload', v);
    // this.router.navigateByUrl('/home');

    // Étape B (quand on branche le back) :
    // this.loading = true; this.error = null;
    // this.auth.register(v as any).subscribe({
    //   next: () => { this.loading = false; this.router.navigateByUrl('/home'); },
    //   error: (e) => { this.loading = false; this.error = e?.error?.message ?? 'Inscription échouée.'; }
    // });
  }
}
