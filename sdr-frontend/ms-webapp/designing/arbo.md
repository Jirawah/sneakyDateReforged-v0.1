sdr-frontend/
└─ ms-webapp/
   ├─ angular.json
   ├─ package.json
   ├─ tsconfig*.json
   ├─ Dockerfile
   ├─ nginx.conf
   └─ src/
      ├─ main.ts
      ├─ index.html
      ├─ styles.scss                      # base + hook dark mode + tokens CSS
      ├─ styles/                          # (conception) SCSS organisé
      │  ├─ _variables.scss               # couleurs, spacing, radius...
      │  ├─ _mixins.scss
      │  └─ theme/
      │     ├─ material-light.scss        # thème Material clair (plus tard)
      │     └─ material-dark.scss         # thème Material sombre (plus tard)
      ├─ assets/
      │  ├─ images/
      │  ├─ icons/
      │  └─ i18n/                         # si multi-lang plus tard
      ├─ environments/
      │  ├─ environment.ts
      │  └─ environment.prod.ts
      └─ app/
         ├─ app.component.ts              # shell racine minimal (router-outlet)
         ├─ app.component.html
         ├─ app.routes.ts                 # routes racine (groupées par layout)
         │
         ├─ layout/                       # ★ Deux layouts (Public / App)
         │  ├─ public/
         │  │  ├─ public-layout.component.ts
         │  │  ├─ public-layout.component.html
         │  │  └─ public-layout.component.scss
         │  │
         │  │  └─ parts/                  # éléments communs au layout public
         │  │     ├─ header-public/
         │  │     └─ footer-public/
         │  │
         │  └─ app/
         │     ├─ app-layout.component.ts
         │     ├─ app-layout.component.html
         │     └─ app-layout.component.scss
         │
         │     └─ parts/                  # éléments communs au layout privé
         │        ├─ header-app/
         │        ├─ sidebar/             # (optionnel) si menu latéral plus tard
         │        └─ user-menu/
         │
         ├─ core/                         # “système” (singletons)
         │  ├─ guards/
         │  │  ├─ auth.guard.ts
         │  │  └─ role.guard.ts           # (si besoin plus tard)
         │  ├─ interceptors/
         │  │  ├─ jwt.interceptor.ts
         │  │  └─ error.interceptor.ts
         │  ├─ services/
         │  │  ├─ http/
         │  │  │  ├─ api-client.service.ts        # baseURL + HttpClient wrapper
         │  │  │  └─ token-storage.service.ts     # JWT/localStorage
         │  │  ├─ auth/
         │  │  │  └─ auth.service.ts             # états login/register
         │  │  ├─ theme.service.ts               # gestion dark/light
         │  │  └─ config.service.ts              # URLs ms-* (via environment)
         │  ├─ models/
         │  │  ├─ auth.models.ts
         │  │  ├─ user.models.ts
         │  │  ├─ rdv.models.ts
         │  │  ├─ friend.models.ts
         │  │  ├─ invitation.models.ts
         │  │  └─ common.types.ts
         │  ├─ tokens/
         │  │  └─ API_BASE_URL.token.ts          # InjectionToken
         │  └─ utils/
         │     ├─ forms.util.ts
         │     └─ date.util.ts
         │
         ├─ shared/                      # réutilisable UI (no business logic)
         │  ├─ components/
         │  │  ├─ form/
         │  │  │  ├─ sd-input/
         │  │  │  ├─ sd-select/
         │  │  │  └─ sd-password/
         │  │  ├─ buttons/
         │  │  ├─ status/
         │  │  │  └─ status-pill/
         │  │  └─ table/
         │  ├─ directives/
         │  ├─ pipes/
         │  └─ material/
         │     └─ material.providers.ts          # centralisation imports/providers Mat
         │
         ├─ pages/                     # pages “publiques” simples
         │  └─ home/
         │     ├─ home.component.ts
         │     ├─ home.component.html
         │     └─ home.component.scss
         │
         ├─ auth/                      # domaine /auth (layout Public)
         │  ├─ auth.routes.ts
         │  ├─ pages/
         │  │  ├─ login/
         │  │  ├─ register/
         │  │  └─ reset-password/
         │  │     ├─ request/          # saisie email
         │  │     └─ confirm/          # nouveau mdp
         │  └─ data-access/            # services + models spécifiques (si besoin)
         │
         ├─ profile/                   # domaine /profile (layout App)
         │  ├─ profile.routes.ts
         │  ├─ pages/
         │  │  └─ profile/
         │  └─ widgets/                # cards, avatar blocks, stats temps de jeu...
         │
         ├─ rdv/                       # domaine /rdv (planning, mes rdv, détail, création)
         │  ├─ rdv.routes.ts
         │  ├─ pages/
         │  │  ├─ planning/
         │  │  ├─ my-rdv/
         │  │  ├─ detail/
         │  │  └─ create/
         │  └─ widgets/                # calendrier, listes, filtres, etc.
         │
         ├─ invitation/                # domaine /invitation (layout App)
         │  ├─ invitation.routes.ts
         │  └─ pages/
         │     └─ invitation-list/
         │
         ├─ friend/                    # domaine /friend (amis + listes + profil ami)
         │  ├─ friend.routes.ts
         │  ├─ pages/
         │  │  ├─ friends/
         │  │  ├─ lists/
         │  │  │  ├─ list-index/
         │  │  │  ├─ list-create/
         │  │  │  └─ list-detail/
         │  │  └─ friend-profile/
         │  └─ widgets/                # tuiles ami, status online/offline, etc.
         │
         ├─ search/                    # domaine /search (recherche joueurs)
         │  ├─ search.routes.ts
         │  └─ pages/
         │     └─ player-search/
         │
         └─ notif/                     # domaine /notif (centre de notifications)
            ├─ notif.routes.ts
            └─ pages/
               └─ notif-center/
