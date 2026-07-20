# Transition from Bluetooth Scanner to Activity Tracker

This plan outlines the transformation of the application from a simple Bluetooth device scanner into a functional cycling session tracker with speed, time, and distance capabilities.

## User Review Required

> [!IMPORTANT]
> This is a significant architectural change. We are introducing a new state management layer that dictates which screen is currently visible to the user.

## Open Questions

*   **Persistence**: Should session data (distance/time) be saved to a local database (like Room) so users can see a history of their rides, or should it only exist for the duration of the current app session?
*   **Units**: Do you want to support both Metric (km/h) and Imperial (mph) units, or stick to one for now?

## Proposed Changes

### [NEW] State Management Layer

#### `AppScreen.kt` (New File)
- Define a sealed class `AppScreen` representing the possible views: `Scanner` and `Tracking`.

#### `SessionManager.kt` (New File)
- A new component to manage the "Active Session" logic.
- Responsibilities:
    - Managing a `Timer` (elapsed time).
    - Calculating `Distance` based on incoming speed data and elapsed time.
    - Handling `Start` and `Stop` commands.
    - Providing `StateFlows` for UI to observe.

### [MODIFY] `MainActivity.kt`

- **Navigation Logic**: Update the root Composable to observe both `BluetoothManager.connectionState` AND `SessionManager.sessionState`.
- **Screen Switching**:
    - If `ConnectionState == Connected`, show `TrackingScreen`.
    - Otherwise, show `BluetoothScreen`.
- **UI Implementation**: Create the `TrackingScreen` Composable with large, readable typography for speed, time, and distance.

### [MODIFY] `BluetoothManager.kt`

- Update to ensure that when a device is connected, it triggers any necessary session initialization if a session was previously running.

## Verification Plan

### Manual Verification
1.  **Connection Flow**: Verify that connecting to a sensor automatically switches the screen from "Scanner" to "Tracker".
2.  **Timer Functionality**: Verify that clicking "Start" begins the timer and clicking "Stop" pauses it.
3.  **Data Integration**: Verify that as the Bluetooth sensor sends speed updates, the `TrackingScreen` updates its speed display in real-time.
4.  **Distance Calculation**: Verify that distance increases proportionally with speed and time.

### Automated Tests
- Unit tests for `SessionManager` to ensure math (distance = speed * time) is accurate across various speed/time intervals.