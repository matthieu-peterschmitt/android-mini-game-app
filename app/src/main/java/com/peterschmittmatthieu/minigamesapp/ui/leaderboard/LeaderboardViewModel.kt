package com.peterschmittmatthieu.minigamesapp.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.peterschmittmatthieu.minigamesapp.data.AppDatabase
import com.peterschmittmatthieu.minigamesapp.data.Score
import com.peterschmittmatthieu.minigamesapp.data.ScoreRepository
import com.peterschmittmatthieu.minigamesapp.ui.reaction.ReactionViewModel
import com.peterschmittmatthieu.minigamesapp.ui.wordgame.WordGameViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Charge et expose les scores pour l'ecran leaderboard, avec filtre par jeu,
 * statistiques personnelles et reinitialisation (bonus TP3). */
class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    /** Filtre du leaderboard. [gameName] null = toutes parties confondues. */
    enum class GameFilter(val label: String, val gameName: String?) {
        ALL("Tous", null),
        REACTION("Reaction", ReactionViewModel.GAME_NAME),
        WORDGAME("Mot cache", WordGameViewModel.GAME_NAME),
    }

    /** Statistiques du joueur courant. */
    data class PersonalStats(val games: Int, val averageScore: Int)

    private val repository =
        ScoreRepository(AppDatabase.getDatabase(application).scoreDao())

    private var playerName: String = ""

    private val _scores = MutableStateFlow<List<Score>>(emptyList())
    val scores: StateFlow<List<Score>> = _scores.asStateFlow()

    private val _filter = MutableStateFlow(GameFilter.ALL)
    val filter: StateFlow<GameFilter> = _filter.asStateFlow()

    private val _stats = MutableStateFlow<PersonalStats?>(null)
    val stats: StateFlow<PersonalStats?> = _stats.asStateFlow()

    init {
        loadScores()
    }

    /** Definit le joueur courant (pour les statistiques personnelles). */
    fun setPlayer(name: String) {
        if (playerName == name) return
        playerName = name
        loadStats()
    }

    /** Change le filtre de jeu et recharge la liste. */
    fun setFilter(newFilter: GameFilter) {
        if (_filter.value == newFilter) return
        _filter.value = newFilter
        loadScores()
    }

    /** Supprime tous les scores puis rafraichit l'affichage. */
    fun clearScores() {
        viewModelScope.launch {
            repository.clearAll()
            loadScores()
            loadStats()
        }
    }

    private fun loadScores() {
        viewModelScope.launch {
            val gameName = _filter.value.gameName
            _scores.value = if (gameName == null) {
                repository.getTopScores()
            } else {
                repository.getTopScoresByGame(gameName)
            }
        }
    }

    private fun loadStats() {
        if (playerName.isBlank()) {
            _stats.value = null
            return
        }
        viewModelScope.launch {
            val games = repository.getGameCount(playerName)
            val average = repository.getAverageScore(playerName) ?: 0.0
            _stats.value = if (games > 0) PersonalStats(games, average.toInt()) else null
        }
    }
}
