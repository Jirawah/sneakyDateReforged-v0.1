# SneakyDateReforged

**SneakyDateReforged** is a web application designed to help gamers schedule **cheater-free** meetups on three popular Steam games: **Rust**, **PUBG**, and **Among Us**. The platform aims to gradually expand to support more titles available on Steam.

---

## Application Purpose

The goal of **SneakyDateReforged** is to allow players to virtually meet through planned gaming sessions. The app offers a complete set of social features to organize, join, and manage these events while ensuring trust and moderation through:

- **Steam identity verification** (ban status check via the Steam Web API)
- **Discord voice channel connection** to confirm live presence
- Advanced management of **friends**, **friend lists**, **invitations**, and **notifications**

---

## Architecture

The application is built on a **modular microservices architecture**, allowing for scalability and loose coupling between components:

| Microservice       | Main Role                                      |
|--------------------|------------------------------------------------|
| `ms-auth`          | User registration, login, Steam & Discord integration |
| `ms-rdv`           | Creating, editing, and displaying gaming meetups |
| `ms-invitation`    | Managing invitations and responses to meetups |
| `ms-friend`        | Handling friends and friend lists             |
| `ms-profil`        | Viewing and searching user profiles           |
| `ms-notif`         | Sending and managing notifications            |
| `ms-configserver`  | Centralized configuration management          |
| `ms-eurekaserver`  | Service discovery with Eureka                 |
| `angular-frontend` | User-facing frontend built with Angular 17    |

---

## Technologies Used

### Backend
- **Java 17**
- **Spring Boot 3**
- **Spring Cloud** (Config Server, Eureka, Feign)
- **Spring Security + JWT**
- **JPA / Hibernate (MySQL)** and **Spring Data MongoDB**
- **Docker & Docker Compose**
- **JUnit / Mockito** for unit and integration testing
- **Stored Procedures** in MySQL for encapsulating repetitive business logic

### Frontend
- **Angular 17** (standalone components)
- **Angular Material**
- **JWT authentication with route guards**
- **HTTP services linked to the backend microservices**

---

## Testing

The `ms-auth` microservice features **complete coverage through unit and integration tests**, ensuring stability and reliability.

- **Unit tests**: service layers, token logic, password encoders, Steam/Discord logic
- **Integration tests**: REST endpoints, Dockerized MySQL, full JWT flows
- **Mocking**: `Mockito`, `MockMvc`, isolated application contexts
- **Executable** via `mvn test` in the respective microservice

---

## Stored Procedures

The project makes use of **multiple stored procedures**, especially within `ms-auth`, to:

- Centralize repetitive business logic (e.g., user creation, token validation)
- Reduce complexity in the Java service layer
- Improve readability and maintainability
- Ensure **reproducibility** via an `init.sql` script auto-loaded at container startup

---

## Security

- Authentication handled via **JWT**, signed with a static secret key
- **Custom security filters** to protect endpoints
- Steam identity validation via the `GetPlayerBans` API
- Discord linkage verified through real-time voice channel detection

---

## Containerization

- Each microservice is **individually containerized**
- The full project is orchestrated using **Docker Compose**
- Uses both **MySQL** and **MongoDB**, depending on the service
- Includes an `init.sql` file for automatic DB initialization (e.g., stored procedures in `ms-auth`)

---

## Running the Project
docker-compose up --build





---------------------------------------------------------------------------------------------------------------------------

# SneakyDateReforged (version fr)

**SneakyDateReforged** est une application web permettant de se donner rendez-vous en mode **"cheater free"** sur trois jeux de la plateforme Steam : **Rust**, **PUBG** et **Among Us**. Elle a vocation à s’ouvrir progressivement à d’autres jeux disponibles sur Steam.

---

## Objectif de l'application

L’objectif de **SneakyDateReforged** est de permettre à des joueurs et joueuses de se rencontrer virtuellement autour d’un rendez-vous planifié dans un jeu en ligne. L’application propose des fonctionnalités sociales complètes pour organiser, rejoindre et gérer ces rendez-vous tout en garantissant la sécurité et la modération grâce à :

- Un système de **validation d’identité via Steam** (vérification d’absence de bannissement)
- Une **connexion Discord** pour prouver la présence en salon vocal
- Une gestion avancée des **amis**, des **listes**, des **invitations**, et des **notifications**

---

## Architecture

L'application est conçue selon une **architecture microservices** modulaire et découplée :

| Microservice      | Rôle principal                                   |
|-------------------|--------------------------------------------------|
| `ms-auth`         | Gestion de l'inscription, connexion, Steam et Discord |
| `ms-rdv`          | Création, modification et affichage des rendez-vous |
| `ms-invitation`   | Système d'invitations et de réponses aux RDV    |
| `ms-friend`       | Gestion des amis et des listes                  |
| `ms-profil`       | Affichage et recherche des profils              |
| `ms-notif`        | Envoi et gestion des notifications              |
| `ms-configserver` | Configuration centralisée                       |
| `ms-eurekaserver` | Service discovery                               |
| `angular-frontend`| Frontend utilisateur en Angular 17              |

---

## Technologies utilisées

### Backend
- **Java 17**
- **Spring Boot 3**
- **Spring Cloud** (Config Server, Eureka, Feign)
- **Spring Security + JWT**
- **JPA / Hibernate (MySQL)** et **Spring Data MongoDB**
- **Docker & Docker Compose**
- **JUnit / Mockito** pour les tests unitaires et d'intégration
- **Procédures stockées** en base de données pour centraliser certaines logiques répétitives

### Frontend
- **Angular 17** (composants standalone)
- **Angular Material**
- **Authentification JWT avec route guards**
- **Services HTTP intégrés aux microservices backend**

---

## Tests

Le microservice `ms-auth` bénéficie d'une **couverture complète en tests unitaires et en tests d'intégration**, afin de garantir la fiabilité du cœur de l'application.

- **Tests unitaires** : services, gestion des tokens, encodeurs, logique Steam/Discord
- **Tests d'intégration** : endpoints REST, base MySQL via Docker, validation des flux JWT
- **Mocking** : `Mockito`, `MockMvc`, contextes d'application isolés
- **Tests exécutables** via `mvn test` dans le microservice concerné

---

## Procédures stockées

Le projet utilise plusieurs **procédures stockées MySQL**, notamment dans `ms-auth`, pour :

- Centraliser la logique métier répétitive (ex. : création d’utilisateur, vérification de tokens)
- Soulager la couche Java des requêtes SQL complexes
- Améliorer la lisibilité et la maintenabilité du code
- Garantir la **reproductibilité** grâce à un script `init.sql` chargé automatiquement au démarrage

---

## Sécurité

- Authentification via **JWT**, signé avec une clé secrète statique
- **Filtres de sécurité personnalisés** pour protéger les endpoints
- Validation de l’identité Steam via l’API `GetPlayerBans`
- Liaison Discord vérifiée via la détection en temps réel dans un salon vocal

---

## Conteneurisation

- Chaque microservice est **dockerisé individuellement**
- Le projet est orchestré avec **Docker Compose**
- Utilise **MySQL** et **MongoDB** selon les services
- Contient un fichier `init.sql` pour l'initialisation automatique (procédures dans `ms-auth`)

---

## Lancement du projet
docker-compose up --build
