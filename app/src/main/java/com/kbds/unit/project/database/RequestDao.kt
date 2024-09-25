package com.kbds.unit.project.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("UPDATE RequestTB SET title = :afterTitle WHERE title = :prevTitle")
    suspend fun updateReqTitleByReqTitle(afterTitle: String, prevTitle: String)

    @Query("SELECT * FROM RequestTB ORDER BY reqId DESC")
    suspend fun getAll(): List<RequestItem>

    // 만약 테이블에 없는것으로 진행될 경우 추가를 해줘야함(-> collectionId는 공백으로 들어갈 것이고, 나중에 Save Request를 통해 Collection Id를 Update해서
    // 매핑이 가능하도록 하면된다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReqItemUrl(requestItem: RequestItem)

    @Query("UPDATE RequestTB SET title = :afterTitle, url = :url WHERE title = :prevTitle")
    suspend fun updateReqItemUrl(afterTitle: String, url: String, prevTitle: String)

    @Query("SELECT reqId FROM RequestTB WHERE collectionId = :cid and title = :title and url = :url")
    suspend fun getReqIdInHistory(cid: Int, title: String, url: String)
}