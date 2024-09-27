package com.kbds.unit.project.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kbds.unit.project.database.model.HistoryItem

@Dao
interface HistoryDao {
    @Insert
    suspend fun insertHistory(historyItem: HistoryItem)
}