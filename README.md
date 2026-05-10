#  E-Banking Backend

Une application backend RESTful complète pour la gestion d'un système bancaire en ligne, développée avec **Spring Boot 3** et sécurisée via **OAuth2 / JWT**.

---

##  Table des matières

- [Aperçu du projet](#aperçu-du-projet)
- [Technologies utilisées](#technologies-utilisées)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation et configuration](#installation-et-configuration)
- [Lancer l'application](#lancer-lapplication)
- [Documentation API (Swagger)](#documentation-api-swagger)
- [Sécurité](#sécurité)
- [Structure du projet](#structure-du-projet)
- [Dépendances Maven](#dépendances-maven)

---

##  Aperçu du projet

**E-Banking Backend** est une API REST qui expose les fonctionnalités essentielles d'une banque en ligne :

- Gestion des **clients** (création, consultation, mise à jour, suppression)
- Gestion des **comptes bancaires** (comptes courants, comptes épargne)
- Gestion des **opérations bancaires** (dépôt, retrait, virement)
- Consultation de l'**historique des transactions**
- Sécurisation des endpoints via **OAuth2 Resource Server (JWT)**

---

##  Technologies utilisées

| Technologie | Version | Rôle |
|---|---|---|
| Java | 21 | Langage principal |
| Spring Boot | 3.2.5 | Framework applicatif |
| Spring Data JPA | — | ORM / Accès base de données |
| Spring Security + OAuth2 | — | Authentification et autorisation |
| MySQL | 8.x | Base de données relationnelle |
| Lombok | — | Réduction du boilerplate Java |
| SpringDoc OpenAPI | 2.3.0 | Documentation API Swagger UI |
| Maven | — | Gestionnaire de dépendances et build |

---

##  Architecture

Le projet suit une architecture **en couches** classique pour les applications Spring Boot :

```
┌────────────────────────────────────────────┐
│              Client (REST / Swagger)       │
└──────────────────────┬─────────────────────┘
                       │ HTTP Requests
┌──────────────────────▼─────────────────────┐
│           Controllers (REST API)           │
│        @RestController — couche web        │
└──────────────────────┬─────────────────────┘
                       │
┌──────────────────────▼─────────────────────┐
│              Services (Métier)             │
│        @Service — logique business         │
└──────────────────────┬─────────────────────┘
                       │
┌──────────────────────▼─────────────────────┐
│           Repositories (JPA)               │
│       @Repository — accès aux données      │
└──────────────────────┬─────────────────────┘
                       │
┌──────────────────────▼─────────────────────┐
│           Base de données MySQL            │
└────────────────────────────────────────────┘
```

---

##  Prérequis

Avant de démarrer, assurez-vous d'avoir installé :

- **Java 21** (JDK)
- **Maven 3.8+**
- **MySQL 8.x** (serveur local ou distant)
- Un serveur **OAuth2 / Keycloak** (pour la gestion des tokens JWT)
- (Optionnel) **IntelliJ IDEA** ou **VS Code**

---

## ⚙️ Installation et configuration

### 1. Cloner le dépôt

```bash
git clone https://github.com/GHIZLANETHR/ebanking-backend.git
cd ebanking-backend
```

### 2. Créer la base de données MySQL

```sql
CREATE DATABASE ebanking_db;
CREATE USER 'ebanking_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ebanking_db.* TO 'ebanking_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurer `application.properties`

Modifiez le fichier `src/main/resources/application.properties` :

```properties
# ===== Base de données =====
spring.datasource.url=jdbc:mysql://localhost:3306/ebanking_db?createDatabaseIfNotExist=true
spring.datasource.username=ebanking_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===== JPA / Hibernate =====
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# ===== Sécurité OAuth2 JWT =====
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/your-realm

# ===== Serveur =====
server.port=8085
```

---

##  Lancer l'application

### Avec Maven Wrapper

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Avec Maven installé

```bash
mvn spring-boot:run
```

### Générer le JAR et l'exécuter

```bash
mvn clean package
java -jar target/ebanking-backend-0.0.1-SNAPSHOT.jar
```

L'application sera disponible sur : `http://localhost:8085`

---

##  Documentation API (Swagger)

Une fois l'application démarrée, accédez à la documentation interactive :

```
http://localhost:8085/swagger-ui/index.html
```

L'API expose notamment les endpoints suivants :

### Clients
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/customers` | Lister tous les clients |
| GET | `/api/customers/{id}` | Obtenir un client par ID |
| POST | `/api/customers` | Créer un nouveau client |
| PUT | `/api/customers/{id}` | Modifier un client |
| DELETE | `/api/customers/{id}` | Supprimer un client |

### Comptes bancaires
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/accounts` | Lister tous les comptes |
| GET | `/api/accounts/{id}` | Obtenir un compte par ID |
| GET | `/api/accounts/{id}/operations` | Historique des opérations |
| GET | `/api/accounts/{id}/pageOperations` | Historique paginé |

### Opérations
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts/debit` | Effectuer un débit |
| POST | `/api/accounts/credit` | Effectuer un crédit |
| POST | `/api/accounts/transfer` | Effectuer un virement |

---

##  Sécurité

Le projet utilise **Spring Security** avec le mode **OAuth2 Resource Server**.

- Tous les endpoints sont **protégés par défaut** et nécessitent un **token JWT valide**.
- Le token doit être fourni dans l'en-tête HTTP :

```
Authorization: Bearer <votre_token_jwt>
```

- Le serveur valide les tokens auprès du serveur d'autorisation configuré (ex : **Keycloak**).
- La validation des données entrantes est assurée par **Spring Validation** (`@Valid`, `@NotNull`, etc.).

---

##  Structure du projet

```
ebanking-backend/
├── .mvn/wrapper/               # Maven Wrapper
├── src/
│   ├── main/
│   │   ├── java/org/example/ebankingbackend/
│   │   │   ├── dtos/           # Data Transfer Objects
│   │   │   ├── entities/       # Entités JPA (Customer, BankAccount, ...)
│   │   │   ├── enums/          # Énumérations (AccountStatus, OperationType, ...)
│   │   │   ├── exceptions/     # Exceptions personnalisées
│   │   │   ├── mappers/        # Convertisseurs DTO ↔ Entités
│   │   │   ├── repositories/   # Interfaces Spring Data JPA
│   │   │   ├── security/       # Configuration sécurité OAuth2
│   │   │   ├── services/       # Logique métier
│   │   │   ├── web/            # Controllers REST
│   │   │   └── EbankingBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                   # Tests unitaires et d'intégration
├── .gitignore
├── mvnw / mvnw.cmd             # Maven Wrapper scripts
├── pom.xml                     # Dépendances et configuration Maven
└── README.md
```

---

##  Dépendances Maven

```xml
<!-- Spring Boot Web -->
<dependency>spring-boot-starter-web</dependency>

<!-- Spring Data JPA -->
<dependency>spring-boot-starter-data-jpa</dependency>

<!-- MySQL Driver -->
<dependency>mysql-connector-j</dependency>

<!-- Sécurité OAuth2 JWT -->
<dependency>spring-boot-starter-oauth2-resource-server</dependency>

<!-- Validation -->
<dependency>spring-boot-starter-validation</dependency>

<!-- Documentation Swagger / OpenAPI -->
<dependency>springdoc-openapi-starter-webmvc-ui:2.3.0</dependency>

<!-- Lombok -->
<dependency>lombok</dependency>

<!-- Tests -->
<dependency>spring-boot-starter-test</dependency>
```

---

## Auteur

**GHIZLANETHR** — [GitHub](https://github.com/GHIZLANETHR)

---

## 📄 Licence

Ce projet est développé à des fins éducatives. Libre d'utilisation pour l'apprentissage et le développement.
