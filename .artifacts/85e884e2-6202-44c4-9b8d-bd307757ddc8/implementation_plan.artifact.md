# Implementation Plan: Complete Activity Tracker Integration

This plan covers the final tasks for the Activity Tracker implementation: wiring `BluetoothManager` to `SessionManager` and implementing session persistence in Room.

## Proposed Changes

### [MODIFY] `BluetoothManager.kt`
- Update `onConnectionStateChange` to trigger a callback or use a Flow that `SessionManager` can observe, to automatically start the session when connected. Wait, better yet: let `MainActivity` handle the orchestration if it already manages screen switching.
- Actually, I'll add a way for `BluetoothManager` to notify interested parties of connection changes beyond just the state flow, or simply rely on the fact that `MainActivity` is observing the state and will trigger session start.

### [MODIFY] `SessionManager.kt`
- **Inject Repository**: Pass `RideRepository` into `SessionManager`.
- **Auto-start Logic**: Ensure `startSession()` is called when a connection is established (or provide a method to be called from `MainActivity`).
- **Implement Persistence**: In `stopSession()`, create a `RideEntity` from the current `SessionState` and use the repository to save it.
- **Scope Management**: Use a `CoroutineScope` that allows for database operations on `stopSession`.

### [MODIFY] `MainActivity.kt`
- Update instantiation logic to include `AppDatabase` and `RideRepository`.
- In the `LaunchedEffect` that observes `connectionState`, call `sessionManager.startSession()` when status transitions to `Connected`.

## Verification Plan

### Manual Verification
1. **Connection & Auto-start**: Connect to a Bluetooth device and verify that the session starts (timer begins) automatically.
2. **Persistence**: Stop a session and verify through logs or a future "History" screen (if implemented) that the ride was saved.
*Note: Since there is no history screen yet, I will add logging to confirm the save operation.*

### Automated Tests
- Verify `SessionManager.stopSession()` triggers the repository's `insertRide` method.