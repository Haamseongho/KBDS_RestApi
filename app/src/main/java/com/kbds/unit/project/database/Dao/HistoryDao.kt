package com.kbds.unit.project.database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kbds.unit.project.database.model.HistoryItem

@Dao
interface HistoryDao {
    @Insert
    suspend fun insertHistory(historyItem: HistoryItem)

    @Query("SELECT * FROM HistoryTB ORDER BY date DESC")
    suspend fun getAllHistoryData(): List<HistoryItem>

    @Query("DELETE FROM HistoryTB")
    fun deleteAllHistory(): Unit
}