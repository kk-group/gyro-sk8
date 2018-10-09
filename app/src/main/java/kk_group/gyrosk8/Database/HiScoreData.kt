package kk_group.gyrosk8.Database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "hiScores")
data class HiScoreData(
        @PrimaryKey(autoGenerate = true) var id: Int,
        val userName: String,
        val score: Int) {

    override fun toString(): String {
        return "USR: $userName  SCORE: $score"
    }
}