# Revue d’Architecture Technique – `ms-auth`

## Objectif

Comparer la conception initiale (UML, MCD, MPD) avec l’implémentation réelle du microservice `ms-auth`, en identifiant :

- les correspondances conformes,
- les écarts justifiés,
- les éventuelles améliorations à prévoir.

---

## 1. Modèle de Données – `UserAuthModel`

| Élément                                                                 | Présence | Commentaire                                              |
|-------------------------------------------------------------------------|----------|-----------------------------------------------------------|
| `email`, `pseudo`, `steamId`                                            | ✅       | Champs obligatoires et uniques                           |
| `password`                                                              | ✅       | Stocké en hash BCrypt                                     |
| `steamPseudo`, `steamAvatar`                                           | ✅       | Rempli via API Steam                                      |
| `pubgHours`, `rustHours`, `amongUsHours`                                | ✅       | Prévu mais pas encore automatiquement alimenté            |
| `discordId`, `discordUsername`, `discordDiscriminator`, `discordNickname`, `discordAvatarUrl` | ✅ | Remplis dynamiquement via bot Discord                     |
| `discordValidated`, `steamValidated`                                    | ✅       | Gèrent l’état de validation                               |
| `role`                                                                  | ✅       | Par défaut à `"USER"`                                     |
| `createdAt`, `updatedAt`                                                | ✅       | Gérés automatiquement via `@PrePersist` et `@PreUpdate`  |

> **Conclusion** : modèle complet et conforme au MCD/MPD, avec une extensibilité future intégrée.

---

## 2. Inscription (`register`)

- ✅ Vérification unicité (`email`, `pseudo`, `steamId`)
- ✅ Vérification Steam (via `steamVerificationService`)
- ✅ Vérification bannissement Steam
- ✅ Appel à la procédure stockée `sp_register_user`
- ✅ Récupération automatique des données `steamPseudo`, `steamAvatar`
- ✅ Génération du JWT à l’inscription

>  **Conclusion** : l’inscription respecte les exigences métier et techniques, avec un bon niveau de sécurité.

---

## 3. Connexion (`login`)

- ✅ Authentification via `AuthenticationManager`
- ✅ Génération du JWT
- ✅ Récupération du pseudo/avatars et des heures de jeu depuis la base

>  **Conclusion** : flux conforme au cas d’utilisation UML de connexion.

---

## 4. Synchronisation Discord

- ✅ Appel au backend via `DiscordSyncRequestDTO`
- ✅ Enregistrement ou mise à jour de l’utilisateur via `UserAuthService`
- ✅ Activation du flag `discordValidated`
- ✅ Utilisation des champs publics (pseudo, avatar, nickname…)

>  **Conclusion** : bien intégré et aligné avec les spécifications du système (fonctionnement à l’inscription ou à la demande).

---

## 5. Sécurité

| Élément                                    | Statut | Commentaire                  |
|--------------------------------------------|--------|-------------------------------|
| Mot de passe crypté                        | ✅     | BCrypt                        |
| JWT (création + validation)                | ✅     | `JwtUtils` bien en place      |
| Filtrage des requêtes sécurisées           | ✅     | Présence de filtres           |
| Vérification d’email/token Steam/Discord   | ✅     | Conforme                      |

---

## 6. Procédure stockée `sp_register_user`

- ✅ Bien appelée depuis le service
- ✅ Prend en compte les cas de doublon
- ✅ Utilise des codes de retour (`p_result_code`)
- ✅ Initialise les données essentielles dès l’inscription

---

## 7. Structure technique (packages)

| Élément                               | Présence | Commentaire                              |
|---------------------------------------|----------|-------------------------------------------|
| `model`, `repository`, `service`, `dto`, `controller`, `security` | ✅       | Bonne séparation des responsabilités     |
| `UserAuthService` dédié               | ✅       | Bon choix pour découpler la logique métier |
| Intégration Steam/Discord séparée     | ✅       | Permet l’évolutivité et le test unitaire |

---

## Résumé global

| Critère                          | État       |
|----------------------------------|------------|
| Conformité avec les diagrammes   | ✅ Très bon |
| Respect des règles métier        | ✅ Complet  |
| Structure technique              | ✅ Solide   |
| Robustesse et sécurité           | ✅ Bonne    |
| Évolutivité                      | ✅ Prête pour extension (rôles, heures de jeu, etc.) |
