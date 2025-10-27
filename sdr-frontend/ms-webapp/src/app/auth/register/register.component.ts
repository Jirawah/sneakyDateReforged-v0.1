// import { Component, OnDestroy, inject } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { Router, RouterLink } from '@angular/router';
// import { MatFormFieldModule } from '@angular/material/form-field';
// import { MatInputModule } from '@angular/material/input';
// import { MatButtonModule } from '@angular/material/button';
// import { MatCheckboxModule } from '@angular/material/checkbox';
// import { CommonModule } from '@angular/common';
// import { environment } from '../../../environments/environment';
// import { Subscription, interval, of } from 'rxjs';
// import { catchError, switchMap } from 'rxjs/operators';
// import { AuthService } from '../../core/services/auth.service';

// @Component({
//   selector: 'app-register',
//   standalone: true,
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     RouterLink,
//     MatFormFieldModule,
//     MatInputModule,
//     MatButtonModule,
//     MatCheckboxModule
//   ],
//   templateUrl: './register.component.html',
//   styleUrls: ['./register.component.scss']
// })
// export class RegisterComponent implements OnDestroy {
//   private fb = inject(FormBuilder);
//   private router = inject(Router);
//   private authService = inject(AuthService);

//   loading = false;
//   error: string | null = null;

//   private discordPollSub?: Subscription;
//   private discordState?: string; // ✅ on garde le "state" renvoyé par le back

//   // ✅ plus de champ "pseudo"
//   form: FormGroup = this.fb.group({
//     email: ['', [Validators.required, Validators.email]],
//     steamId: [''],
//     discordConnected: [{ value: false, disabled: true }], // 🔒 non cliquable
//     password: ['', [Validators.required, Validators.minLength(12)]],
//     confirmPassword: ['', [Validators.required]]
//   }, { validators: [passwordsMatchValidator()] });

//   ngOnDestroy(): void {
//     this.discordPollSub?.unsubscribe();
//   }

//   openDiscordInvite(): void {
//     this.error = null;

//     // 1) Demande un "state" au backend
//     this.authService.createDiscordPending().subscribe({
//       next: ({ state }) => {
//         this.discordState = state;

//         // 2) Ouvre Discord
//         const url = environment.discordInviteUrl || 'https://discord.com/app';
//         window.open(url, '_blank', 'noopener,noreferrer');

//         // 3) Démarre le polling par state
//         const start = Date.now();
//         this.discordPollSub?.unsubscribe();

//         this.discordPollSub = interval(2000).pipe(
//           switchMap(() => this.authService.getDiscordStatusByState(state)),
//           catchError(() => of({ connected: false }))
//         ).subscribe(res => {
//           if (res?.connected) {
//             this.form.get('discordConnected')?.setValue(true, { emitEvent: false });
//             this.discordPollSub?.unsubscribe();
//           }
//           if (Date.now() - start > 120000) { // 2 min de polling max
//             this.discordPollSub?.unsubscribe();
//           }
//         });
//       },
//       error: () => {
//         this.error = 'Impossible de démarrer la connexion Discord (pending).';
//       }
//     });
//   }

//   async submit(): Promise<void> {
//     if (this.form.invalid || !this.form.get('discordConnected')?.value) {
//       this.form.markAllAsTouched();
//       return;
//     }

//     this.loading = true;
//     this.error = null;

//     try {
//       // Exemple quand tu brancheras vraiment l’AuthService.register :
//       // const payload = this.form.getRawValue(); // inclut les disabled
//       // await this.authService.register(payload).toPromise();
//       // this.router.navigateByUrl('/auth/login');
//     } catch (e: any) {
//       this.error = e?.error?.message || 'Une erreur est survenue.';
//     } finally {
//       this.loading = false;
//     }
//   }
// }

// /** Validator : password === confirmPassword */
// export function passwordsMatchValidator() {
//   return (group: FormGroup) => {
//     const p = group.get('password')?.value;
//     const c = group.get('confirmPassword')?.value;
//     return p && c && p === c ? null : { passwordsMismatch: true };
//   };
// }





// import { Component, OnDestroy, inject } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { Router, RouterLink } from '@angular/router';
// import { MatFormFieldModule } from '@angular/material/form-field';
// import { MatInputModule } from '@angular/material/input';
// import { MatButtonModule } from '@angular/material/button';
// import { MatCheckboxModule } from '@angular/material/checkbox';
// import { CommonModule } from '@angular/common';
// import { environment } from '../../../environments/environment';
// import { Subscription, interval, of } from 'rxjs';
// import { catchError, switchMap } from 'rxjs/operators';
// import { AuthService } from '../../core/services/auth.service';

// @Component({
//   selector: 'app-register',
//   standalone: true,
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     RouterLink,
//     MatFormFieldModule,
//     MatInputModule,
//     MatButtonModule,
//     MatCheckboxModule
//   ],
//   templateUrl: './register.component.html',
//   styleUrls: ['./register.component.scss']
// })
// export class RegisterComponent implements OnDestroy {
//   private fb = inject(FormBuilder);
//   private router = inject(Router);
//   private authService = inject(AuthService);

//   loading = false;
//   error: string | null = null;

//   private discordPollSub?: Subscription;
//   private discordState?: string; // ✅ le "state" renvoyé par le back pour corréler l'utilisateur

//   // 👉 On garde le même FormGroup mais on rend steamId obligatoire
//   // 👉 discordConnected reste dans le form (même si la checkbox HTML est disabled)
//   form: FormGroup = this.fb.group(
//     {
//       email: ['', [Validators.required, Validators.email]],
//       steamId: ['', [Validators.required]], // ⬅ ICI : maintenant requis
//       discordConnected: [{ value: false, disabled: true }], // rempli automatiquement après Discord
//       password: ['', [Validators.required, Validators.minLength(12)]],
//       confirmPassword: ['', [Validators.required]]
//     },
//     {
//       validators: [passwordsMatchValidator()]
//     }
//   );

//   ngOnDestroy(): void {
//     this.discordPollSub?.unsubscribe();
//   }

//   openDiscordInvite(): void {
//     this.error = null;

//     // 1) Demande un "state" au backend pour lier cette session navigateur
//     this.authService.createDiscordPending().subscribe({
//       next: ({ state }) => {
//         this.discordState = state;

//         // 2) Ouvre Discord (ton serveur / ton lien d'invite)
//         const url = environment.discordInviteUrl || 'https://discord.com/app';
//         window.open(url, '_blank', 'noopener,noreferrer');

//         // 3) Démarre le polling régulier sur /discord/status?state=...
//         const start = Date.now();
//         this.discordPollSub?.unsubscribe();

//         this.discordPollSub = interval(2000).pipe(
//           switchMap(() => this.authService.getDiscordStatusByState(state)),
//           catchError(() => of({ connected: false })) // si erreur réseau → on considère pas encore connecté
//         ).subscribe(res => {
//           if (res?.connected) {
//             // coche la case (coté form, pas côté DOM)
//             this.form.get('discordConnected')?.setValue(true, { emitEvent: false });

//             // plus tard on viendra ajouter: this.discordPseudoFromBackend = res.discordPseudo;
//             // pour l’instant on ne touche pas encore à ça

//             // stop polling
//             this.discordPollSub?.unsubscribe();
//           }

//           // sécurité : on arrête après 2 minutes même si pas connecté
//           if (Date.now() - start > 120000) {
//             this.discordPollSub?.unsubscribe();
//           }
//         });
//       },
//       error: () => {
//         this.error = 'Impossible de démarrer la connexion Discord (pending).';
//       }
//     });
//   }

//   async submit(): Promise<void> {
//     // On garde la logique existante pour l’instant
//     if (this.form.invalid || !this.form.get('discordConnected')?.value) {
//       this.form.markAllAsTouched();
//       return;
//     }

//     this.loading = true;
//     this.error = null;

//     try {
//       // Ici on branchera l'appel réel au backend (register)
//       // avec le payload complet (pseudo Discord, email, steamId, etc.).
//       //
//       // const payload = { ... };
//       // await this.authService.register(payload).toPromise();
//       // this.router.navigateByUrl('/auth/login');
//     } catch (e: any) {
//       this.error = e?.error?.message || 'Une erreur est survenue.';
//     } finally {
//       this.loading = false;
//     }
//   }
// }

// /** Validator : password === confirmPassword */
// export function passwordsMatchValidator() {
//   return (group: FormGroup) => {
//     const p = group.get('password')?.value;
//     const c = group.get('confirmPassword')?.value;
//     return p && c && p === c ? null : { passwordsMismatch: true };
//   };
// }
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

// ⬇⬇⬇ On importe l'interface RegisterRequest pour typer le payload envoyé au backend
import { RegisterRequest } from '../../shared/models/auth';

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

  // -- Discord / état de liaison
  private discordPollSub?: Subscription;
  private discordState?: string; // state renvoyé par /discord/pending

  // ✅ On stockera ici le pseudo Discord renvoyé par le back (discordPseudo)
  //    et c'est ça qui deviendra "pseudo" dans le register final.
  discordPseudoFromBackend: string | null = null;

  // Formulaire d'inscription
  form: FormGroup = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      steamId: ['', [Validators.required]], // requis par le backend
      discordConnected: [{ value: false, disabled: true }], // sera coché après Discord
      password: ['', [Validators.required, Validators.minLength(12)]],
      confirmPassword: ['', [Validators.required]]
    },
    {
      validators: [passwordsMatchValidator()]
    }
  );

  ngOnDestroy(): void {
    this.discordPollSub?.unsubscribe();
  }

  /**
   * Lance la connexion Discord :
   * 1. appelle /discord/pending → récupère le state
   * 2. ouvre Discord
   * 3. commence à poll /discord/status?state=...
   */
  openDiscordInvite(): void {
    this.error = null;

    this.authService.createDiscordPending().subscribe({
      next: ({ state }) => {
        this.discordState = state;

        // Ouvre Discord dans un nouvel onglet pour que l'utilisateur rejoigne le vocal
        const url = environment.discordInviteUrl || 'https://discord.com/app';
        window.open(url, '_blank', 'noopener,noreferrer');

        // Démarre le polling Discord
        const start = Date.now();
        this.discordPollSub?.unsubscribe();

        this.discordPollSub = interval(2000).pipe(
          switchMap(() => this.authService.getDiscordStatusByState(state)),
          catchError(() => of({ connected: false, discordPseudo: null }))
        ).subscribe((res: any) => {
          // IMPORTANT: `(res: any)` enlève l'erreur rouge sur res.discordPseudo

          // res doit ressembler à { connected: boolean, discordPseudo: string | null }
          if (res?.connected) {
            // 1. Coche la case dans le form
            this.form.get('discordConnected')?.setValue(true, { emitEvent: false });

            // 2. Sauvegarde le pseudo Discord renvoyé par le back
            //    → on l'utilisera comme "pseudo" pour l'inscription
            this.discordPseudoFromBackend = res.discordPseudo || null;

            // 3. Stop polling
            this.discordPollSub?.unsubscribe();
          }

          // Sécurité: on arrête après 2 minutes max
          if (Date.now() - start > 120000) {
            this.discordPollSub?.unsubscribe();
          }
        });
      },
      error: () => {
        this.error = 'Impossible de démarrer la connexion Discord (pending).';
      }
    });
  }

  /**
   * Soumission du formulaire d'inscription
   */
  submit(): void {
    // blocage si form pas valide ou Discord pas validé
    if (this.form.invalid || !this.form.get('discordConnected')?.value) {
      this.form.markAllAsTouched();
      return;
    }

    // On doit avoir récupéré le pseudo Discord via le polling
    const finalPseudo = this.discordPseudoFromBackend ?? '';
    if (!finalPseudo) {
      this.error = 'Impossible de récupérer ton pseudo Discord. Recommence la connexion Discord.';
      return;
    }

    this.loading = true;
    this.error = null;

    // Payload attendu par le backend (RegisterRequest)
    const payload: RegisterRequest = {
      pseudo: finalPseudo,
      email: this.form.get('email')!.value,
      steamId: this.form.get('steamId')!.value,
      password: this.form.get('password')!.value,
      confirmPassword: this.form.get('confirmPassword')!.value,
      // ms-auth considère discordId comme optionnel -> pour l'instant on envoie null
      discordId: undefined
    };

    this.authService.register(payload).subscribe({
      next: () => {
        // Succès -> on redirige vers la page login
        this.router.navigateByUrl('/auth/login');
      },
      error: (err) => {
        console.error(err);
        this.error = err?.error?.message || 'Une erreur est survenue.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
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


