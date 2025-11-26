package com.example.calmflight.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flight_sessions")
data class FlightSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val expectedFear: Int, // 1-10
    val actualFear: Int? = null // 1-10
)


