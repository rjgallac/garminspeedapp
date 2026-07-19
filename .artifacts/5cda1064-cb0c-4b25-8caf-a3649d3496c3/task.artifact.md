# BLE Integration Task List

- `[ ]` Permissions Setup
    - `[ ]` Add Bluetooth and Location permissions to `AndroidManifest.xml`
- `[ ]` Bluetooth Scanner Implementation
    - `[ ]` Create `BluetoothScanner.kt` with `ScanCallback`
    - `[ ]` Expose discovered devices via `StateFlow`
- `[ ]` Connection Logic
    - `[ ]` Create `BluetoothManager.kt` to handle `BluetoothGatt`
    - `[ ]` Implement connection/disconnection logic
- `[ ]` UI Update
    - `[ ]` Integrate scanner with ViewModel/UI
    - `[ ]` Display list of discovered devices in `MainActivity.kt`
    - `[ ]` Add Scan button and connection interaction
