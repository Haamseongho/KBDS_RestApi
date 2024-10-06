package com.kbds.unit.project.database.Dao

import android.icu.text.CaseMap.Title
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.collections.model.CollectionWithRequests
import com.kbds.unit.project.database.model.RequestItem

@Dao
interface CollectionDao {
    @Insert
    fun insert(collectionItem: CollectionItem)
    @Delete
    fun delete(collectionItem: CollectionItem)
    @Update
    fun update(collectionItem: CollectionItem)

    @Query("SELECT * FROM CollectionTB ORDER BY cId DESC")
    fun getAll(): List<CollectionItem>

    @Query("DELETE FROM CollectionTB WHERE id = :id")
    fun deleteById(id: String)

    @Query("UPDATE CollectionTB SET title = :title WHERE id = :id")
    fun renameCollection(id: String, title: String)


    // 부모엔터티 삽입
    @Insert
    suspend fun insertCollection(collection: CollectionItem): Long // CID 반환

    // 자식 엔터티 삽입
    @Insert
    suspend fun insertRequest(requestItem: RequestItem)

    // 두 엔터티 함께 삽입
    @Transaction
    suspend fun insertCollectionWithRequests(collection: CollectionItem, requests: List<RequestItem>) {
        // 부모 엔터티 삽입 후 자동 생성된 cId 값을 가져옴
        val collectionId = insertCollection(collection).toInt()
        // 자식 엔터티 삽입 (부모의 collectionId를 자식의 외래 키로 설정)
        for (request in requests) {
            insertRequest(request.copy(collectionId = collectionId))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCollection(collectionItem: CollectionItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRequests(requestItems: List<RequestItem>)



    @Transaction
    suspend fun updateCollectionWithRequests(
        collectionItem: CollectionItem,
        requestItems: List<RequestItem>
    ) {
        insertOrUpdateCollection(collectionItem)
        insertOrUpdateRequests(requestItems)
    }


    // INSERT

    @Insert
    suspend fun insertOrUpdateCollection2(collectionItem: CollectionItem)

    // Request는 추가해야함
    @Insert
    suspend fun insertOrUpdateRequests2(requestItems: RequestItem)

    @Transaction
    suspend fun updateCollectionWithRequests2(
        collectionItem: CollectionItem,
        requestItems: RequestItem
    ) {
        insertOrUpdateCollection(collectionItem)
        insertOrUpdateRequests2(requestItems)
    }

    @Transaction
    @Query("SELECT * FROM COLLECTIONTB WHERE cId = :collectionId")
    suspend fun getCollectionWithRequests(collectionId: Int): CollectionWithRequests

    @Query("UPDATE CollectionTB SET requestCount = :size WHERE cId = :collectionId")
    suspend fun updateRequestCount(size: Int, collectionId: Int)

    // Collection Title 가지고 찾아서 리스트 추출
    @Query("SELECT * FROM CollectionTB WHERE title = :findTitle")
    suspend fun findTitleInHistory(findTitle: String) : List<CollectionItem>

    @Query("SELECT cId FROM collectionTB")
    suspend fun getAllCId() : List<Int>
}