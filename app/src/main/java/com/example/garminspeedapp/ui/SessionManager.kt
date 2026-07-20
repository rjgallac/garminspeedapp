package com.example.garminspeedapp.ui

import android.content.Context
import com.example.garminspeedapp.BluetoothManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SessionManager(
    private val context: Context,
    private val bluetoothManager: BluetoothManager
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null
    private var lastUpdateTimeMillis: Long = 0L

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState = _sessionState.asStateFlow()

    fun startSession() {
        if (_sessionState.value.status == SessionStatus.Running) return

        _sessionState.value = SessionState(
            status = SessionStatus.Running,
            elapsedTimeSeconds = 0L,
            distanceMiles = 0.0,
            currentSpeedMph = bluetoothManager.speed.value
        )
        lastUpdateTimeMillis = System.currentTimeMillis()

        startTimer()
        observeSpeed()
    }

    fun stopSession() {
        timerJob?.cancel()
        _sessionState.value = _sessionState.value.copy(status = SessionStatus.Stopped)
        // TODO: Implement saving to Room via Repository
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                delay(1000)
                _sessionState.value = _sessionState.value.copy(
                    elapsedTimeSeconds = _sessionState.value.elapsedTimeSeconds + 1
                )
            }
        }
    }

    private fun observeSpeed() {
        scope.launch {
            bluetoothManager.speed.collect { speedMph ->
                if (_sessionState.value.status == SessionStatus.Running) {
                    updateDistance(speedMph)
                }
            }
        }
    }

    private fun updateDistance(speedMph: Float) {
        val currentTime = System.currentTimeMillis()
        if (lastUpdateTimeMillis != 0L) {
            val deltaTimeSeconds = (currentTime - lastUpdateTimeMillis) / 1000.0
            // Speed is in MPH. Distance (miles) = Speed (mph) * Time (hours)
            // Time in hours = deltaTimeSeconds / 3600
            val deltaMiles = speedMph * (deltaTimeSeconds / 3600.0)
            
            _sessionState.value = _sessionState.value.copy(
                distanceMiles = _sessionState.value.distanceMiles + deltaMiles,
                currentSpeedMph = speedMph
            )
        }
        lastUpdateTimeMillis = currentTime
    }
}
