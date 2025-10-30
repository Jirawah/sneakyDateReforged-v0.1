// import { Component, inject } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
// import { Router } from '@angular/router';

// // Material
// import { MatCardModule } from '@angular/material/card';
// import { MatFormFieldModule } from '@angular/material/form-field';
// import { MatInputModule } from '@angular/material/input';
// import { MatButtonModule } from '@angular/material/button';
// imports: [
//   ReactiveFormsModule,
//   // Material
//   import('@angular/material/form-field').then(m => m.MatFormFieldModule),
//   import('@angular/material/input').then(m => m.MatInputModule),
//   import('@angular/material/button').then(m => m.MatButtonModule),
// ]

// // import { AuthService } from '../../core/services/auth.service';

// @Component({
//   selector: 'app-login',
//   standalone: true,
//   imports: [
//     CommonModule, ReactiveFormsModule,
//     MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule
//   ],
//   templateUrl: './login.component.html',
//   styleUrls: ['./login.component.scss'],
// })
// export class LoginComponent {
//   private fb = inject(FormBuilder);
//   private router = inject(Router);
//   // private auth = inject(AuthService);

//   loading = false;
//   error: string | null = null;

//   form = this.fb.group({
//     email: ['', [Validators.required, Validators.email]],
//     password: ['', Validators.required],
//   });

//   submit() {
//     if (this.form.invalid) return;
//     // Étape A (UI seule) : placeholder
//     // console.log('login payload', this.form.value);
//     // this.router.navigateByUrl('/home');

//     // Étape B (quand on branche le back) :
//     // this.loading = true; this.error = null;
//     // this.auth.login(this.form.value as any).subscribe({
//     //   next: () => { this.loading = false; this.router.navigateByUrl('/home'); },
//     //   error: (e) => { this.loading = false; this.error = e?.error?.message ?? 'Connexion échouée.'; }
//     // });
//   }
// }
// import { Component, inject } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { Router, RouterLink } from '@angular/router';
// import { MatFormFieldModule } from '@angular/material/form-field';
// import { MatInputModule } from '@angular/material/input';
// import { MatButtonModule } from '@angular/material/button';
// import { CommonModule } from '@angular/common';

// import { AuthService } from '../../core/services/auth.service';

// @Component({
//   selector: 'app-login',
//   standalone: true,
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     RouterLink,
//     MatFormFieldModule,
//     MatInputModule,
//     MatButtonModule
//   ],
//   templateUrl: './login.component.html',
//   styleUrls: ['./login.component.scss']
// })
// export class LoginComponent {

//   private fb = inject(FormBuilder);
//   private router = inject(Router);
//   private authService = inject(AuthService);

//   loading = false;
//   error: string | null = null;

//   form: FormGroup = this.fb.group({
//     email: ['', [Validators.required, Validators.email]],
//     password: ['', [Validators.required]]
//   });

//   // submit(): void {
//   //   if (this.form.invalid) {
//   //     this.form.markAllAsTouched();
//   //     return;
//   //   }

//   //   this.loading = true;
//   //   this.error = null;

//   //   this.authService.login({
//   //     email: this.form.get('email')!.value,
//   //     password: this.form.get('password')!.value
//   //   }).subscribe({
//   //     next: () => {
//   //       // login OK -> redirection (mets l’URL que tu veux comme "home")
//   //       this.router.navigate(['/home'], { replaceUrl: true });
//   //     },
//   //     error: (err) => {
//   //       this.error = err?.error?.message || 'Identifiants invalides.';
//   //       this.loading = false;
//   //     }
//   //   });
//   // }
//   // src/app/auth/login/login.component.ts
//   submit(): void {
//     if (this.form.invalid) {
//       this.form.markAllAsTouched();
//       return;
//     }

//     this.loading = true;
//     this.error = null;

//     this.authService.login({
//       email: this.form.get('email')!.value,
//       password: this.form.get('password')!.value
//     }).subscribe({
//       next: (res) => {
//         // Sécurité: vérifie qu’on a bien un token
//         if (!res?.token) {
//           this.error = 'Réponse de login invalide (pas de token).';
//           this.loading = false;
//           return;
//         }

//         // 2) Redirige vers /home
//         this.router.navigate(['/home'], { replaceUrl: true });
//       },
//       error: (err) => {
//         this.error = err?.error?.message || 'Identifiants invalides.';
//         this.loading = false;
//       }
//     });
//   }
// }
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  loading = false;
  error: string | null = null;

  form: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = null;

    this.authService.login({
      email: this.form.get('email')!.value,
      password: this.form.get('password')!.value
    }).subscribe({
      next: () => {
        // Le token est déjà stocké par AuthService (sdr_jwt)
        this.router.navigate(['/home'], { replaceUrl: true });
      },
      error: (err) => {
        this.error = err?.error?.message || 'Identifiants invalides.';
        this.loading = false;
      }
    });
  }
}

