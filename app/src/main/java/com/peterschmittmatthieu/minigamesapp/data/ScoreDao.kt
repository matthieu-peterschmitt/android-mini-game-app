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

    /** Les 10 meilleurs scores pour un jeu donne (bonus : filtre par jeu). */
    @Query("SELECT * FROM scores WHERE game_name = :gameName ORDER BY score DESC LIMIT 10")
    suspend fun getTopScoresByGame(gameName: String): List<Score>

    /** Nombre de parties jouees par un joueur (bonus : statistiques). */
    @Query("SELECT COUNT(*) FROM scores WHERE player_name = :playerName")
    suspend fun getGameCount(playerName: String): Int

    /** Score moyen d'un joueur, null s'il n'a jamais joue (bonus : statistiques). */
    @Query("SELECT AVG(score) FROM scores WHERE player_name = :playerName")
    suspend fun getAverageScore(playerName: String): Double?

    /** Supprime tous les scores (bonus : reinitialisation). */
    @Query("DELETE FROM scores")
    suspend fun clearAll()
}
