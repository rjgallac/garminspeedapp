package com.example.garminspeedapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BluetoothScreen(
                        scanner = scanner,
                        manager = manager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BluetoothScreen(
    scanner: BluetoothScanner,
    manager: BluetoothManager,
    modifier: Modifier = Modifier
) {
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
                if (isScanning) {
                    scanner.stopScan()
                } else {
                    scanner.startScan()
                }
            },
            enabled = isScanning || devices.isNotEmpty()
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
