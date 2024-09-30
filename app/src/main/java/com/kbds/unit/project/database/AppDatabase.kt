package com.kbds.unit.project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.database.Dao.CollectionDao
import com.kbds.unit.project.database.Dao.HistoryDao
import com.kbds.unit.project.database.Dao.RequestDao
import com.kbds.unit.project.database.model.HistoryItem
import com.kbds.unit.project.database.model.RequestItem


@Database(entities = [RequestItem::class, CollectionItem::class, HistoryItem::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun requestDao(): RequestDao
    abstract fun collectionDao(): CollectionDao
    abstract fun historyDao() : HistoryDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app-database2.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}