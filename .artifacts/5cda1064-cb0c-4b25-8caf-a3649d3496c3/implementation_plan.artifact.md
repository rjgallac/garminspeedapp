# BLE Integration for Garmin Sensor

Implement Bluetooth Low Energy (BLE) scanning and connection capabilities to discover and connect to a Garmin sensor.

## User Review Required

> [!IMPORTANT]
> I will implement the standard Android BLE scanning and connection pattern. If your Garmin sensor requires specific Service UUIDs or characteristic manipulations, we will need to add those in a follow-up step.

## Open Questions

> [!WARNING]
> **Do you have the specific Service UUIDs or characteristics for the Garmin sensor?** Without these, I can only implement general scanning and connection.

## Proposed Changes

### Android Manifest
#### [MODIFY] `AndroidManifest.xml`
- Add Bluetooth permissions (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADVERTISE`).
- Add Location permissions (required for scanning on many Android versions: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`).
- Declare Bluetooth features.

### Core Logic (New Components)
#### [NEW] `BluetoothScanner.kt`
- A class to encapsulate the `BluetoothLeScanner` logic.
- Implementation of `ScanCallback` to handle discovered devices.
- Ability to start and stop scanning.
- A way to expose discovered devices to the UI (e.g., via a `StateFlow`).

#### [NEW] `BluetoothManager.kt`
- High-level manager to coordinate scanning and connection.
- Handles the `BluetoothGatt` lifecycle.
- Manables connection/disconnection states.

### UI Layer
#### [MODIFY] `MainActivity.kt`
- Integrate `BluetoothManager` or a ViewModel.
- Replace "Hello Android" with a UI that shows:
    - A button to start scanning.
    - A list of discovered BLE devices.
    - Connection status indicator.
    - Logic to initiate connection when a device is selected.

## Verification Plan

### Manual Verification
- Run the app on a physical Android device with Bluetooth enabled.
- Verify that the app requests necessary permissions at runtime.
- Start scanning and ensure nearby Bluetooth devices appear in the list.
- Select a device and verify that a connection attempt is initiated (even if it fails without correct UUIDs).

### Automated Tests
- Unit tests for `BluetoothScanner` logic using mocked `BluetoothLeScanner`.
- Unit tests for `BluetoothManager` state transitions.
