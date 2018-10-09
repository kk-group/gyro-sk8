package kk_group.gyrosk8.Database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(HiScoreData::class)], version = 1, exportSchema = true)

abstract class HiScoreDatabase: RoomDatabase() {
    abstract fun hiScoreDao(): HiScoreDao

    /* one and only one instance */
    companion object{
        private var sInstance: HiScoreDatabase? = null

        @Synchronized
        fun get(context: Context): HiScoreDatabase {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext, HiScoreDatabase::class.java, "hiScores.db").build()
            }
            return sInstance!!
        }
    }
}