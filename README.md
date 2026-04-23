# CedarStar Android

CedarStar Android is a Jetpack Compose + Material 3 Android app skeleton. This repository currently focuses on architecture, navigation, shared state, SSE plumbing, and Mock-driven UI scaffolding. Business logic is intentionally not implemented yet.

## Top-level directory responsibilities

- `core/` — App-wide infrastructure such as network config, SSE client scaffolding, and Hilt modules.
- `data/` — DTOs, domain models, repository interfaces, and Mock / Impl repository layers.
- `ui/` — Compose screens, shared UI state, navigation, theme, and reusable widgets.
- `util/` — Small app utilities such as logging and formatting helpers.
- `app/src/main/AndroidManifest.xml` — Android application entry, permissions, and component declarations.
- `build.gradle.kts` / `settings.gradle.kts` — Gradle build and dependency management.

## Dependency list

The project is intentionally limited to the libraries below.

### Core Android / Lifecycle
- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.activity:activity-compose`

### Compose
- `androidx.compose:compose-bom`
- `androidx.compose.ui:ui`
- `androidx.compose.ui:ui-graphics`
- `androidx.compose.ui:ui-tooling-preview`
- `androidx.compose.material3:material3`
- `androidx.compose.material:material-icons-extended`
- `androidx.compose.ui:ui-tooling` (debug only)

### Navigation
- `androidx.navigation:navigation-compose`

### Networking / Serialization
- `com.squareup.retrofit2:retrofit`
- `com.squareup.retrofit2:converter-kotlinx-serialization`
- `com.squareup.okhttp3:okhttp`
- `com.squareup.okhttp3:logging-interceptor`
- `org.jetbrains.kotlinx:kotlinx-serialization-json`

### SSE
- `com.launchdarkly:okhttp-eventsource`

### Coroutines
- `org.jetbrains.kotlinx:kotlinx-coroutines-android`

### DI
- `com.google.dagger:hilt-android`
- `com.google.dagger:hilt-compiler` (KSP)
- `androidx.hilt:hilt-navigation-compose`

## How to run

1. Install the Android SDK locally.
2. Create a `local.properties` file at the project root and point it to your SDK installation, for example:

```properties
sdk.dir=C:\\Android\\Sdk
```

3. Open the project in Android Studio or run Gradle from the project root.
4. Build the debug app:

```bash
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

## Mock mode

The current app boots in Mock mode by default.

- `ChatRepositoryMock` returns fixed chat history.
- `StatusRepositoryMock` is intended to drive top-bar status changes.
- `ConnectionRepositoryMock` provides platform connection indicators.
- `RepositoryModule` binds Mock implementations in Hilt.

This means the app can render its shell, navigation, and placeholder states without any real backend.

## Known TODOs

- `StatusRepositoryMock` still needs a timed mock loop for richer top-bar updates.
- `ConnectionRepositoryMock` still needs timed random toggles for connection indicator animation verification.
- `SseClient` is still a structural stub and does not maintain a real SSE stream.
- `StatusRepositoryImpl` / `ChatRepositoryImpl` are placeholders for future REST + SSE integration.
- `DashboardScreen`, `ChatScreen`, `JournalScreen`, `CompanionScreen`, and `ClioScreen` are still placeholder UI.
- `MainScreen` still has some navigation/drawer/dashboard state that is scaffolded but not fully wired to final UX behavior.
- `local.properties` is intentionally not committed and must be created locally.
