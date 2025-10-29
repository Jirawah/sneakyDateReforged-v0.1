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

// // ⬇⬇⬇ On importe l'interface RegisterRequest pour typer le payload envoyé au backend
// import { RegisterRequest } from '../../shared/models/auth';

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

//   // -- Discord / état de liaison
//   private discordPollSub?: Subscription;
//   private discordState?: string; // state renvoyé par /discord/pending

//   // ✅ On stockera ici le pseudo Discord renvoyé par le back (discordPseudo)
//   //    et c'est ça qui deviendra "pseudo" dans le register final.
//   discordPseudoFromBackend: string | null = null;

//   // Formulaire d'inscription
//   form: FormGroup = this.fb.group(
//     {
//       email: ['', [Validators.required, Validators.email]],
//       steamId: ['', [Validators.required]], // requis par le backend
//       discordConnected: [{ value: false, disabled: true }], // sera coché après Discord
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

//   /**
//    * Lance la connexion Discord :
//    * 1. appelle /discord/pending → récupère le state
//    * 2. ouvre Discord
//    * 3. commence à poll /discord/status?state=...
//    */
//   openDiscordInvite(): void {
//     this.error = null;

//     this.authService.createDiscordPending().subscribe({
//       next: ({ state }) => {
//         this.discordState = state;

//         // Ouvre Discord dans un nouvel onglet pour que l'utilisateur rejoigne le vocal
//         const url = environment.discordInviteUrl || 'https://discord.com/app';
//         window.open(url, '_blank', 'noopener,noreferrer');

//         // Démarre le polling Discord
//         const start = Date.now();
//         this.discordPollSub?.unsubscribe();

//         this.discordPollSub = interval(2000).pipe(
//           switchMap(() => this.authService.getDiscordStatusByState(state)),
//           catchError(() => of({ connected: false, discordPseudo: null }))
//         ).subscribe((res: any) => {
//           // IMPORTANT: `(res: any)` enlève l'erreur rouge sur res.discordPseudo

//           // res doit ressembler à { connected: boolean, discordPseudo: string | null }
//           if (res?.connected) {
//             // 1. Coche la case dans le form
//             this.form.get('discordConnected')?.setValue(true, { emitEvent: false });

//             // 2. Sauvegarde le pseudo Discord renvoyé par le back
//             //    → on l'utilisera comme "pseudo" pour l'inscription
//             this.discordPseudoFromBackend = res.discordPseudo || null;

//             // 3. Stop polling
//             this.discordPollSub?.unsubscribe();
//           }

//           // Sécurité: on arrête après 2 minutes max
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

//   /**
//    * Soumission du formulaire d'inscription
//    */
//   submit(): void {
//     // blocage si form pas valide ou Discord pas validé
//     if (this.form.invalid || !this.form.get('discordConnected')?.value) {
//       this.form.markAllAsTouched();
//       return;
//     }

//     // On doit avoir récupéré le pseudo Discord via le polling
//     const finalPseudo = this.discordPseudoFromBackend ?? '';
//     if (!finalPseudo) {
//       this.error = 'Impossible de récupérer ton pseudo Discord. Recommence la connexion Discord.';
//       return;
//     }

//     this.loading = true;
//     this.error = null;

//     // Payload attendu par le backend (RegisterRequest)
//     const payload: RegisterRequest = {
//       pseudo: finalPseudo,
//       email: this.form.get('email')!.value,
//       steamId: this.form.get('steamId')!.value,
//       password: this.form.get('password')!.value,
//       confirmPassword: this.form.get('confirmPassword')!.value,
//       // ms-auth considère discordId comme optionnel -> pour l'instant on envoie null
//       discordId: undefined
//     };

//     this.authService.register(payload).subscribe({
//       next: () => {
//         // Succès -> on redirige vers la page login
//         this.router.navigateByUrl('/auth/login');
//       },
//       error: (err) => {
//         console.error(err);
//         this.error = err?.error?.message || 'Une erreur est survenue.';
//         this.loading = false;
//       },
//       complete: () => {
//         this.loading = false;
//       }
//     });
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
//   // injections
//   private fb = inject(FormBuilder);
//   private router = inject(Router);
//   private authService = inject(AuthService);

//   // état UI
//   loading = false;
//   error: string | null = null;

//   // polling Discord
//   private discordPollSub?: Subscription;
//   private discordState?: string;

//   // infos retournées par le backend quand l'utilisateur rejoint le vocal Discord
//   public discordPseudoFromBackend: string | null = null;
//   public discordIdFromBackend: string | null = null;

//   // formulaire d'inscription
//   form: FormGroup = this.fb.group(
//     {
//       email: ['', [Validators.required, Validators.email]],
//       steamId: ['', [Validators.required]],
//       discordConnected: [{ value: false, disabled: true }], // sera coché par le polling Discord
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

//   /**
//    * Lance le flux Discord :
//    *  1. on demande un "state" au backend
//    *  2. on ouvre Discord
//    *  3. on poll /discord/status?state=... jusqu'à ce que connected = true
//    *     -> à ce moment-là on remplit la checkbox + on retient le pseudo Discord
//    */
//   openDiscordInvite(): void {
//     this.error = null;

//     this.authService.createDiscordPending().subscribe({
//       next: ({ state }) => {
//         this.discordState = state;

//         // on ouvre ton serveur Discord (l'invite)
//         const url = environment.discordInviteUrl || 'https://discord.com/app';
//         window.open(url, '_blank', 'noopener,noreferrer');

//         // on démarre le polling régulier
//         const start = Date.now();
//         this.discordPollSub?.unsubscribe();

//         this.discordPollSub = interval(2000).pipe(
//           switchMap(() =>
//             this.authService.getDiscordStatusByState(state)
//           ),
//           catchError(() =>
//             // si le GET /discord/status plante temporairement, on ne casse pas tout le flux
//             of({ connected: false, discordPseudo: null, discordId: null })
//           )
//         ).subscribe(status => {
//           if (status?.connected) {
//             // on coche la case Discord dans le form
//             this.form.get('discordConnected')?.setValue(true, { emitEvent: false });

//             // on garde les infos Discord en mémoire locale pour le register()
//             this.discordPseudoFromBackend = status.discordPseudo || null;
//             this.discordIdFromBackend = status.discordId || null;

//             // stop polling
//             this.discordPollSub?.unsubscribe();
//           }

//           // sécurité : arrêt du polling après 2 minutes
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

//   /**
//    * Soumission du formulaire d'inscription.
//    * Si tout est valide -> on appelle le backend /auth/register.
//    * Au succès -> redirection vers /auth/login.
//    */
//   submit(): void {
//     // blocage si form invalide ou discord pas validé
//     if (this.form.invalid || !this.form.get('discordConnected')?.value) {
//         this.form.markAllAsTouched();
//         return;
//     }

//     this.loading = true;
//     this.error = null;

//     // on choisit le pseudo final : priorité pseudo Discord
//     // (fallback "player" juste au cas où)
//     const finalPseudo = this.discordPseudoFromBackend || 'player';

//     // payload que le backend attend (RegisterRequest côté ms-auth)
//     const payload = {
//       pseudo: finalPseudo,
//       email: this.form.get('email')!.value,
//       steamId: this.form.get('steamId')!.value,
//       password: this.form.get('password')!.value,
//       confirmPassword: this.form.get('confirmPassword')!.value,
//       // ms-auth accepte discordId nullable -> on envoie l'id Discord capturé si on l'a
//       discordId: this.discordIdFromBackend ?? null
//     };

//     this.authService.register(payload).subscribe({
//       next: () => {
//         // ✅ Succès -> on envoie l'utilisateur vers la page login
//         this.router.navigateByUrl('/auth/login');
//       },
//       error: (err) => {
//         // ❌ Échec -> on reste ici et on affiche l'erreur
//         console.error('[REGISTER] error', err);
//         this.error = err?.error?.message || 'Impossible de créer le compte.';
//         this.loading = false;
//       }
//     });
//   }
// }

// /** Validator custom : password === confirmPassword */
// export function passwordsMatchValidator() {
//   return (group: FormGroup) => {
//     const p = group.get('password')?.value;
//     const c = group.get('confirmPassword')?.value;
//     return p && c && p === c ? null : { passwordsMismatch: true };
//   };
// }
import { Component, OnDestroy, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
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
    MatCheckboxModule,
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnDestroy {
  // injections
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  // état UI
  loading = false;
  error: string | null = null;

  // polling Discord
  private discordPollSub?: Subscription;
  private discordState?: string;

  // infos retournées par le backend quand l'utilisateur rejoint le vocal Discord
  public discordPseudoFromBackend: string | null = null;
  public discordIdFromBackend: string | null = null;

  // formulaire d'inscription
  form: FormGroup = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      steamId: ['', [Validators.required]],
      discordConnected: [{ value: false, disabled: true }], // sera coché par le polling Discord
      password: ['', [Validators.required, Validators.minLength(12)]],
      confirmPassword: ['', [Validators.required]],
    },
    {
      validators: [passwordsMatchValidator()],
    }
  );

  ngOnDestroy(): void {
    this.discordPollSub?.unsubscribe();
  }

  /**
   * Lance le flux Discord :
   *  1. on demande un "state" au backend
   *  2. on ouvre Discord
   *  3. on poll /discord/status?state=... jusqu'à ce que connected = true
   *     -> à ce moment-là on remplit la checkbox + on retient le pseudo Discord
   */
  openDiscordInvite(): void {
    this.error = null;

    this.authService.createDiscordPending().subscribe({
      next: ({ state }) => {
        this.discordState = state;

        // on ouvre ton serveur Discord (l'invite)
        const url = environment.discordInviteUrl || 'https://discord.com/app';
        window.open(url, '_blank', 'noopener,noreferrer');

        // on démarre le polling régulier
        const start = Date.now();
        this.discordPollSub?.unsubscribe();

        this.discordPollSub = interval(2000)
          .pipe(
            switchMap(() =>
              this.authService.getDiscordStatusByState(state)
            ),
            catchError(() =>
              // si le GET /discord/status plante temporairement, on ne casse pas tout le flux
              of({ connected: false, discordPseudo: null, discordId: null })
            )
          )
          .subscribe((status) => {
            if (status?.connected) {
              // on coche la case Discord dans le form
              this.form
                .get('discordConnected')
                ?.setValue(true, { emitEvent: false });

              // on garde les infos Discord en mémoire locale pour le register()
              this.discordPseudoFromBackend = status.discordPseudo || null;
              this.discordIdFromBackend = status.discordId || null;

              // stop polling
              this.discordPollSub?.unsubscribe();
            }

            // sécurité : arrêt du polling après 2 minutes
            if (Date.now() - start > 120000) {
              this.discordPollSub?.unsubscribe();
            }
          });
      },
      error: () => {
        this.error = 'Impossible de démarrer la connexion Discord (pending).';
      },
    });
  }

  /**
   * Soumission du formulaire d'inscription.
   * Si tout est valide -> on appelle le backend /auth/register.
   * Au succès -> redirection vers /auth/login.
   */
  submit(): void {
    // blocage si form invalide ou discord pas validé
    if (this.form.invalid || !this.form.get('discordConnected')?.value) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    // 🔒 sécurité : on ne tente pas register sans discordId capturé
    if (!this.discordIdFromBackend) {
      this.loading = false;
      this.error =
        "Discord non détecté. Merci de rejoindre le salon vocal Discord d'authentification avant de créer le compte.";
      return;
    }

    // on choisit le pseudo final : priorité pseudo Discord
    // (fallback "player" juste au cas où)
    const finalPseudo = this.discordPseudoFromBackend?.trim() || 'player';

    // payload que le backend attend (RegisterRequest côté ms-auth)
    const payload: RegisterRequest = {
      pseudo: finalPseudo,
      email: this.form.get('email')!.value,
      steamId: this.form.get('steamId')!.value,
      password: this.form.get('password')!.value,
      confirmPassword: this.form.get('confirmPassword')!.value,
      // on envoie l'id Discord réel capturé
      discordId: this.discordIdFromBackend,
    };

    this.authService.register(payload).subscribe({
      next: () => {
        // 🔄 on coupe le spinner ici aussi
        this.loading = false;

        // ✅ Succès -> on envoie l'utilisateur vers la page login
        this.router.navigateByUrl('/auth/login');
      },
      error: (err) => {
        // ❌ Échec -> on reste ici et on affiche l'erreur
        console.error('[REGISTER] error', err);
        this.error =
          err?.error?.message || 'Impossible de créer le compte.';
        this.loading = false;
      },
    });
  }
}

/** Validator custom : password === confirmPassword */
export function passwordsMatchValidator() {
  return (group: FormGroup) => {
    const p = group.get('password')?.value;
    const c = group.get('confirmPassword')?.value;
    return p && c && p === c ? null : { passwordsMismatch: true };
  };
}

