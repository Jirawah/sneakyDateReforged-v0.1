// // src/app/core/guards/auth.guard.ts
// import { inject } from '@angular/core';
// import { CanMatchFn, Router, UrlTree } from '@angular/router';
// import { AuthService } from '../services/auth.service';

// export const authGuard: CanMatchFn = (): boolean | UrlTree => {
//   const auth = inject(AuthService);
//   const router = inject(Router);

//   // ✅ Autoriser la zone "app" uniquement si le JWT existe
//   return auth.isAuthenticated() ? true : router.createUrlTree(['/auth/login']);
// };

// src/app/core/guards/auth.guard.ts
// import { inject } from '@angular/core';
// import { CanMatchFn, Router, UrlTree } from '@angular/router';
// import { AuthService } from '../services/auth.service';

// export const authGuard: CanMatchFn = (): boolean | UrlTree => {
//   const auth = inject(AuthService);
//   const router = inject(Router);
//   return auth.isAuthenticated() ? true : router.createUrlTree(['/auth/login']);
// };
// src/app/core/guards/auth.guard.ts
import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated() ? true : router.createUrlTree(['/auth/login']);
};

