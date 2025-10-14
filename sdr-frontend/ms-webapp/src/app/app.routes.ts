import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicOnlyGuard } from './core/guards/public-only.guard';

export const routes: Routes = [
  // Zone publique (non connecté)
  {
    path: '',
    canMatch: [publicOnlyGuard],
    loadComponent: () =>
      import('./layout/public/public-layout/public-layout.component')
        .then(c => c.PublicLayoutComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'auth/login' },
      { path: 'auth', loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES) },
    ],
  },

  // Zone appli (connecté)
  {
    path: '',
    canMatch: [authGuard],
    loadComponent: () =>
      import('./layout/app/app-layout/app-layout.component')
        .then(c => c.AppLayoutComponent),
    children: [
      { path: 'home', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
    ],
  },

  { path: '**', redirectTo: 'auth/login' },
];
