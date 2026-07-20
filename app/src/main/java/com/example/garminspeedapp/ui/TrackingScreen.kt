package com.example.garminspeedapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.garminspeedapp.BluetoothManager
import java.util.*

@Composable
fun TrackingScreen(
    sessionManager: SessionManager,
    bluetoothManager: BluetoothManager,
    modifier: Modifier = Modifier,
    onBackToScanner: () -> Unit
) {
    val sessionState by sessionManager.sessionState.collectAsState()
    val connectionState by bluetoothManager.connectionState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onBackToScanner, modifier = Modifier.align(Alignment.Start)) {
            Text("Back to Scanner")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Tracking Session", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        // Speed Display
        Text(
            text = "%.1f".format(sessionState.currentSpeedMph),
            fontSize = 80.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "MPH", fontSize = 24.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Time", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = formatDuration(sessionState.elapsedTimeSeconds),
                    fontSize = 20.sp
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Distance", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "%.2f mi".format(sessionState.distanceMiles),
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { sessionManager.startSession() },
                enabled = sessionState.status != SessionStatus.Running,
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                Text("Start")
            }

            Button(
                onClick = { sessionManager.stopSession() },
                enabled = sessionState.status == SessionStatus.Running,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                Text("Stop")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { bluetoothManager.disconnect() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Disconnect Device")
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
