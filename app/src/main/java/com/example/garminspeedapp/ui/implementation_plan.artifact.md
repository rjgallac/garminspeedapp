# Fix Speed and Distance Unit Mismatches

The application currently suffers from incorrect unit conversions between the Bluetooth sensor's native format (HPH), Kilometers per Hour (km/h), and the application's expected Miles per Hour (mph). This causes both displayed speed and accumulated distance to be mathematically incorrect.

## Proposed Changes

### 1. Standardize Sensor Data in `BluetoothManager.kt`
The primary goal is to make the `speed` StateFlow emit a reliable **MPH** value.

- [ ] **Update `parseCscMeasurement` logic**:
    - Convert raw HPH (hectometers per hour) to km/h: $\text{km/h} = \text{HPH} / 10$.
    - Convert km/h to mph: $\text{mph} = \text{km/h} \times 0.621371$.
    - This makes the `BluetoothManager` the "Single Source of Truth" for correct units.

### 2. Verify and Clean up `SessionManager.kt`
Since the `BluetoothManager` will now provide true MPH, the existing distance math is actually correct, but needs verification.

- [ ] **Verify `updateDistance` math**:
    - Confirm $\text{miles} = \text{mph} \times (\text{seconds} / 3600)$ is applied to a valid `speedMph`.
- [ ] **Update Documentation**: Ensure comments correctly reflect that the incoming stream is MPH.

## Verification Plan

### Automated Verification
- [ ] Run a unit test (if available) or a manual build to ensure no syntax errors were introduced during conversion.

### Manual Verification
- [ ] **Check Speed Accuracy**: Compare the app's displayed speed with a known accurate source (e.g., Google Maps/GPS) while riding.
- [ ] **Check Distance Accuracy**: Compare the accumulated miles in the app after a short ride against a known distance.
