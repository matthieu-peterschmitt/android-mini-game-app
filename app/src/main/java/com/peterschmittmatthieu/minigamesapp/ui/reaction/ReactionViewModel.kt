package com.peterschmittmatthieu.minigamesapp.ui.reaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

/**
 * Detient l'etat et la logique du jeu de reaction. Survit aux recompositions
 * et aux changements de configuration ; le composable se contente d'afficher
 * [uiState] et d'appeler ses methodes.
 */
class ReactionViewModel : ViewModel() {

    enum class Phase { READY, PLAYING, RESULT }

    data class ReactionUiState(
        val phase: Phase = Phase.READY,
        val target: Int = 0,
        val currentMs: Int = 0,
        val gap: Int = 0,
    )

    private val _uiState = MutableStateFlow(ReactionUiState())
    val uiState: StateFlow<ReactionUiState> = _uiState.asStateFlow()

    /** Pas applique toutes les 10 ms : amplitude (vitesse) et signe (sens). */
    private var step = 0
    private var timerJob: Job? = null

    init {
        reset()
    }

    /** Genere une nouvelle partie et lance le timer. */
    fun startGame() {
        val target = Random.nextInt(6_000, 18_000)
        val magnitude = Random.nextInt(5, 21)
        val offset = Random.nextInt(2_000, 5_000)
        val incrementing = Random.nextBoolean()
        // Le timer demarre toujours du cote oppose a la cible pour le sens
        // choisi, afin qu'il se dirige vers elle et reste atteignable.
        step = if (incrementing) magnitude else -magnitude
        val start = if (incrementing) target - offset else target + offset

        _uiState.value = ReactionUiState(
            phase = Phase.PLAYING,
            target = target,
            currentMs = start,
        )
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(10L)
                _uiState.update { it.copy(currentMs = it.currentMs + step) }
            }
        }
    }

    /** Stoppe le timer et calcule l'ecart a la cible. */
    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update {
            it.copy(phase = Phase.RESULT, gap = abs(it.currentMs - it.target))
        }
    }

    /** Retour a l'etat initial (READY), pret pour une nouvelle partie. */
    fun reset() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = ReactionUiState()
    }
}
