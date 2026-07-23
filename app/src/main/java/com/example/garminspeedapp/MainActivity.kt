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
import androidx.activity.result.ActivityResultLauncher
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
import com.example.garminspeedapp.data.AppDatabase
import com.example.garminspeedapp.data.RideRepository
import com.example.garminspeedapp.ui.SessionManager
import com.example.garminspeedapp.ui.AppScreen
import com.example.garminspeedapp.ui.TrackingScreen
import com.example.garminspeedapp.ui.theme.GarminSpeedAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scanner = BluetoothScanner(applicationContext)
        val manager = BluetoothManager(applicationContext)
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = RideRepository(database.rideDao())
        val sessionManager = SessionManager(manager, repository)

        setContent {
            GarminSpeedAppTheme {
                val context = androidx.compose.ui.platform.LocalContext.current
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Scanner) }
                
                val connectionState by manager.connectionState.collectAsState()

                // React to connection state changes for navigation
                LaunchedEffect(connectionState) {
                    if (connectionStatusIsConnected(connectionState)) {
                        currentScreen = AppScreen.Tracking
                        sessionManager.startSession()
                    } else if (connectionStatusIsDisconnected(connectionState)) {
                        currentScreen = AppScreen.Scanner
                        // We don't necessarily stop the session here, 
                        // as it might be a transient disconnection, 
                        // but in this app, we'll assume connection loss stops tracking.
                        sessionManager.stopSession()
                    }
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    // Handle it
                }

                val bluetoothEnableLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode != android.app.Activity.RESULT_OK) {
                        android.widget.Toast.makeText(context, "Bluetooth enablement failed", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val screen = currentScreen) {
                        is AppScreen.Scanner -> BluetoothScreen(
                            scanner = scanner,
                            manager = manager,
                            modifier = Modifier.padding(innerPadding),
                            permissionLauncher = permissionLauncher,
                            onBluetoothEnableRequest = {
                                val enableBtIntent = android.content.Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                bluetoothEnableLauncher.launch(enableBtIntent)
                            },
                            onDeviceConnected = {
                                currentScreen = AppScreen.Tracking
                            }
                        )
                        is AppScreen.Tracking -> TrackingScreen(
                            sessionManager = sessionManager,
                            bluetoothManager = manager,
                            modifier = Modifier.padding(innerPadding),
                            onBackToScanner = {
                                currentScreen = AppScreen.Scanner
                            }
                        )
                    }
                }
            }
        }
    }

    private fun connectionStatusIsConnected(state: BluetoothManager.ConnectionState): Boolean {
        return state is BluetoothManager.ConnectionState.Connected
    }

    private fun connectionStatusIsDisconnected(state: BluetoothManager.ConnectionState): Boolean {
        return state is BluetoothManager.ConnectionState.Disconnected
    }
    
    // I'll add these helper functions to MainActivity if needed, 
    // but let's see if they are actually used elsewhere.
}

@Composable
fun BluetoothScreen(
    scanner: BluetoothScanner,
    manager: BluetoothManager,
    modifier: Modifier = Modifier,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    onBluetoothEnableRequest: () -> Unit,
    onDeviceConnected: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val devices by scanner.discoveredDevices.collectAsState()
    val isScanning by scanner.isScanning.collectAsState()
    val connectionState by manager.connectionState.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Bluetooth Scanner", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                    val requiredPermissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
                        requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                    }

                    val ungrantedPermissions = requiredPermissions.filter {
                        androidx.core.content.ContextCompat.checkSelfPermission(context, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    }

                    if (ungrantedPermissions.isEmpty()) {
                        scanner.startScan(stopOnDiscovery = true)
                    } else {
                        permissionLauncher.launch(ungrantedPermissions.toTypedArray())
                    }
                }
            },
            enabled = true
        ) {
            Text(text = if (isScanning) "Stop Scanning" else "Start Scanning...")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(devices.toList()) { device ->
                DeviceItem(device = device, onClick = {
                    manager.connect(device)
                    onDeviceConnected()
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
