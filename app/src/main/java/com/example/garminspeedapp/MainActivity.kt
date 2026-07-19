package com.example.garminspeedapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.garminspeedapp.ui.theme.GarminSpeedAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // In a real app, these should be in a ViewModel and injected via Hilt/Koin
        val scanner = BluetoothScanner(applicationContext)
        val manager = BluetoothManager(applicationContext)

        setContent {
            GarminSpeedAppTheme {
                val context = androidx.compose.ui.platform.LocalContext.current
                
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val allGranted = permissions.entries.all { it.value }
                    if (!allGranted) {
                        // Handle permission denial if needed
                    }
                }

                // Launcher for Bluetooth enablement request
                val bluetoothEnableLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode != android.app.Activity.RESULT_OK) {
                        android.widget.Toast.makeText(context, "Bluetooth enablement failed", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BluetoothScreen(
                        scanner = scanner,
                        manager = manager,
                        modifier = Modifier.padding(innerPadding),
                        permissionLauncher = permissionLauncher,
                        onBluetoothEnableRequest = {
                            val enableBtIntent = android.content.Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            bluetoothEnableLauncher.launch(enableBtIntent)
                        }
                    )
                }
            }
        }
    }

    // Removed permissionLauncher from here as it must be called in Composable context

}

@Composable
fun BluetoothScreen(
    scanner: BluetoothScanner,
    manager: BluetoothManager,
    modifier: Modifier = Modifier,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    onBluetoothEnableRequest: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val devices by scanner.discoveredDevices.collectAsState()
    val isScanning by scanner.isScanning.collectAsState()
    val connectionState by manager.connectionState.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Bluetooth Scanner", fontSize = 24.sp, fontWeight = FontWeight
            .Bold)
        Spacer(modifier = Modifier.height(8.dp))

        val stateText = when (connectionState) {
            is BluetoothManager.ConnectionState.Connected -> "Connected"
            is BluetoothManager.ConnectionState.Connecting -> "Connecting..."
            is BluetoothManager.ConnectionState.Disconnected -> "Disconnected"
            is BluetoothManager.ConnectionState.Error -> "Error: ${(connectionState as BluetoothManager.ConnectionState.Error).message}"
        }

        Text(
            text = "Status: $stateText",
            color = if (connectionState is BluetoothManager.ConnectionState.Error) Color.Red else Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                    onBluetoothEnableRequest()
                    return@Button
                }

                if (isScanning) {
                    scanner.stopScan()
                } else {
                    // Request permissions before scanning
                    val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
                        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                    }
                    
                    val allGranted = permissions.all {
                        androidx.core.content.ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    }

                    if (allGranted) {
                        scanner.startScan()
                    } else {
                        permissionLauncher.launch(permissions.toTypedArray())
                    }
                }
            },
            enabled = true
        ) {
            Text(text = if (isScanning) "Stop Scanning" else "Start Scanning")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(devices.toList()) { device ->
                DeviceItem(device = device, onClick = {
                    manager.connect(device)
                })
                HorizontalDivider()
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = device.name ?: "Unknown Device", fontWeight = FontWeight.Bold)
        Text(text = device.address, fontSize = 12.sp, color = Color.Gray)
    }
}
