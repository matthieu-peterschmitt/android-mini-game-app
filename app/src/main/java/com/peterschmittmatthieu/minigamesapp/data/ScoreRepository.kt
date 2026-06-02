package com.peterschmittmatthieu.minigamesapp.data

/**
 * Couche d'abstraction entre les ViewModels et le DAO. Centralise l'acces aux
 * donnees et respecte la separation des responsabilites.
 */
class ScoreRepository(private val scoreDao: ScoreDao) {

    suspend fun insertScore(score: Score) = scoreDao.insertScore(score)

    suspend fun getTopScores(): List<Score> = scoreDao.getTopScores()
}
