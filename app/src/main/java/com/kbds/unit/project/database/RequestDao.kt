package com.kbds.unit.project.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.database.model.RequestItem

@Dao
interface RequestDao {
    @Query("DELETE FROM RequestTB WHERE reqId = :id")
    suspend fun deleteByReqId(id: Int)

    @Query("DELETE FROM RequestTB WHERE title = :reqTitle")
    suspend fun deleteByReqTitle(reqTitle: String)

    @Query("DELETE FROM RequestTB WHERE collectionId = :id")
    suspend fun deleteByCollectionId(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReqItem(requestItem: RequestItem)

    @Query("UPDATE RequestTB SET title = :title WHERE reqId = :reqId")
    suspend fun updateReqTitleByReqId(title: String, reqId: Int)

    @Query("UPDATE RequestTB SET title = :AfterTitle WHERE title = :prevTitle")
    suspend fun updateReqTitleByReqTitle(AfterTitle: String, prevTitle: String)

    @Query("SELECT * FROM RequestTB ORDER BY reqId DESC")
    suspend fun getAll(): List<RequestItem>
}