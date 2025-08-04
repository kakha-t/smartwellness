## Projekt Titel  
**SmartWellness**

## Beschreibung  
SmartWellness ist deine All-in-One-Android-App für gesunde Ernährung, Fitness und Stressabbau. Mit modernem Jetpack-Compose-UI, lokaler Room-Datenbank und Firebase-Sync bringt sie dir Rezepte, Trainingsangebote und personalisierte Pläne direkt auf dein Smartphone.

#### Ernährungsangebote  
In diesem Bereich verlinkt SmartWellness auf renommierte Webseiten mit hochwertigen Rezepten und Ernährungstipps. Statt mühsam selbst nach Ideen zu suchen, findest du hier fertige Menüvorschläge für jede Tageszeit – von ausgewogenen Frühstücksideen bis zu leichten Abendessen.

#### Fitness & Bewegung  
Ob klassisches Studio-Training, Yoga oder Aquafitness – SmartWellness präsentiert dir eine Auswahl an aktuellen Anbietern vor Ort und im Netz. Über eine übersichtliche Kachel-Ansicht (“Beliebte Kategorien”) gelangst du direkt zu den Webseiten der Studios und Anbieter, die dir unterschiedliche Kursangebote, Trainingspläne und Bewegungsprogramme vorstellen.

#### Personalisierte Ernährungspläne  
Für Fortgeschrittene und alle, die mehr Kontrolle wünschen, bietet die App einen interaktiven Planer: Wähle Lebensmittel aus einer umfangreichen Datenbank, und SmartWellness berechnet automatisch Kalorien, Fett, Eiweiß, Kohlenhydrate und glykämischen Index. Schritt für Schritt entsteht so dein individueller Ernährungsplan, ohne dass du selbst rechnen musst.

## Features

- **Deklarative UI mit Jetpack Compose (Material 3)**  
  – Alle Screens als `@Composable`-Funktionen  
  – Material3-Components für konsistentes Design (Buttons, Cards, Snackbars, TopAppBar…)  
- **Navigation Compose**  
  – `NavHost` & `NavController` für einfache Routen (Home, Ernährung, Fitness, Plan, Mehr)  
  – Übergabe von Argumenten über sichere Typen  
- **Lokale Persistenz mit Room**  
  – Entities: `Plan`, `User`, `Lebensmittel`, `AuswahlEintrag`  
  – DAOs für CRUD-Operationen (`PlanDao`, `UserDao`, `LebensmittelDao`)  
  – Auto-Migrations und SQL-Compile-Time-Checks  
- **Repository-Pattern**  
  – `PlanRepository` kapselt DAO-Aufrufe  
  – Einfache Austauschbarkeit durch MockK in Unit-Tests  
- **Firebase-Integration**  
  – **Firebase Auth** (E-Mail/Passwort-Login) mit Coroutines (`.await()`)  
  – **Cloud Firestore** für Synchronisation von Essens- und Trainingsplänen  
- **State Management & Side-Effects**  
  – `remember { mutableStateOf(...) }`, `mutableStateListOf(...)`  
  – `LaunchedEffect`, `SnackbarHostState` für asynchrone Effekte und Feedback  
- **CSV-Import**  
  – `LebensmittelImporter` liest `Lebensmittelliste.csv` aus `assets/`  
  – Vollständige Nährwertdaten (Kalorien, Fett, Eiweiß, Kohlenhydrate, glyk. Index)  
- **Testing**  
  – **Unit Tests** mit JUnit4 & MockK (`PlanRepositoryTest`)  
  – **Instrumented Tests** für Room-DAOs im In-Memory-DB (`PlanDaoTest`)  
  – **UI Tests** mit ComposeTestRule (`PlanScreenTest`)  

---

## Architektur & Tech Stack

| Schicht            | Technologie / Bibliothek                             | Zweck                                                     |
|--------------------|------------------------------------------------------|-----------------------------------------------------------|
| **UI**             | Jetpack Compose (Material 3)                        | Deklarative Screens, Themes, Composables                  |
| **Navigation**     | Navigation Compose                                  | Routing, Back-Stack, Deep-Links                           |
| **Daten-Layer**    | Room (androidx.room)                                | Entities, DAOs, `RoomDatabase`                            |
| **Repository**     | Eigene Klassen (`PlanRepository`, `UserRepository`) | Geschäftslogik, Daten-Abstraktion                         |
| **Cloud & Auth**   | Firebase Auth, Firebase Firestore                   | Nutzer-Login, Cloud-Sync von Plänen                       |
| **Asynchronität**  | Kotlin Coroutines                                    | `suspend`-Funktionen, `runTest`, `await()`                |
| **State Mgmt.**    | Compose State APIs                                  | `mutableStateOf`, `mutableStateListOf`, `remember`,          |
|                    |                                                      | `LaunchedEffect`, `SnackbarHostState`                     |
| **Unit Tests**     | JUnit4, MockK, kotlinx-coroutines-test               | `PlanRepositoryTest` (Logik-Tests)                        |
| **Instrumented Tests** | AndroidJUnit4, Room In-Memory, runBlocking      | `PlanDaoTest` (DAO-Tests auf Gerät/Emulator)              |
| **UI Tests**       | `createComposeRule` (Compose UI TestRule)           | `PlanScreenTest` (Compose-Screen-Tests)                   |
| **Assets**         | CSV-Datei                                            | Initialer Lebensmittel-Katalog (`Lebensmittelliste.csv`)  |
| **Build System**   | Gradle Kotlin DSL (`.kts`)                          | Dependency-Management, Plugins, Kapt                      |




## Screenshots / Demo

Hier ein kurzer Einblick in die SmartWellness-App:

| Home | Ernährung | Fitness | Plan |
|------|-----------|---------|------|
| <img src="docs/home.png" alt="HomeScreen" width="150" /> | <img src="docs/nutrition.png" alt="NutritionScreen" width="150" /> | <img src="docs/fitness.png" alt="FitnessScreen" width="150" /> | <img src="docs/plan.png" alt="PlanScreen" width="150" /> |

1. **Home:** Begrüßung, Wellen-Hintergrund und „Beliebte Kategorien“  
2. **Ernährungsangebote:** Liste externer Rezept- und Tipps-Webseiten  
3. **Fitness & Bewegung:** Anbieter-Kacheln mit Yoga, Aquatraining usw.  
4. **Plan:** Interaktiver Planer mit automatischer Kalorien- und Nährwert-Berechnung  

## Getting Started / Installation

Folge diesen Schritten, um SmartWellness lokal zu klonen, zu bauen und auszuführen.

### Voraussetzungen

- **Android Studio** (Arctic Fox oder neuer)  
- **JDK 11** (oder höher)  
- **Android SDK** (API Level 31+)  
- **Git** installiert und konfiguriert

### 1. Repository klonen

```bash
git clone https://github.com/<DEIN_USERNAME>/smartwellness.git
cd smartwellness
