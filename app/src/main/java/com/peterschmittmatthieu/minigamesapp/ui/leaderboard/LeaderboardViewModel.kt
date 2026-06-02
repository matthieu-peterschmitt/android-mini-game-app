package com.peterschmittmatthieu.minigamesapp.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.peterschmittmatthieu.minigamesapp.data.AppDatabase
import com.peterschmittmatthieu.minigamesapp.data.Score
import com.peterschmittmatthieu.minigamesapp.data.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Charge et expose les 10 meilleurs scores pour l'ecran leaderboard. */
class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        ScoreRepository(AppDatabase.getDatabase(application).scoreDao())

    private val _scores = MutableStateFlow<List<Score>>(emptyList())
    val scores: StateFlow<List<Score>> = _scores.asStateFlow()

    init {
        viewModelScope.launch {
            _scores.value = repository.getTopScores()
        }
    }
}
