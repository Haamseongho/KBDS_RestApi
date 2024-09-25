package com.kbds.unit.project.database

import androidx.room.Dao
import androidx.room.Insert
import com.kbds.unit.project.database.model.HistoryItem

@Dao
interface HistoryDao {
    @Insert
    suspend fun insertHistoryItem(historyItem: HistoryItem)
}