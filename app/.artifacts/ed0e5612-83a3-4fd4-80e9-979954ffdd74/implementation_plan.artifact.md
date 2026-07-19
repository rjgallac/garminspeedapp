# Implement CSC Service Discovery and Data Reception

Implement the full Bluetooth GATT handshake for the Cycling Speed and Cadence (CSC) service to enable the app to receive real-time speed data from a connected sensor.

## User Review Required

> [!IMPORTANT]
> This change introduces logic that depends on specific Bluetooth hardware capabilities (the ability to send notifications). If testing on an emulator, you will need to use a simulated GATT server.

## Open Questions

- None at this stage.

## Proposed Changes

### `BluetoothManager.kt`

I will implement the full workflow within the existing `gattCallback`:

#### [MODIFY] `BluetoothManager.kt`
- **Add Constants**: Define `CSC_SERVICE_UUID` (`0x1816`) and `CSC_MEASUREMENT_CHAR_UUID` (`0x2A5B`).
- **New State**: Add a `MutableStateFlow<Float>` called `_speed` to track the current speed.
- **Service Discovery**: In `onServicesDiscovered`, logic will be added to:
    - Locate the CSC Service.
    - Locate the Measurement Characteristic.
    - Enable local notifications via `setCharacteristicNotification`.
    - Enable remote notifications by writing to the Client Characteristic Configuration Descriptor (CCCD) (`0x2902`).
- **Data Parsing**: Implement `onCharacteristicChanged` to:
    - Read the byte array from the characteristic.
    int8/uint16 parsing (the first byte is flags, followed by speed/cadence data).
    - Update the `_speed` StateFlow with the parsed value.
- **Error Handling**: Ensure failures in discovery or descriptor writing update the `ConnectionState.Error`.

## Verification Plan

### Automated Tests
- (Unit tests for parsing logic if extracted to a helper class).

### Manual Verification
- **Logcat Monitoring**: Monitor logs to ensure "Services discovered", "Notifications enabled", and "Received speed: X" messages appear.
- **UI Validation**: Verify that the UI (which should be updated to observe `speed`) reflects changing values when a sensor sends data.
