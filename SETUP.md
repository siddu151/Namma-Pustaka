# Namma-Pustaka — Smart Library Assistant (Setup)

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer (recommended) with **JDK 17**
- Android **SDK 34** (compileSdk 34)
- A device or emulator with **API 24+** and (for QR) a **camera**

> **JAVA_HOME:** Ensure `JAVA_HOME` points to a valid **Windows JDK 17** install (not a Linux archive path). In Android Studio you can use **File → Settings → Build, Execution, Deployment → Build Tools → Gradle** and set the Gradle JDK to **Embedded JDK** or your installed JDK 17.

## 1. Open the project

1. Open Android Studio → **Open** → select the `NammaPustaka` folder (the one that contains `settings.gradle.kts` and the `app` module).
2. Wait for **Gradle Sync** to finish.

## 2. Gemini API key (Kannada summaries)

1. Create or open **`local.properties`** in the project root (same folder as `settings.gradle.kts`).
2. Add your key (no quotes):

```properties
GEMINI_API_KEY=YOUR_KEY_HERE
```

3. Sync Gradle again. The key is injected into **`BuildConfig.GEMINI_API_KEY`** at build time.

Without a key, **Generate Kannada summary** will show an error message; the rest of the app works offline.

## 3. Run the app

1. Select the **`app`** run configuration.
2. Click **Run** (green triangle).

## 4. Demo accounts (seed data)

On first launch, the Room database is empty and **`LibraryRepository.ensureSeedData()`** inserts sample users and books:

| Role   | Email                 | Password    |
|--------|----------------------|-------------|
| Admin  | `admin@nammapustaka.edu` | `admin123` |
| Student| `anil@nammapustaka.edu`  | `student123` |
| Student| `soumya@nammapustaka.edu`| `student123` |

You can also **Register** a new student or admin from the UI.

## 5. Architecture (viva notes)

- **MVVM:** Fragments + `ViewModel` + `LiveData` / coroutines  
- **Repository:** `LibraryRepository` hides Room + Gemini  
- **Room:** `database/` entities, DAOs, `AppDatabase`  
- **Navigation:** Single `MainActivity` + `nav_graph.xml` + **BottomNavigationView** (role-specific menu)  
- **QR:** **ZXing** bitmap (`QrCodeGenerator`) on add/edit book; **ML Kit** + **CameraX** in `IssueReturnFragment`  
- **Notifications:** `LibraryNotificationHelper` + `OverdueCheckWorker` (WorkManager, 15-minute periodic check)

## 6. Permissions

Declared in `AndroidManifest.xml`: **INTERNET**, **CAMERA**, **POST_NOTIFICATIONS** (Android 13+), **RECORD_AUDIO** (voice search). Runtime requests are used where required.

## 7. Database file

Room database name: **`nammapustaka_smart.db`**. Uninstall the app to reset data, or clear app storage.

## 8. Troubleshooting

- **Gradle / JAVA_HOME errors:** Fix JDK path, then **File → Invalidate Caches / Restart** if needed.  
- **Gemini 404 / model errors:** The Retrofit interface targets `gemini-1.5-flash`. If Google changes model names, update `GeminiApi.kt`.  
- **QR not detected:** Ensure good lighting; ML Kit reads from the live preview only (no iframe).

---

Built for rural school library workflows: catalog, issue/return, overdue awareness, reservations, leaderboard, and optional Kannada AI summaries.
