// // import { Routes } from '@angular/router';
// // import { authGuard } from './core/guards/auth.guard';
// // import { publicOnlyGuard } from './core/guards/public-only.guard';

// // export const routes: Routes = [
// //   // Zone publique (non connecté)
// //   {
// //     path: '',
// //     canMatch: [publicOnlyGuard],
// //     loadComponent: () =>
// //       import('./layout/public/public-layout/public-layout.component')
// //         .then(c => c.PublicLayoutComponent),
// //     children: [
// //       { path: '', pathMatch: 'full', redirectTo: 'auth/login' },
// //       { path: 'auth', loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES) },
// //     ],
// //   },

// //   // Zone appli (connecté)
// //   {
// //     path: '',
// //     canMatch: [authGuard],
// //     loadComponent: () =>
// //       import('./layout/app/app-layout/app-layout.component')
// //         .then(c => c.AppLayoutComponent),
// //     children: [
// //       { path: 'home', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
// //     ],
// //   },

// //   { path: '**', redirectTo: 'auth/login' },
// // ];
// import { Routes } from '@angular/router';
// import { authGuard } from './core/guards/auth.guard';
// import { publicOnlyGuard } from './core/guards/public-only.guard';

// export const routes: Routes = [
//   //
//   // 🌐 ZONE PUBLIQUE (utilisateur NON connecté)
//   //
//   {
//     path: '',
//     canMatch: [publicOnlyGuard],
//     loadComponent: () =>
//       import('./layout/public/public-layout/public-layout.component')
//         .then(c => c.PublicLayoutComponent),
//     children: [
//       // Page d'accueil publique "/"
//       {
//         path: '',
//         pathMatch: 'full',
//         loadComponent: () =>
//           import('./pages/public-home/public-home.component')
//             .then(m => m.PublicHomeComponent),
//       },

//       // Groupe auth -> /auth/login et /auth/register etc.
//       {
//         path: 'auth',
//         loadChildren: () =>
//           import('./auth/auth.routes').then(m => m.AUTH_ROUTES),
//       },
//     ],
//   },

//   //
//   // 🔐 ZONE APPLI (utilisateur CONNECTÉ)
//   //
//   {
//     path: '',
//     canMatch: [authGuard],
//     loadComponent: () =>
//       import('./layout/app/app-layout/app-layout.component')
//         .then(c => c.AppLayoutComponent),
//     children: [
//       {
//         path: 'home',
//         loadComponent: () =>
//           import('./pages/home/home.component')
//             .then(m => m.HomeComponent),
//       },
//     ],
//   },

//   //
//   // 🧹 FALLBACK
//   // Toute route inconnue -> accueil publique
//   //
//   { path: '**', redirectTo: '' },
// ];
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicOnlyGuard } from './core/guards/public-only.guard';

export const routes: Routes = [
  //
  // 🌐 ZONE PUBLIQUE (utilisateur NON connecté)
  //
  {
    path: '',
    canMatch: [publicOnlyGuard],
    loadComponent: () =>
      import('./layout/public/public-layout/public-layout.component')
        .then(c => c.PublicLayoutComponent),
    children: [
      // Page d'accueil publique "/"
      {
        path: '',
        pathMatch: 'full',
        loadComponent: () =>
          import('./pages/public-home/public-home.component')
            .then(m => m.PublicHomeComponent),
      },

      // Groupe auth -> /auth/login et /auth/register etc.
      {
        path: 'auth',
        loadChildren: () =>
          import('./auth/auth.routes').then(m => m.AUTH_ROUTES),
      },

      // ✅ Page "mot de passe oublié"
      // URL: /forgot-password
      {
        path: 'forgot-password',
        loadComponent: () =>
          import('./pages/forgot-password/forgot-password.component')
            .then(m => m.ForgotPasswordComponent),
      },

      // ✅ Page "nouveau mot de passe"
      // URL: /reset-password?token=XYZ
      {
        path: 'reset-password',
        loadComponent: () =>
          import('./pages/reset-password/reset-password.component')
            .then(m => m.ResetPasswordComponent),
      },
    ],
  },

  //
  // 🔐 ZONE APPLI (utilisateur CONNECTÉ)
  //
  {
    path: '',
    canMatch: [authGuard],
    loadComponent: () =>
      import('./layout/app/app-layout/app-layout.component')
        .then(c => c.AppLayoutComponent),
    children: [
      {
        path: 'home',
        loadComponent: () =>
          import('./pages/home/home.component')
            .then(m => m.HomeComponent),
      },
    ],
  },

  //
  // 🧹 FALLBACK
  // Toute route inconnue -> accueil publique
  //
  { path: '**', redirectTo: '' },
];

