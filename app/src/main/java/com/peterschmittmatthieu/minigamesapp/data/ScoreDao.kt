package com.peterschmittmatthieu.minigamesapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {

    @Insert
    suspend fun insertScore(score: Score)

    /** Les 10 meilleurs scores, toutes parties confondues. */
    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 10")
    suspend fun getTopScores(): List<Score>
}
