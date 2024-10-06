package com.kbds.unit.project.collections.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.kbds.unit.project.database.model.RequestItem

@Entity(tableName = "CollectionTB")
data class CollectionItem(
    @PrimaryKey(autoGenerate = true) var cId: Int = 0,
    var id: String = "", // Collections 탭에서 아이디
    val title: String,
    val requestCount: Int,
    var isExpanded: Boolean = false  // 추가
)

data class CollectionWithRequests(
    @Embedded val collectionItem: CollectionItem,
    @Relation(
        parentColumn = "cId",  // CollectionItem의 cId와 연결
        entityColumn = "collectionId"  // RequestItem의 외래 키 (collectionId)
    )
    val requestList: List<RequestItem>
)
