package com.kbds.unit.project.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("HistoryTB")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val hId: Int = 0,
    val reqId: Int,
    val collectionId: Int,
    val date: String,
    val title: String,
    val type: String,
    val url: String,
    val params: String,
    val headers: String,
    val body: String

)

