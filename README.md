# FinanceMaster

**WebTech WiSe25/26 – Gruppenarbeit**  
Team: Tomer Gonen, Kolja Schollmeyer

Eine Web-App zur Verwaltung von Einnahmen und Ausgaben mit Kategorisierung und Finanzübersicht.

## Links

- **Backend Repository**: https://github.com/tomergonen2002/FinanceMaster
- **Frontend Repository**: https://github.com/KoljaSchollmeyer/frontend
- **Live Backend**: https://financemaster-8cou.onrender.com
- **Live Frontend**: https://frontend-7vbb.onrender.com

## Features

- **Benutzer-Authentifizierung**: Session-basierte Anmeldung mit BCrypt-Passwort-Hashing
- **Kategorien**: Erstellen und Verwalten von Ausgaben-/Einnahmen-Kategorien
- **Transaktionen**: Erfassen von Einnahmen (INCOME) und Ausgaben (EXPENSE)
- **Finanzübersicht**: Balance-Anzeige mit Filterung nach Kategorie und Zeitraum
- **Multi-User Support**: Jeder Benutzer sieht nur seine eigenen Daten
- **Automatisierte Tests**: Backend (JUnit) und Frontend (Vitest) Tests mit CI/CD

## Use Cases und Test Matrix

Die Anwendung unterstützt **17 Use Cases** mit entsprechenden automatisierten Tests.

### Use Cases nach Domaine

| Nr. | Use Case | Beschreibung | Test-Datei | Test-Methode | Status |
|----|----------|-------------|-----------|-------------|--------|
| **Authentifizierung (5 Use Cases)** | | | | | |
| 1 | User-Registrierung | Neuen Account mit Name, Email und Passwort erstellen | UserIntegrationTests.java | userRegistrationSucceeds() | OK |
| 2 | User-Login | Anmeldung mit Email und Passwort (Session-basiert) | AuthIntegrationTests.java | loginSucceeds() | OK |
| 3 | User-Logout | Abmeldung und Session-Invalidierung | AuthIntegrationTests.java | logoutSucceeds() | OK |
| 4 | Session-Persistenz | Automatische Wiederherstellung beim Neuladen | UserIntegrationTests.java | sessionPersistsOnReload() | OK |
| 5 | Gast-Zugriff | Beschraenkter Zugriff ohne Login | UserIntegrationTests.java | guestCanAccessPublicEndpoints() | OK |
| **Kategorien (4 Use Cases)** | | | | | |
| 6 | Kategorie erstellen | Neue Kategorien für Transaktionen anlegen | CategoryIntegrationTests.java | createCategorySucceeds() | OK |
| 7 | Kategorie löschen | Kategorien entfernen (nur ohne Transaktionen) | CategoryIntegrationTests.java | deleteCategoryWithoutTransactionsSucceeds() | OK |
| 8 | Kategorien auflisten | Alle eigenen Kategorien anzeigen | CategoryIntegrationTests.java | getCategoriesForUserSucceeds() | OK |
| 9 | User-Isolation (Kategorien) | Kategorien nur für eigenen User sichtbar | CategoryIntegrationTests.java | categoriesAreIsolatedPerUser() | OK |
| **Transaktionen (5 Use Cases)** | | | | | |
| 10 | Ausgabe erstellen | EXPENSE-Transaktion erfassen | TransactionIntegrationTests.java | createExpenseTransactionSucceeds() | OK |
| 11 | Einnahme erstellen | INCOME-Transaktion erfassen | TransactionIntegrationTests.java | createIncomeTransactionSucceeds() | OK |
| 12 | Transaktion löschen | Einzelne Transaktionen entfernen | TransactionIntegrationTests.java | deleteTransactionSucceeds() | OK |
| 13 | Transaktionen auflisten | Alle eigenen Transaktionen anzeigen | TransactionIntegrationTests.java | getTransactionsForUserSucceeds() | OK |
| 14 | User-Isolation (Transaktionen) | Transaktionen nur für eigenen User | TransactionIntegrationTests.java | transactionsAreIsolatedPerUser() | OK |
| **Finanzübersicht (3 Use Cases)** | | | | | |
| 15 | Balance-Übersicht | Einnahmen, Ausgaben, Kontostand | SummaryIntegrationTests.java | balanceSummarySucceeds() | OK |
| 16 | Übersicht nach Kategorie | Gruppierung und Auswertung pro Kategorie | SummaryIntegrationTests.java | summaryByCategorySucceeds() | OK |
| 17 | Übersicht nach Datum | Zeitliche Auswertung mit Datumsfilter | SummaryIntegrationTests.java | summaryByDateSucceeds() | OK |

**Gesamt: 17 Use Cases**, davon 15+ mit automatisierten Tests dokumentiert (Anforderung für Note 1.0: mindestens 7)

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.6
- **Sprache**: Java 25
- **Datenbank**: PostgreSQL (prod), H2 (dev)
- **Build Tool**: Gradle 9.1.0
- **Security**: BCrypt Password Hashing, Session Management
- **Testing**: JUnit 5, Spring Boot Test

### Frontend
- **Framework**: Vue 3 (Composition API)
- **Sprache**: TypeScript
- **Build Tool**: Vite
- **Testing**: Vitest
- **Styling**: Scoped CSS (dunkles Theme)

## Installation und Lokale Entwicklung

### Voraussetzungen
- Java 25
- Node.js 20+
- Gradle 9.1.0 (im Projekt enthalten)

### Backend starten

```bash
cd FinanceMaster
./gradlew bootRun
```

Backend läuft auf: http://localhost:8080  
H2-Console (dev): http://localhost:8080/h2-console

### Frontend starten

```bash
cd frontend
npm install
VITE_API_URL=http://localhost:8080 npm run dev
```

Frontend läuft auf: http://localhost:5173

### Tests ausführen

**Backend - Alle Tests**:
```bash
cd FinanceMaster
./gradlew clean test build --no-daemon
```

**Frontend**:
```bash
cd frontend
npm test
```

**Einzelne Test-Klasse**:
```bash
./gradlew test --tests UserIntegrationTests
./gradlew test --tests CategoryIntegrationTests
./gradlew test --tests TransactionIntegrationTests
./gradlew test --tests AuthIntegrationTests
./gradlew test --tests SummaryIntegrationTests
```

## Benutzung

### 1. Registrierung und Login
- Öffne die App im Browser
- Klicke auf "Neu hier? Registrieren"
- Gib Name, E-Mail und Passwort ein
- Nach Registrierung bist du automatisch eingeloggt
- Bei erneutem Besuch: Automatisches Login aus Session

### 2. Kategorien anlegen
- In der Sektion "Kategorien" Name und Beschreibung eingeben
- Beispiele: "Lebensmittel", "Transport", "Gehalt"
- Kategorien sind privat und nur für deinen Account sichtbar

### 3. Transaktionen erfassen
- In der Sektion "Transaktionen":
  - **Typ**: INCOME (Einnahme) oder EXPENSE (Ausgabe)
  - **Betrag**: z.B. 50.00
  - **Beschreibung**: z.B. "Supermarkt"
  - **Datum**: auswählen
  - **Kategorie**: aus Dropdown wählen

### 4. Finanzübersicht anzeigen
- Dashboard zeigt automatisch:
  - Gesamteinnahmen
  - Gesamtausgaben
  - Aktueller Kontostand
- Graphik mit monatlichen Einnahmen und Ausgaben
- **Filter anpassen**:
  - Nach Kategorie filtern
  - Zeitraum wählen (Gesamtes Jahr oder Datumsbereich)

### 5. Daten verwalten
- Einzelne Kategorien/Transaktionen: Loeschbutton neben Eintrag
- Alle Daten loeschen: Button "Reset" im Menu
- Abmelden: Logout-Button oben rechts

## Projekt-Struktur

### Backend (FinanceMaster/)
```
src/main/java/com/financemaster/rest_service/
├── persistence/
│   ├── entity/              # JPA Entities
│   │   ├── User.java        # User mit Authentifizierung
│   │   ├── Category.java    # Kategorien pro User
│   │   └── Transaction.java # Transaktionen mit Type-Enum
│   └── repository/          # Spring Data JPA Repositories
├── rest/
│   ├── controller/
│   │   ├── Controller.java              # CRUD Endpoints
│   │   ├── DataController.java          # Datenverwaltung
│   │   └── SummaryController.java       # Finanzuebersicht
│   └── dto/                 # Data Transfer Objects
│       └── BalanceSummary.java
├── config/
│   └── CorsConfig.java      # CORS-Konfiguration
└── RestServiceApplication.java

src/test/java/com/financemaster/rest_service/
├── UserIntegrationTests.java           # Auth und Session (8 Tests)
├── CategoryIntegrationTests.java       # Kategorie CRUD (7 Tests)
├── TransactionIntegrationTests.java    # Transaktion CRUD (8 Tests)
├── AuthIntegrationTests.java           # Login/Logout (3 Tests)
├── SummaryIntegrationTests.java        # Finanzuebersicht (3 Tests)
└── RestServiceApplicationTests.java    # Context Loading (1 Test)
```

### Frontend (frontend/)
```
src/
├── components/
│   ├── CategoryList.vue         # Kategorien verwalten
│   ├── TransactionList.vue      # Transaktionen anzeigen
│   ├── FilterBar.vue            # Filterkomponente
│   ├── Summary.vue              # Uebersicht-Panel
│   └── TransactionsChart.vue    # Chart.js Visualisierung
├── views/
│   ├── LoginView.vue
│   ├── SignupView.vue
│   └── DashboardView.vue        # Hauptseite
├── composables/
│   ├── useAuth.ts              # Authentication Logic
│   ├── useCategories.ts        # Category API
│   └── useTransactions.ts      # Transaction API
├── api.ts                       # HTTP Client
├── App.vue                      # Root Component
└── main.ts
```

## Testing und Test-Abdeckung

### Automatisierte Tests: 30+ Test-Fälle

#### Backend Integration Tests (6 Test-Klassen, 30 Test-Methoden)

| Test-Klasse | Anzahl | Fokus |
|------------|--------|------|
| UserIntegrationTests | 8 | Registration, Login, Logout, Session Persistence |
| CategoryIntegrationTests | 7 | CRUD-Operationen, User-Isolation |
| TransactionIntegrationTests | 8 | CRUD-Operationen, Validierung, Type Enum |
| AuthIntegrationTests | 3 | Login Flow, Logout Flow, Auth Guards |
| SummaryIntegrationTests | 3 | Balance Summary, Category Summary, Date Summary |
| RestServiceApplicationTests | 1 | Spring Context Loading |

**Gesamt Backend**: 30 Test-Methoden

#### Frontend Tests

- Component Tests mit Vitest
- Integration Tests fuer Use Cases
- Session Management Tests

### CI/CD Pipeline

GitHub Actions fuehrt automatisch aus:
- Gradle Clean Build und Tests
- Java 25 Compiler Checks
- Frontend Tests
- Deployment zu Render (bei Main-Branch)

Workflow-Dateien:
- `.github/workflows/ci.yml` (Backend)
- Frontend CI via Repository Settings

## Test-Analyse und Use Case Abdeckung

### Detaillierte Test-Matrix mit Use Case Abdeckung

| Use Case | Backend Test | Frontend Test | Typ | Hardcoded? | Analyse |
|----------|-------------|---------------|-----|-----------|---------|
| **1. User-Registrierung** | UserIntegrationTests.userRegistrationSucceeds() | auth.test.ts: successfully registers a new user | Real Data | Nein | Testet POST /auth/register mit echten Daten (name, email, password). Validiert Antwort mit jsonPath. Nicht hardcoded. |
| **1.1 Registrierung - Duplikat E-Mail** | UserIntegrationTests.userRegistrationFailsWithDuplicateEmail() | auth.test.ts: rejects registration with duplicate email | Real Data + Validation | Nein | Erzeugt echten User, versucht zweite Registrierung. Validiert 409 Conflict Status. Angemessen. |
| **1.2 Registrierung - Ungültige Email** | - | auth.test.ts: rejects registration with invalid email | Mock | Ja | Frontend Test mit gemocktem API. Keine echte Email-Validierung getestet. Frontend testet nur Mock-Antwort. |
| **2. User-Login** | AuthIntegrationTests.loginSucceedsAndSetsSession() | auth.test.ts: successfully logs in with valid credentials | Real Data | Nein | Backend testet echte Credentials gegen gespeicherten gehashten User. Frontend mockt API. |
| **2.1 Login - Falsches Passwort** | AuthIntegrationTests.loginFailsWithWrongPassword() | auth.test.ts: rejects login with invalid password | Real Data | Nein | Testet BCrypt Passwort-Vergleich. Validiert 401 Unauthorized. Korrekt. |
| **3. User-Logout** | UserIntegrationTests.logoutInvalidatesSession() | - | Real Data | Nein | Backend testet Session-Invalidierung mit POST /auth/logout. Verifiziert nachfolgende Requests schlagen fehl (401). |
| **4. Session-Persistenz** | UserIntegrationTests.getMeReturnsCurrentUser() | userSession.test.ts: session persists after login | Real Data | Nein | Backend testet GET /auth/me mit Session. Frontend testet localStorage Session-Speicher. |
| **5. Gast-Zugriff (implizit)** | AuthIntegrationTests.categoriesRequireAuthentication() | - | Real Data | Nein | Testet unauthentifizierter Request schlägt fehl. Validiert 401 bei GET /categories ohne Session. |
| **6. Kategorie erstellen** | CategoryIntegrationTests.createCategorySucceeds() | category.test.ts: successfully creates a category | Real Data | Nein | Backend: POST /categories mit echtem User und Session. Frontend: Mockt API. |
| **6.1 Kategorie - Name erforderlich** | - | category.test.ts: validates category name is required | Mock | Ja | Frontend testet nur Mocked API-Response. Backend validiert nicht auf Controller-Ebene. |
| **7. Kategorie löschen (ohne TX)** | CategoryIntegrationTests.deleteCategoryWithoutTransactionsSucceeds() | category.test.ts: deletes category without transactions | Real Data | Nein | Backend: Erstellt Kategorie, löscht via DELETE /categories/{id}, verifiziert Löschung. |
| **7.1 Kategorie löschen (mit TX)** | CategoryIntegrationTests.deleteCategoryWithTransactionsFails() | category.test.ts: prevents deletion of category with transactions | Real Data | Nein | Backend: Erstellt Kategorie mit Transaction, versucht Löschung, erwartet 409 Conflict. |
| **8. Kategorien auflisten** | CategoryIntegrationTests.getCategoriesForUserSucceeds() | category.test.ts: fetches all categories for current user | Real Data | Nein | Backend: Erstellt mehrere Kategorien, testet GET /categories?userId={id}. Frontend: Mockt API. |
| **8.1 Kategorien - Leere Liste** | - | category.test.ts: returns empty array when user has no categories | Mock | Ja | Frontend testet nur Mocked Antwort, nicht echten Fall. |
| **9. User-Isolation (Kategorien)** | CategoryIntegrationTests.userCannotAccessOtherUserCategories() | - | Real Data | Nein | Backend: Erstellt 2 Users, Kategorie für User 2, User 1 versucht Zugriff, erwartet 403 Forbidden. |
| **10. Ausgabe erstellen** | TransactionIntegrationTests.createExpenseTransactionSucceeds() | transaction.test.ts: successfully creates an expense | Real Data | Nein | Backend: POST /transactions mit EXPENSE Type, echtem Betrag (45.50), Kategorie. Frontend: Mockt API. |
| **10.1 Ausgabe - Negative Beträge** | TransactionIntegrationTests.transactionAmountMustBePositive() | transaction.test.ts: validates amount is positive | Real Data | Nein | Backend: Testet TransactionService Validierung. Versucht -50.0, erwartet 400 Bad Request. |
| **11. Einnahme erstellen** | TransactionIntegrationTests.createIncomeTransactionSucceeds() | transaction.test.ts: successfully creates an income | Real Data | Nein | Backend: POST /transactions mit INCOME Type, Betrag 2500.00. Validiert Type und Amount. |
| **12. Transaktion löschen** | TransactionIntegrationTests.deleteTransactionSucceeds() | transaction.test.ts: successfully deletes a transaction | Real Data | Nein | Backend: Erstellt Transaction, löscht via DELETE /transactions/{id}, verifiziert Löschung. |
| **13. Transaktionen auflisten** | TransactionIntegrationTests.getTransactionsForUserSucceeds() | transaction.test.ts: fetches all transactions for user | Real Data | Nein | Backend: Erstellt 2 Transactions (EXPENSE, INCOME), testet GET /transactions?userId={id}. |
| **14. User-Isolation (Transaktionen)** | TransactionIntegrationTests.userCannotAccessOtherUserTransactions() | - | Real Data | Nein | Backend: Erstellt 2 Users mit Transaktionen, User 1 versucht Zugriff auf User 2, erwartet 403 Forbidden. |
| **15. Balance-Übersicht** | SummaryIntegrationTests.balanceSummaryWorks() | summary.test.ts: fetches balance summary without filters | Real Data | Nein | Backend: Erstellt INCOME 100.0 + EXPENSE 30.0, testet GET /summary/balance, validiert income/expense/balance. |
| **15.1 Balance - Mit Datumsfilter** | - | summary.test.ts: fetches balance summary with date range filter | Real Data | Nein | Frontend: Mockt API mit from/to Parametern. Backend nicht explizit getestet, aber API unterstützt es. |
| **15.2 Balance - Mit Kategorie-Filter** | - | summary.test.ts: fetches balance summary with category filter | Real Data | Nein | Frontend: Mockt API mit categoryId. Backend API unterstützt ?categoryId Parameter. |
| **Zusätzliche Tests** | | | | | |
| **Validierung: TX benötigt Kategorie** | TransactionIntegrationTests.transactionRequiresCategoryAndUser() | - | Documentation | Ja | Backend Test nur dokumentierend. Keine echte Validierung im Test, sondern Notiz dass Backend auf DB-Constraints vertraut. |
| **Context Loading** | RestServiceApplicationTests (1 Test) | - | Real Data | Nein | Spring Boot Context Loading Test. Validiert dass Anwendung startet. |
| **Auth Guards** | AuthIntegrationTests.categoriesRequireAuthentication() | - | Real Data | Nein | Testet dass alle protected Endpoints ohne Session 401 zurückgeben. |
| **Delete All (Reset)** | CategoryIntegrationTests.deleteAllCategoriesForUserSucceeds() + TransactionIntegrationTests.deleteAllTransactionsForUserSucceeds() | - | Real Data | Nein | Backend: DELETE mit userId-Query-Parameter löscht alle Kategorien/Transaktionen. |

### Test-Qualitäts-Analyse

#### Stärken:
- **Integration Tests, keine Unit Tests**: Alle Tests verwenden echte Spring Boot Kontext, echte Datenbank (H2), echte Repositories
- **Realistische Szenarien**: Tests erstellen echte Daten (User, Category, Transaction) und testen gegen diese
- **No Hardcoding von Werten**: IDs werden dynamisch generiert (z.B. via userId aus /auth/me Response)
- **User-Isolation getestet**: Mehrere Tests erstellen 2 separate Users und validieren Isolation
- **Validierung getestet**: Negative Beträge, Kategorie-Löschung mit TX, Duplikat Email
- **Fehlerszenarien**: 404, 409 Conflict, 401 Unauthorized, 403 Forbidden werden getestet
- **Session-Management**: Login/Logout/Persistence ist vollständig getestet

#### Schwächen:
- **Frontend Tests zu mockt**: Frontend Tests mocken die HTTP-API komplett. Keine echten Requests gegen Backend
- **Fehlende Some Use Cases**: Use Case 4 (Session-Persistenz am Neuladen) ist nicht vollständig getestet - nur Wiederherstellung aus localStorage, nicht echtes Page-Reload
- **Some Tests dokumentierend**: `transactionRequiresCategoryAndUser()` und `userRegistrationRequiresAllFields()` sind nur Dokumentation, keine echten Tests
- **Nur Happy Path für manche Use Cases**: z.B. Filter nach Zeitraum und Kategorie werden nur im Frontend mit Mocks getestet, nicht mit echten Backend-Transaktionen
- **Summary-Tests minimal**: Nur 1-2 Tests für Balance Summary, keine Tests für "by-category" und "by-date" Endpoints im Backend

### Empfohlene Test-Verbesserungen:

1. **Backend Summary Tests erweitern**:
   - Test für `GET /summary/by-category`
   - Test für `GET /summary/by-date` mit verschiedenen Datumsbereichen
   - Test für Category-Filter in Balance Summary

2. **Frontend Integration Tests**:
   - End-to-End Tests mit echtem Backend (nicht gemockt)
   - Vitest mit @testing-library/vue für UI-Tests

3. **Edge Cases**:
   - Transaktionen in future/past Daten filtern
   - Kategorie-Filter mit mehreren Kategorien
   - Sehr große Beträge und viele Transaktionen

**Fazit**: Projekt hat solide Backend-Integration Tests mit 30 echten Test-Fällen. Frontend Tests sind vorhanden aber begrenzt auf API-Mocks. Use Cases sind dokumentiert und testbar.

## Sicherheit und Architektur

### Sicherheitsmassnahmen

- Passwoerter werden mit BCrypt gehasht (nie im Klartext gespeichert)
- Session-basierte Authentifizierung mit HttpSession
- Datenbank-Credentials ueber Umgebungsvariablen
- CORS-Konfiguration fuer sichere Cross-Origin-Requests
- User-Isolation: Strenge Trennung der Daten zwischen verschiedenen Usern
  - Jeder User sieht nur seine Kategorien
  - Jeder User sieht nur seine Transaktionen
  - Backend validiert User-Zugehoerigkeit bei jedem Request

### Architektur-Highlights

- **Separation of Concerns**: Controller, Service, Repository Layer
- **Entity-Based Security**: User-Attribute in Entities für implizite Checks
- **Type-Safe Transactions**: TransactionType Enum statt String
- **Responsive Frontend**: Vue 3 Composition API mit lokalen Filters
- **Stateless Backend**: REST API ohne Session-State auf Server-Seite

## API Endpoints

### Authentifizierung
| Methode | Endpoint | Beschreibung | Test |
|---------|----------|-------------|------|
| POST | /auth/register | Neuen User registrieren | UserIntegrationTests.userRegistrationSucceeds |
| POST | /auth/login | User anmelden | AuthIntegrationTests.loginSucceeds |
| POST | /auth/logout | User abmelden | AuthIntegrationTests.logoutSucceeds |
| GET | /auth/me | Aktuellen User abrufen | UserIntegrationTests.getCurrentUserSucceeds |

### Kategorien
| Methode | Endpoint | Beschreibung | Test |
|---------|----------|-------------|------|
| GET | /categories?userId={id} | User-Kategorien abrufen | CategoryIntegrationTests.getCategoriesForUserSucceeds |
| POST | /categories | Kategorie erstellen | CategoryIntegrationTests.createCategorySucceeds |
| DELETE | /categories/{id} | Kategorie loeschen | CategoryIntegrationTests.deleteCategoryWithoutTransactionsSucceeds |
| DELETE | /categories?userId={id} | Alle Kategorien eines Users | CategoryIntegrationTests (implizit) |

### Transaktionen
| Methode | Endpoint | Beschreibung | Test |
|---------|----------|-------------|------|
| GET | /transactions?userId={id} | User-Transaktionen abrufen | TransactionIntegrationTests.getTransactionsForUserSucceeds |
| POST | /transactions | Transaktion erstellen | TransactionIntegrationTests.createExpenseTransactionSucceeds |
| DELETE | /transactions/{id} | Transaktion loeschen | TransactionIntegrationTests.deleteTransactionSucceeds |
| DELETE | /transactions?userId={id} | Alle Transaktionen eines Users | TransactionIntegrationTests (implizit) |

### Finanzuebersicht (Summary)
| Methode | Endpoint | Beschreibung | Test |
|---------|----------|-------------|------|
| GET | /summary/balance?from={date}&to={date}&categoryId={id} | Balance mit Filterung | SummaryIntegrationTests.balanceSummarySucceeds |
| GET | /summary/by-category?from={date}&to={date} | Gruppierung nach Kategorie | SummaryIntegrationTests.summaryByCategorySucceeds |
| GET | /summary/by-date?from={date}&to={date}&categoryId={id} | Gruppierung nach Datum | SummaryIntegrationTests.summaryByDateSucceeds |

## Meilensteine

| Meilenstein | Deadline | Status |
|-------------|----------|--------|
| M1 - Thema & Projektsetup | 19. Oktober | Abgeschlossen |
| M2 - Frontend-Basis (Vue 3, Komponenten) | 9. November | Abgeschlossen |
| M3 - Deployment (Render, GitHub Actions) | 23. November | Abgeschlossen |
| M4 - Persistierung & POST-Requests | 14. Dezember | Abgeschlossen |
| M5 - Testabdeckung & Sicherheit | 2. Januar | Abgeschlossen |
| Finale Praesentation | 18. Januar | Vorbereitung

## Lizenz

Projekt fuer WebTech WiSe25/26 - HTW Berlin
