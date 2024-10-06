package com.kbds.unit.project.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.versionedparcelable.VersionedParcelize
import com.kbds.unit.project.collections.model.CollectionItem


@Entity (
    tableName = "RequestTB",
    foreignKeys = [
        ForeignKey(
            entity = CollectionItem::class,
            parentColumns = ["cId"],
            childColumns = ["collectionId"],
            onDelete = CASCADE
        )
    ]
)
data class RequestItem(
    @PrimaryKey(autoGenerate = true) val reqId: Int = 0,  // API 화면이랑 1:1 매핑해서 넣을 것
    val collectionId: Int,  // CollectionItem의 id와 일치시켜서 1:N 형태로 넣을것
    val type: String, // POST, GET, PARAM, DELETE, PUT, ...
    val title: String, // RequestName
    val url: String? = null
)
