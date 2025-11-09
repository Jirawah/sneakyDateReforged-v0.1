import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const publicOnlyGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  // true = on peut entrer dans la zone publique (pas connecté)
  // false = on SKIP cette zone et le routeur passe à la suite (zone app)
  return !auth.isAuthenticated();
};
