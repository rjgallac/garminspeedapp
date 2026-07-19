# Implementation Plan: Establishing Stable BLE Connection to Garmin Sensor

## Goal
Transition from merely "connecting" at the Bluetooth level to a verified connection where the app has successfully discovered the Cycling Speed and Cadence (CSC) service on the sensor.

## Proposed Changes

### BluetoothManager.kt
#### [MODIFY] `BluetoothManager.kt`
- Update `onConnectionStateChange` to trigger `gatt.discoverServices()` automatically upon successful connection.
- Implement `onServicesDiscovered` logic to specifically check for the presence of the CSC Service UUID (`1816`).
- Add a new `ConnectionState` subclass: `ServiceDiscovery(val isFound: Boolean)` to communicate discovery progress/results to the UI.

### MainActivity.kt
#### [MODIFY] `MainActivity.kt`
- Update the `BluetoothScreen` UI to reflect the new connection states (e.g., "Connecting...", "Discovering Services...", "Ready