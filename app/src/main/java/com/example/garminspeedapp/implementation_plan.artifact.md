# Implement Runtime Permissions and Bluetooth Check

This plan addresses the issue where the "Start Scanning" button was disabled and implements the necessary runtime permissions for Bluetooth scanning on modern Android devices.

## Proposed Changes

### [MODIFY] MainActivity.kt
Update `MainActivity` to handle permission requests and check if Bluetooth is enabled before starting a scan.

- Implement `ActivityResultLauncher` for requesting permissions (`BLUETOOHT_SCAN`, `BLUETOOTH_CONNECT`, `ACCESS_FINE_LOCATION`).
- Update the `BluetoothScreen` to trigger permission checks when "Start Scanning" is clicked.
- Add logic to check if Bluetooth is enabled and provide feedback/action to enable it.

## Verification Plan

### Manual Verification
1. Launch the app.
2. Verify the "Start Scanning" button is enabled.
3. Click "Start Scanning".
4. Verify that the system permission dialog appears.
5. Grant permissions and verify scanning starts.
6. Deny permissions and verify error feedback or no scan start.
7. Test with Bluetooth turned off to ensure proper handling.
