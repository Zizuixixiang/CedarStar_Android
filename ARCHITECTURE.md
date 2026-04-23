# CedarStar Android Architecture

This document explains the design intent of the current project skeleton. The emphasis is on why the architecture is arranged this way, not just what exists.

## Data flow

```text
SSE / REST  ->  Repository  ->  StateFlow  ->  ViewModel  ->  Composable
     ^                                         |
     |                                         |
     +---------------- user actions -----------+
```

### Why this flow exists

The project keeps all state changes moving in one direction so that UI remains predictable.

- `Repository` is the only layer that knows about network or SSE details.
- `ViewModel` converts repository state into UI state.
- `Composable` only observes state and emits user intents upward.

This separation matters because it prevents UI components from becoming hidden sources of business logic. It also keeps network and SSE side effects out of the composition layer, which is important for stability and testability.

## Shared ViewModel scope

`MainViewModel` is intended to be activity-scoped so all four tabs read the same shared state.

### Why this is designed that way

The top bar, drawer indicators, and dashboard entry all depend on one consistent source of truth. If every tab owned its own `MainViewModel`, then each screen could drift into a different state snapshot and the top bar would no longer represent the app globally.

Using one shared ViewModel instance gives the app:

- a single source of truth for app-wide status
- consistent top-bar values across tab switches
- drawer indicators that reflect the same repository state as the current page
- a clean place to combine multiple repository flows

This also supports the architectural rule that tab switching should not recreate global state.

## Top bar non-recomposition principle

The top bar is placed in the `Scaffold.topBar` slot and reads only the shared state it needs.

### Why this reduces visual churn

If the top bar were nested inside each tab screen, switching tabs would cause it to be recreated unnecessarily. By keeping it outside the content area, the top bar remains structurally stable while only the body content changes.

The animated pieces inside the top bar use local animation primitives:

- `AnimatedNumber` for money transitions
- `AnimatedEmotionIcon` for emotion changes

This means the number and emoji animate smoothly without forcing the rest of the shell to redraw.

## Drawer local refresh behavior

The drawer is designed so that each platform indicator observes only the slice of state it needs.

### Why this is important

A naive implementation would observe the full `connections` list at the drawer level and rebuild the whole drawer whenever any platform changes. That is acceptable for tiny apps but not for this architecture, where the goal is local refresh and predictable recomposition boundaries.

By isolating each `ConnectionIndicator`, only the corresponding indicator recomposes when that platform changes. That keeps the rest of the drawer stable.

## Mock -> Impl switching

Repository binding is centralized in Hilt modules.

### Why the switch is done at the DI layer

Swapping behavior in DI allows the UI and ViewModels to remain unchanged while repository behavior changes underneath.

This gives three benefits:

- no UI code needs to know whether data is real or mocked
- the swap happens in one place instead of being scattered across screens
- future backend integration can be introduced without refactoring Composables

In the current setup, `RepositoryModule` binds Mock repositories by default. Later, the same module can be changed to bind Impl repositories without affecting the presentation layer.

## SSE architecture

`SseClient` is intentionally kept as a transport-only layer.

### Why SSE must not touch UI directly

SSE events are asynchronous and can arrive at any time. If a callback writes directly into UI objects, the app becomes vulnerable to threading issues and lifecycle leaks.

Instead, SSE should:

1. receive a raw event
2. parse it into a typed event
3. update repository state
4. expose that state through `StateFlow`
5. let ViewModels and Composables observe the flow

This keeps event handling safe, testable, and consistent with the rest of the unidirectional data flow.

## Architecture red lines

The following are forbidden by design:

- Do not instantiate repositories directly inside `Composable` functions.
- Do not use `LiveData`; use `StateFlow` / `SharedFlow` instead.
- Do not mutate UI state directly from SSE callbacks, network callbacks, or broadcast receivers.
- Do not add multiple module copies for the same responsibility.
- Do not let tab screens own separate copies of app-wide state.
- Do not implement business logic inside Compose screens.
- Do not use XML layouts for UI screens.
- Do not use Java for this app layer.
- Do not create container-level refresh behavior that causes the entire shell to redraw on one small state change.

## Why the current structure is intentionally minimal

This repository is a skeleton, not a finished product. The goal is to establish:

- one Activity
- one shared app shell
- one global state hub
- one repository contract per domain
- one clear place for future REST and SSE integration

A minimal skeleton is easier to audit and much harder to accidentally violate than a prematurely elaborate implementation.
