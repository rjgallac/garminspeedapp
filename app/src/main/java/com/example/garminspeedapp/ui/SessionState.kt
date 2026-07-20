package com.example.garminspeedapp.ui

import androidx.compose.runtime.Immutable

@Immutable
sealed class AppScreen {
    object Scanner : AppScreen()
    object Tracking : AppScreen()
}

sealed class SessionStatus {
    object Idle : SessionStatus()
    object Running : SessionStatus()
    object Stopped : SessionStatus()
}

data class SessionState(
    val status: SessionStatus = SessionStatus.Idle,
    val elapsedTimeSeconds: Long = 0L,
    val distanceMiles: Double = 0.0,
    val currentSpeedMph: Float = 0f
)
