# Actions à Mener – Microservice `ms-auth`

## Enregistrement (Register)
- Ajouter un **fallback** en cas d’échec ou d’indisponibilité de l’API Steam (timeout, message utilisateur).
- Externaliser la logique de mise à jour du `steamPseudo` et `steamAvatar` dans une méthode dédiée pour une meilleure testabilité.

## Connexion (Login)
- Implémenter une **synchronisation automatique des heures de jeu Steam** (via tâche cron ou déclenchée à intervalle régulier).

## Synchronisation Discord
- Sécuriser davantage la logique de **fallback via `pseudo` si `discordId` est introuvable**, pour éviter de mauvaises associations.
- Ajouter un champ `discordSyncedAt` dans `UserAuthModel` pour **tracer les synchronisations**.
- Ajouter des **logs ou alertes** backend/bot si une synchronisation échoue ou est incohérente.

## Sécurité
- Ajouter un **endpoint de refresh token** pour permettre le renouvellement du JWT sans relogin.
- Mettre en place une **expiration courte pour le token JWT** + stratégie de refresh côté frontend.

## Procédure stockée
- Ajouter des **logs métiers explicites** en cas de retour d’erreur (`p_result_code < 0`) dans `AuthService`.

## Structure technique
- Prévoir un **dossier `utils`** si des méthodes génériques (ex. : fallback avatar, parsing Steam) se multiplient.
- Segmenter davantage en **sous-packages** (`steam`, `discord`) si la logique métier associée s’étoffe.
