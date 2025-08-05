# Plan de Tests du microservice `ms-auth`

---

## ✅ Tests unitaires

| Fichier                                   | Méthodes / Logiques à tester                                      | Testé ? |
|-------------------------------------------|-------------------------------------------------------------------|---------|
| `service/AuthService.java`                | `register()`, `login()`, valid / invalid / duplicata             | ✅      |
| `service/SteamVerificationService.java`   | `verifySteamUser()`, gestion d'erreurs API                       | ✅      |
| `service/PasswordResetService.java`       | `requestReset()`, `resetPassword()` avec tokens expirés / valides | ✅      |
| `service/UserAuthService.java`            | `updateSteamProfile()`, `syncDiscordProfile()`                   | ✅      |
| `service/UserAuthDetailsService.java`     | `loadUserByUsername()` (Spring Security)                         | ❌      |
| `service/DiscordSyncService.java`         | `syncDiscord()` avec User existant ou non                        | ❌      |
| `procedure/RegisterProcedureExecutor.java`| `execute()` avec différents codes retour                         | ❌      |
| `security/JwtUtils.java`                  | `generateToken()`, `validateToken()`, `extractClaims()`          | ❌      |

---

## 🔁 Tests d’intégration

| Fichier / Endpoint                                 | Comportements / scénarios couverts                                  | Testé ? |
|----------------------------------------------------|----------------------------------------------------------------------|---------|
| `controller/AuthController.java`                   | `POST /auth/register`, `POST /auth/login`, `POST /auth/discord-sync`| ❌      |
| `controller/PasswordResetController.java`          | `POST /auth/reset/request`, `POST /auth/reset/confirm`              | ❌      |
| `service/AuthService.java`                         | appels combinés : Steam, JWT, procédure SQL, repo                   | ✅ (partiel) |
| `service/SteamVerificationService.java`            | simulation appel HTTP Steam via WireMock/TestRestTemplate           | ❌      |
| `procedure/RegisterProcedureExecutor.java`         | appel réel de procédure stockée MySQL                               | ❌      |
| `security/JwtAuthFilter.java`                      | filtrage des requêtes entrantes avec token                          | ❌      |
| `bot/DiscordBot.java`                              | écoute d’événements Discord (optionnel)                             | ❌      |
| `application.properties` via `@SpringBootTest`     | profil de test, injection, sécurité, config                         | ✅ (skipped pour l’instant) |

---

## 📊 Résumé

- **Tests unitaires implémentés** : `AuthServiceTest`, `SteamVerificationServiceTest`, `PasswordResetServiceTest`, `UserAuthServiceTest`
- **Tests d’intégration en attente** : tous, à préparer avec `@SpringBootTest` ou `Testcontainers`
- **Tests unitaires restants** : 4 fichiers prioritaires à couvrir
