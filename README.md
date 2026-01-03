# FinanceMaster

**WebTech WiSe25/26 – Gruppenarbeit**  
Team: Tomer Gonen, Kolja Schollmeyer

Eine Web-App zur Verwaltung von Einnahmen und Ausgaben mit Kategorisierung und Finanzübersicht.

## Links

- **Backend Repository**: https://github.com/tomergonen2002/FinanceMaster
- **Frontend Repository**: https://github.com/KoljaSchollmeyer/frontend
- **Live Backend**: https://financemaster-8cou.onrender.com
- **Live Frontend**: https://frontend-7vbb.onrender.com
- **Lokal Frontend**: http://localhost:5173/
- **Lokal Backend**: http://localhost:8080/

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

## Deployment auf Render

### Benötigte Umgebungsvariablen (Backend Service)

```
DB_NAME=financemaster_database
DB_PASSWORD=
FRONTEND_URL=https://frontend-7vbb.onrender.com
```

- **DB_NAME**: Name der PostgreSQL-Datenbank
- **DB_PASSWORD**: Passwort für PostgreSQL-User
- **FRONTEND_URL**: URL des Frontend-Services für CORS-Konfiguration (wichtig für Cross-Domain-Authentication)

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

## API Endpoints

### Authentifizierung
| Methode | Endpoint | Beschreibung | Auth erforderlich |
|---------|----------|-------------|------------------|
| POST | /auth/register | Neuen Benutzer registrieren | Nein |
| POST | /auth/login | Benutzer anmelden | Nein |
| GET | /auth/me | Aktuellen Benutzer abrufen | Ja |
| POST | /auth/logout | Benutzer abmelden | Ja |

### Kategorien
| Methode | Endpoint | Beschreibung | Auth erforderlich |
|---------|----------|-------------|------------------|
| GET | /categories | Alle Kategorien abrufen | Ja |
| POST | /categories | Kategorie erstellen | Ja |
| DELETE | /categories/{id} | Kategorie löschen | Ja |
| DELETE | /categories | Alle Kategorien löschen | Ja |

### Transaktionen
| Methode | Endpoint | Beschreibung | Auth erforderlich |
|---------|----------|-------------|------------------|
| GET | /transactions | Alle Transaktionen abrufen | Ja |
| POST | /transactions | Transaktion erstellen | Ja |
| DELETE | /transactions/{id} | Transaktion löschen | Ja |
| DELETE | /transactions | Alle Transaktionen löschen | Ja |

### Finanzübersicht
| Methode | Endpoint | Beschreibung | Auth erforderlich |
|---------|----------|-------------|------------------|
| GET | /summary/balance | Balance-Übersicht mit optional Filterung nach Kategorie, Datum | Ja |
| GET | /summary/by-category | Übersicht gruppiert nach Kategorien | Ja |
| GET | /summary/by-date | Übersicht gruppiert nach Datum | Ja |

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
