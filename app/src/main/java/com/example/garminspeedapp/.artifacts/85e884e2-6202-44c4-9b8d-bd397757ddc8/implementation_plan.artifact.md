# Implement CSC Service Handshake and Data Parsing

This plan outlines the steps to complete the Bluetooth Low Energy (BLE) handshake for the Cycling Speed and Cadence (CSC) service and parse incoming speed data.

## Proposed Changes

### [MODIFY] `BluetoothManager.kt`

The main work will happen within `BluetoothManager.kt` to handle service discovery, characteristic identification, enabling notifications, and parsing the incoming byte array.

#### 1. Service Discovery & Characteristic Setup
- In `onServicesDiscovered`, locate the CSC Service (`0x1816`) and the CSC Measurement Characteristic (`0x2A5B`).
- Enable notifications for the CSC Measurement characteristic using `setCharacteristicNotification`.
- Enable the Client Characteristic Configuration Descriptor (CCCD) by writing to the descriptor UUID (`0x2902`) to ensure the sensor starts sending updates.

#### 2. Data Parsing Implementation
- Implement `onCharacteristicChanged` in the `gattCallback`.
- Implement logic to parse the `value: ByteArray` according to the Bluetooth SIG specification for CSC Measurement:
    - The first byte indicates flags (e.g., presence of cadence data).
    - Subsequent bytes represent speed and/or cadence values as 16-bit unsigned integers (units are 0.01 km/h or 0.01 rpm).
- Update the `_speed` StateFlow with the parsed speed value.

## Verification Plan

### Manual Verification
- **Connect to a CSC Sensor:** Connect to a real or simulated Bluetooth CSC sensor.
- **Observe Connection Flow:** Verify that services are discovered and notifications are enabled.
- **Verify Data Updates:** Observe if the `speed` StateFlow is being updated and if this is reflected in the UI (if UI components are added for speed).

### Automated Tests
- If possible, implement a unit test for the byte array parsing logic using a mocked `BluetoothGattCharacteristic`.