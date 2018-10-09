package kk_group.gyrosk8.Database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface HiScoreDao {
//    @Query("SELECT * FROM hiScores")
//    fun getHiScores()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hiScore: HiScoreData)

}