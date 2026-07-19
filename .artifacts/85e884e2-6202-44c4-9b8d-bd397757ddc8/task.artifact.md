# CSC Implementation Tasks

- `[ ]` Clean up duplicate declarations in `BluetoothManager.kt`
- `[ ]` Implement service discovery and characteristic setup in `onServicesDiscovered`
    - `[ ]` Find CSC Service (0x1816)
    - `[ ]` Find CSC Measurement Characteristic (0x2A5B)
    - `[ ]` Enable notifications for the characteristic
    - `[ ]` Write to CCCD descriptor to enable updates
- `[ ]` Implement data parsing in `onCharacteristicChanged`
    - `[ ]` Parse flags byte
    - `[ ]` Extract speed value (16-bit unsigned int)
    - `[ ]` Update `_speed` StateFlow
- `[ ]` Verify implementation (Compile check)