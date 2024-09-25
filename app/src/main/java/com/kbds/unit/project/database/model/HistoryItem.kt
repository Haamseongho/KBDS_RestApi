package com.kbds.unit.project.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.Date

@Entity(tableName = "HistoryTB")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val hId: Int = 0,
    val reqId: Int,
    val collectionId: Int,
    val date: String,
    val title: String,
    val type: String,
    val url: String,
    val params: MutableMap<String, String> ?= null,
    val headers: MutableMap<String, String> ?= null,
    val body: String
)
