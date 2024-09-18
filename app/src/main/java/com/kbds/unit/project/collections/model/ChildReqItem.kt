package com.kbds.unit.project.collections.model

import androidx.room.PrimaryKey
import java.util.UUID

data class ChildReqItem(
    val uid: String = UUID.randomUUID().toString(),
    val id: Int,
    val type: String,
    val title: String,
) {
}
