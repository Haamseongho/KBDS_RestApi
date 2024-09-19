package com.kbds.unit.project.collections.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
@Entity(tableName = "ChildRequestTB")
data class ChildReqItem(
    @PrimaryKey(autoGenerate = true) val reqId: Int = 0,  // API 화면이랑 1:1 매핑해서 넣을 것
    val collectionId: Int,  // CollectionItem의 id와 일치시켜서 1:N 형태로 넣을것
    val type: String, // POST, GET, PARAM, DELETE, PUT, ...
    val title: String, // RequestName
)
