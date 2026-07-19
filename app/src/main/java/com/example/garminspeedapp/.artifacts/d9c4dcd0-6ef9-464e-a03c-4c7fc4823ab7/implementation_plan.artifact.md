# Fix Errors and Warnings in MainActivity.kt

This plan aims to resolve compilation errors and address deprecation warnings and architectural issues in `MainActivity.kt` to ensure a stable and modern Android implementation.

## User Review Required

> [!IMPORTANT]
> The refactoring of `permissionLauncher` involves moving stateful Compose logic from the Activity class into the Composable hierarchy, which is necessary for correctness but changes where the launcher is defined.

## Open Questions

- None at this time.

## Proposed Changes

### [MODIFY] MainActivity.kt

#### Refactor Permission Handling
- Move `permissionLauncher` from the `MainActivity` class property to a `@Composable` function within `setContent`. This fixes the error: `@Composable invocables can only happen from the context of a @Composable function`.
- Implement a basic response to permission denial (e.g., using `Toast`) instead of an empty block.

#### Refactor Bluetooth Enablement logic
- Replace the deprecated `startActivityForResult` with the modern `registerForActivityResult(ActivityResultContracts.StartActivityForResult())`.

#### Improve API Compatibility
- Add runtime checks for Android 12+ (API level 31) when requesting `BLUETOOTH_SCAN` and `BLUETOOTH_CONNECT` permissions to avoid crashes on older devices.

#### Code Cleanup
- Remove unused variable `context` in `BluetoothScreen`.
- Address deprecation warnings for `BluetoothAdapter.getDefaultAdapter()` if possible, though `BluetoothManager` is the preferred way in modern Android.
- Clean up empty `if` blocks.

## Verification Plan

### Automated Tests
- Run `gradle build` to ensure no compilation errors remain.

### Manual Verification
- Launch the app and verify that the UI renders correctly.
- Trigger a Bluetooth scan and check if permission requests appear for both older and newer Android versions.
- Verify that the "Bluetooth Enable" request works via the new `ActivityResultLauncher`.
