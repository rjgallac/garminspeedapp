# Implement BLE Connection for Garmin Speed Sensor 2 (Phase 1)

This plan outlines the implementation of the initial Bluetooth Low Energy (BLE) connection steps: requesting permissions, scanning for devices, identifying the Garmin sensor, and establishing a connection.

## User Review Required

> [!IMPORTANT]
> I will be adding several new permissions to the `AndroidManifest.xml`. This is necessary for BLE scanning on modern Android versions.

## Proposed Changes

### `app/src/main/AndroidManifest.xml`
#### [MODIFY] `AndroidManifest.xml`
- Add `BLUETOOOTH_SCAN`, `BLUETOOTH_CONNECT`, and `BLUETOOTH_ADVERTISE` permissions.
- Add `ACCESS_FINE_LOCATION` permission (required for BLE scanning on many Android versions).

### New Components
#### [NEW] `com/example/garminspeedapp/BluetoothManager.kt`
A new class to encapsulate all Bluetooth LE logic, including:
- Permission checking.
- Starting and stopping the BLE scan.
- Handling discovered devices.
- Managing the `BluetoothGatt` connection.
- Identifying the Garmin sensor via its Service UUID (`1816`).

#### [NEW] `com/example/garminspeedapp/BleScannerViewModel.kt`
A ViewModel to bridge the `BluetoothManager` and the UI, holding the state of:
- Permission status.
- List of discovered devices.
- Connection status (Disconnected, Connecting, Connected).

### `app/src/main/java/com/example/garminspeedapp/MainActivity.kt`
#### [MODIFY] `MainActivity.kt`
- Update the UI to show the scanning status and a list of found devices.
- Implement the logic to request Bluetooth and Location permissions at runtime.
- Connect the UI buttons (e.g., "Scan", "Connect") to the ViewModel.

## Verification Plan

### Automated Tests
- *Note: Initial implementation focuses on hardware interaction, which is difficult to unit test without mocking the Android Bluetooth stack.*

### Manual Verification
1.  **Permission Check**: Run the app and verify that the Bluetooth/Location permission dialog appears.
2.  **Scanning**: Press a "Scan" button in the UI and verify that the scan starts (can be verified by checking logcat).
3.  **Device Discovery**: Ensure that nearby BLE devices (or specifically the Garmin sensor if present) appear in the list.
4.  **Connection**: Tap on a discovered device to trigger the connection attempt and verify the connection state changes to "Connecting" then "Connected" in the UI.
