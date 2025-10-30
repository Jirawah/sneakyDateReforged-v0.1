// // src/app/core/guards/public-only.guard.ts
// import { inject } from '@angular/core';
// import { CanMatchFn, Router, UrlTree } from '@angular/router';
// import { AuthService } from '../services/auth.service';

// export const publicOnlyGuard: CanMatchFn = (): boolean | UrlTree => {
//   const auth = inject(AuthService);
//   const router = inject(Router);

//   // ✅ Si déjà connecté, on évite les pages publiques et on redirige vers /home
//   return auth.isAuthenticated() ? router.createUrlTree(['/home']) : true;
// };
// src/app/core/guards/public-only.guard.ts
// import { inject } from '@angular/core';
// import { CanMatchFn, Router, UrlTree } from '@angular/router';
// import { AuthService } from '../services/auth.service';

// export const publicOnlyGuard: CanMatchFn = (): boolean | UrlTree => {
//   const auth = inject(AuthService);
//   const router = inject(Router);
//   return auth.isAuthenticated() ? router.createUrlTree(['/home']) : true;
// };
// src/app/core/guards/public-only.guard.ts
import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const publicOnlyGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  // true = on peut entrer dans la zone publique (pas connecté)
  // false = on SKIP cette zone et le routeur passe à la suite (zone app)
  return !auth.isAuthenticated();
};

