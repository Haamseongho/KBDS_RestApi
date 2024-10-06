package com.kbds.unit.project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kbds.unit.project.collections.model.ChildReqItem
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.database.Dao.CollectionDao
import com.kbds.unit.project.database.Dao.HistoryDao
import com.kbds.unit.project.database.Dao.RequestDao
import com.kbds.unit.project.database.model.HistoryItem
import com.kbds.unit.project.database.model.RequestItem


@Database(entities = [RequestItem::class, CollectionItem::class, HistoryItem::class, ChildReqItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun requestDao(): RequestDao
    abstract fun collectionDao(): CollectionDao
    abstract fun historyDao() : HistoryDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context?): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context!!.applicationContext,
                        AppDatabase::class.java,
                        "app-database5.db"
                    ).addCallback(object: RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("PRAGMA foreign_keys=ON;")
                        }
                    }).build()
                }
            }
            return INSTANCE
        }
    }
}