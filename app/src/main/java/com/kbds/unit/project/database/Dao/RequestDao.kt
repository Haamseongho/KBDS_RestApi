package com.kbds.unit.project.database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.database.model.RequestItem

@Dao
interface RequestDao {

    @Insert
    suspend fun insertNewCollection(requestItem: RequestItem)
    @Query("DELETE FROM RequestTB WHERE reqId = :id")
    suspend fun deleteByReqId(id: Int)

    @Query("DELETE FROM RequestTB WHERE title = :reqTitle")
    suspend fun deleteByReqTitle(reqTitle: String)

    @Transaction
    @Query("DELETE FROM RequestTB WHERE reqId = :reqId")
    suspend fun deleteByChildCollectionId(reqId: Int)

    @Query("SELECT reqId FROM RequestTB WHERE title = :title and type = :type and collectionId = :cId")
    suspend fun findReqIdByOtherData(title: String, type: String, cId: Int) : Int

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

    @Query("UPDATE RequestTB SET title = :afterTitle, url = :url WHERE title = :prevTitle and reqId = :reqId")
    suspend fun updateReqItemUrl(afterTitle: String, url: String, prevTitle: String, reqId: Int)

    @Query("SELECT reqId FROM REQUESTTB WHERE title = :afterTitle and collectionId = :cId")
    suspend fun getReqIdByTitleAndCid(afterTitle: String, cId: Int) : Int?

    // List가져오기
    @Query("SELECT * FROM REQUESTTB WHERE reqId = :reqId")
    suspend fun getRequestListByReqId(reqId: Int) : List<RequestItem>

    // Collection ID로 사이즈 가져오기
    @Query("SELECT * FROM REQUESTTB WHERE collectionId = :cId")
    suspend fun getSizeFromCollectionId(cId: Int) : List<RequestItem>
}