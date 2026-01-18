# FinanceMaster

**WebTech WiSe25/26 – Gruppenarbeit**

Team: Tomer Gonen, Kolja Schollmeyer

Eine Web-App zur Verwaltung von Einnahmen und Ausgaben mit Kategorisierung, Fremdwährungs-Support und Finanzübersicht.

## 🔗 Links

- **Backend Repository**: https://github.com/tomergonen2002/FinanceMaster
- **Frontend Repository**: https://github.com/KoljaSchollmeyer/frontend
- **Live Backend**: https://financemaster-8cou.onrender.com
- **Live Frontend**: https://frontend-7vbb.onrender.com
- **Lokal Frontend**: http://localhost:5173/
- **Lokal Backend**: http://localhost:8080/


## 🚀 Installation und Lokale Entwicklung

### Backend starten
Voraussetzung: Java 25 JDK installiert.

```bash
cd FinanceMaster
./gradlew bootRun
```

### Frontend starten
Voraussetzung: Node.js 20+ installiert.

```bash
cd frontend
npm install
VITE_API_URL=http://localhost:8080 npm run dev
```

## 📸 Anwendungsfälle (Use Cases)

Die folgende Dokumentation demonstriert die zentralen Funktionen der Anwendung.

### 1. Authentifizierung & Session-Management
Nutzer können ein Konto erstellen und sich anmelden. Nach dem Login gelangen sie auf das Dashboard, wo oben rechts die **Abmelden-Taste** zur Verfügung steht (sicherer Session-Logout).

<table>
  <tr>
    <td width="50%">
      <h4 align="center">Registrieren</h4>
      <img src="docs/Neues Konto.png" alt="Registrieren UI" width="100%">
    </td>
    <td width="50%">
      <h4 align="center">Anmelden</h4>
      <img src="docs/Anmelden.png" alt="Login UI" width="100%">
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h4 align="center">Dashboard (mit Abmelden)</h4>
      <img src="docs/DashboardView.png" alt="Dashboard Header" width="100%">
    </td>
    <td width="50%">
      <h4 align="center">Logout Request (200 OK)</h4>
      <img src="docs/auth_logout.png" alt="Logout Network" width="100%">
    </td>
  </tr>
</table>

### 2. Finanzübersicht
Das System berechnet automatisch die Summen aller Einnahmen und Ausgaben sowie die resultierende Bilanz basierend auf den aktuellen Daten.

<div align="center">
  <img src="docs/Beispieldaten_1.png" alt="Finanzübersicht Bilanz" width="800">
</div>

### 3. Kategorien verwalten
Nutzer können eigene Kategorien anlegen. Das System schützt die Datenintegrität: Kategorien, die bereits verwendet werden, können nicht gelöscht werden.

<table>
  <tr>
    <td width="50%">
      <h4 align="center">Kategorie-Liste</h4>
      <img src="docs/Beispieldaten_2.png" alt="Kategorien Liste" width="100%">
    </td>
    <td width="50%">
      <h4 align="center">Lösch-Schutz (Fehler)</h4>
      <img src="docs/Kategorie_Loeschen_Fehler.png" alt="Fehler beim Löschen" width="100%">
    </td>
  </tr>
</table>

### 4. Transaktionen verwalten
Robuste Erfassung von Buchungen. Das System validiert Eingaben visuell (fehlender Betrag oder Kategorie) und bestätigt erfolgreiche Einträge.

<table>
  <tr>
    <td width="50%">
      <h4 align="center">Fehler: Betrag fehlt</h4>
      <img src="docs/Ohne_Betrag.png" alt="Validierung Betrag" width="100%">
    </td>
    <td width="50%">
      <h4 align="center">Fehler: Kategorie fehlt</h4>
      <img src="docs/Ohne_Kategorie.png" alt="Validierung Kategorie" width="100%">
    </td>
  </tr>
</table>
<div align="center">
  <h4>Erfolgreich gespeicherte Transaktion</h4>
  <img src="docs/Neue_Transaktion.png" alt="Transaktion gespeichert" width="800">
</div>

### 5. Fremdwährungen
Die App unterstützt diverse Währungen (USD, GBP, JPY, etc.) via externer API. Der Wechselkurs wird live geladen und der Euro-Wert automatisch berechnet.

<table>
  <tr>
    <td width="50%">
      <h4 align="center">Eingabe Fremdwährung</h4>
      <img src="docs/Neue_Transaktion_USD.png" alt="USD Eingabe" width="100%">
    </td>
    <td width="50%">
      <h4 align="center">Auto-Wechselkurs</h4>
      <img src="docs/Neue_Transaktion_Wechselkus.png" alt="Wechselkurs Detail" width="100%">
    </td>
  </tr>
</table>

### 6. Beispieldaten Generierung
Über den Button "Beispieldaten" können neue Nutzer das System sofort mit Testdaten füllen, um Funktionen wie Filterung und Bilanzierung zu testen.

<table>
  <tr>
    <td width="50%">
      <h4 align="center">Generierte Transaktionen</h4>
      <img src="docs/Beispieldaten_1.png" alt="Generierte Daten Liste" width="100%">
    </td>
    <td width="50%">
      <h4 align="center">Generierte Kategorien</h4>
      <img src="docs/Beispieldaten_2.png" alt="Generierte Daten Kategorien" width="100%">
    </td>
  </tr>
</table>

### 7. Filterung
Die Transaktionsliste lässt sich dynamisch nach Zeiträumen und Kategorien filtern.

<table>
  <tr>
    <td width="33%">
      <h4 align="center">Filter: Kategorie</h4>
      <img src="docs/Filter_nach_Kategorie.png" alt="Filter Kategorie" width="100%">
    </td>
    <td width="33%">
      <h4 align="center">Filter: Zeitraum</h4>
      <img src="docs/Filter_nach_Zeitraum.png" alt="Filter Zeit" width="100%">
    </td>
    <td width="33%">
      <h4 align="center">Kombination</h4>
      <img src="docs/Filter_nach_Kategorie_und_Zeitraum.png" alt="Kombi Filter" width="100%">
    </td>
  </tr>
</table>

### Extra: Dark Mode
<div align="center">
  <img src="docs/Darkmode.png" alt="Dark Mode" width="800">
</div>

---

## 🧪 Test-Dokumentation & Qualitätssicherung

Das Projekt verfügt über eine umfassende Testabdeckung mit insgesamt **30 automatisierten Tests**.

### 1. Backend Tests (Spring Boot Integration)
Die Tests validieren die API-Endpunkte, die Datenbank-Integrität und die Sicherheitsmechanismen (Isolation).

**Ausführen:** `./gradlew test`

### 2. Frontend Tests (Vitest & Vue Test Utils)
Die Tests prüfen die UI-Logik, den State-Store (Pinia) und die API-Fehlerbehandlung.

**Ausführen:** `npm test`


## 💾 Datenbankmodell

Das relationale Modell besteht aus drei Tabellen und ihren Beziehungen:
- `users`: Benutzerkonto
- `categories`: Kategorien (gehören einem User)
- `transactions`: Buchungen (gehören User + Kategorie)

<div align="center">
  <img src="docs/db-diagram.png" alt="Relationales DB-Modell" width="720" />
</div>

## 🛡️ Sicherheit und Architektur

### Sicherheitsmaßnahmen
- **Passwörter:** Werden sicher mit BCrypt gehasht.
- **Authentifizierung:** Session-basiert mit HttpOnly-Cookies (kein Zugriff via JavaScript möglich).
- **CORS:** Konfiguriert, um nur Anfragen vom Frontend zuzulassen.
- **User-Isolation:** Strenge Trennung der Daten. Jeder Controller prüft bei jedem Request, ob die angefragte Ressource tatsächlich dem eingeloggten User gehört.

## Nutzung mit Safari (macOS / iOS)
Safari blockiert den Login standardmäßig, da der Session-Cookie aufgrund der getrennten Domains als "Third-Party-Cookie" eingestuft wird (Intelligent Tracking Prevention).

Um die Anwendung zu testen, ist folgender Workaround nötig:
1. Öffnen Sie **Einstellungen** → **Datenschutz**.
2. Deaktivieren Sie temporär die Option **"Websiteübergreifendes Tracking verhindern"**.
3. Laden Sie die Seite neu.

### Deployment (Render)
Benötigte Umgebungsvariablen:
- `DB_NAME`, `DB_PASSWORD`, `DB_USER`
- `FRONTEND_URL` (für CORS)

### KI-Einsatz

Wir haben im Rahmen des Projekts KI-Tools wie GitHub Copilot und Gemini genutzt.
