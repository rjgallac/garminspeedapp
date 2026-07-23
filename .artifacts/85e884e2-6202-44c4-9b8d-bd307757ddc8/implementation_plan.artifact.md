# Stop Scanning on Device Selection

This plan implements the requirement that the Bluetooth scan should automatically stop once a user selects a device from the list.

## Proposed Changes

### `BluetoothScanner.kt`
#### [MODIFY] `BluetoothScanner.kt`
- Add a way to signal when a scan should stop, or modify `onScanResult` to allow for an external trigger.
- *Refined approach:* I will add a `stopScanOnDiscovery` boolean parameter to the `startScan` function. If true, the scanner will automatically call `stopScan()` as soon as any device is discovered.

### `MainActivity.kt`
#### [MODIFY] `MainActivity.kt`
- Update the `Button` click listener in `BluetoothScreen` to pass `true` to `scanner.startScan(stopScanOnDiscovery = true)` so that selecting a device (which happens via the `DeviceItem`'s `onClick`) is complemented by an automatic stop when the list updates.

## Verification Plan

### Automated Tests
- No unit tests currently exist for this logic, but I will verify via manual build and run.

### Manual Verification
- Build and run the app.
- Start a Bluetooth scan.
- Tap on a device in the `LazyColumn`.
- **Expected Result:** The scan should stop immediately upon clicking the device, and the "Start Scanning..." button should revert to its initial state (or at least not stay in "Stop Scanning" mode).