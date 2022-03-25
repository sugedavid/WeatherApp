package com.sogoamobile.weatherapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Notes (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val notes: String,
    val date: String

    )