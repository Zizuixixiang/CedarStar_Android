# Handoff Notes

This file lists everything intentionally left for later implementation or backend integration.

## Empty or placeholder implementations

- `core/sse/SseClient.kt` is a structural stub and does not yet maintain a real SSE connection.
- `data/repository/StatusRepositoryImpl.kt` is a placeholder for future SSE-backed status updates.
- `data/repository/ChatRepositoryImpl.kt` is a placeholder for future REST-backed chat history and send-message calls.
- `data/repository/ConnectionRepositoryMock.kt` currently holds static sample platform states.
- `data/repository/StatusRepositoryMock.kt` still needs a timed update loop if richer mock animation is desired.

## Hardcoded placeholder values

- Token provider returns a hardcoded mock token.
- API base URL is still a placeholder host.
- `DashboardOverviewDto` contains placeholder text.
- Mock chat history contains fixed sample messages.
- Placeholder screen content is used in all four tab screens and dashboard.

## Features intentionally not implemented yet

- Real REST request handling for history, send message, and dashboard overview.
- Real SSE connection management, reconnection strategy, and parsing loop.
- Full tab content UI for chat, journal, companion, and Clio pages.
- Final dashboard UI and routing behavior beyond the placeholder destination.
- Real token persistence or authentication storage.
- Any production backend integration.
- Any persistence layer for local data.

## Future backend integration TODOs

- Replace Mock repository bindings in `core/di/RepositoryModule.kt` with Impl bindings when backend is ready.
- Wire `StatusRepositoryImpl` to consume SSE events and expose them as `StateFlow<AppStatus>`.
- Wire `ChatRepositoryImpl` to `CedarStarApi` for send/history endpoints.
- Expand `ConnectionRepositoryImpl` when real platform connection data becomes available.
- Add real SSE event-source lifecycle management and retry behavior.
- Replace hardcoded token provider with real storage-backed provider.

## Validation work left for later

- Run a real Android build after Android SDK is installed locally.
- Verify top-bar animation timing on device/emulator.
- Verify drawer indicator updates with mock or live SSE events.
- Verify dashboard modal routing and back behavior.
- Verify tab switching behavior on device.
