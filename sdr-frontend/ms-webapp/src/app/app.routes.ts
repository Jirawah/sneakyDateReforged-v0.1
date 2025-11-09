import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicOnlyGuard } from './core/guards/public-only.guard';

export const routes: Routes = [
  // ZONE PUBLIQUE (non connecté)
  {
    path: '',
    canMatch: [publicOnlyGuard],
    loadComponent: () =>
      import('./layout/public/public-layout/public-layout.component')
        .then(c => c.PublicLayoutComponent),
    children: [
      {
        path: '',
        pathMatch: 'full',
        loadComponent: () =>
          import('./pages/public-home/public-home.component')
            .then(m => m.PublicHomeComponent),
        data: { hideLogo: true },
      },
      {
        path: 'auth',
        loadChildren: () =>
          import('./auth/auth.routes').then(m => m.AUTH_ROUTES),
      },
      {
        path: 'forgot-password',
        loadComponent: () =>
          import('./pages/forgot-password/forgot-password.component')
            .then(m => m.ForgotPasswordComponent),
      },
      {
        path: 'reset-password',
        loadComponent: () =>
          import('./pages/reset-password/reset-password.component')
            .then(m => m.ResetPasswordComponent),
      },
    ],
  },

  // ZONE APPLI (connecté)
  {
    path: '',
    canMatch: [authGuard],
    loadComponent: () =>
      import('./layout/app/app-layout/app-layout.component')
        .then(c => c.AppLayoutComponent),
    children: [
      // redirect par défaut dans l’app
      { path: '', pathMatch: 'full', redirectTo: 'home' },
      {
        path: 'home',
        loadComponent: () =>
          import('./pages/app-home/app-home.component')
            .then(m => m.AppHomeComponent),
      },
    ],
  },

  // 🧹 fallback
  { path: '**', redirectTo: '' },
];




