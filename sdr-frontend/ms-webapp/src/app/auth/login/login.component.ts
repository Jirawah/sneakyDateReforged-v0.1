import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

// Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
imports: [
  ReactiveFormsModule,
  // Material
  import('@angular/material/form-field').then(m => m.MatFormFieldModule),
  import('@angular/material/input').then(m => m.MatInputModule),
  import('@angular/material/button').then(m => m.MatButtonModule),
]

// import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  // private auth = inject(AuthService);

  loading = false;
  error: string | null = null;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  submit() {
    if (this.form.invalid) return;
    // Étape A (UI seule) : placeholder
    // console.log('login payload', this.form.value);
    // this.router.navigateByUrl('/home');

    // Étape B (quand on branche le back) :
    // this.loading = true; this.error = null;
    // this.auth.login(this.form.value as any).subscribe({
    //   next: () => { this.loading = false; this.router.navigateByUrl('/home'); },
    //   error: (e) => { this.loading = false; this.error = e?.error?.message ?? 'Connexion échouée.'; }
    // });
  }
}
