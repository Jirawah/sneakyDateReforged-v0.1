| Fichier | Méthodes / Fonctionnalités à tester en intégration |
|---------|-----------------------------------------------------|
| `ms_auth/controller/AuthController.java` | `POST /auth/register`, `POST /auth/login`, `POST /auth/discord-sync` — vérifie le comportement REST complet avec validation, sécurité, enregistrement via procédure |
| `ms_auth/controller/PasswordResetController.java` | `POST /auth/reset/request`, `POST /auth/reset/confirm` — test de bout-en-bout avec base H2 ou Testcontainers |
| `ms_auth/service/AuthService.java` | registre plusieurs composants : JWT, Steam, UserRepo, PasswordEncoder, procédure SQL |
| `ms_auth/service/PasswordResetService.java` | interaction avec TokenRepo et UserRepo, vérification d’expiration, persistences en BDD |
| `ms_auth/service/SteamVerificationService.java` | appel à l’API Steam externe — à mocker ou tester via WireMock si API testable |
| `ms_auth/bot/DiscordBot.java` | démarrage / arrêt du bot Discord — uniquement si simulation Discord en test |
| `ms_auth/security/JwtAuthFilter.java` | traitement des tokens dans le contexte Spring Security |
| `ms_auth/procedure/RegisterProcedureExecutor.java` | exécution réelle de la procédure stockée en BDD |
| `application.properties` via `@SpringBootTest` | vérifie le chargement de config, sécurité, base MySQL (via Testcontainers ou docker-compose) |
