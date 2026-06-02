package com.peterschmittmatthieu.minigamesapp.ui.reaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.peterschmittmatthieu.minigamesapp.data.AppDatabase
import com.peterschmittmatthieu.minigamesapp.data.Score
import com.peterschmittmatthieu.minigamesapp.data.ScoreRepository
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
class ReactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        ScoreRepository(AppDatabase.getDatabase(application).scoreDao())

    /** Pseudo du joueur courant, fixe au demarrage de la partie. */
    private var playerName: String = ""

    enum class Phase { READY, PLAYING, RESULT }

    data class ReactionUiState(
        val phase: Phase = Phase.READY,
        val target: Int = 0,
        val currentMs: Int = 0,
        val gap: Int = 0,
        // Bonus : options choisies avant la partie.
        val blindMode: Boolean = false,
        val variableSpeed: Boolean = false,
        // Bonus timer aveugle : vrai quand le timer doit etre masque.
        val timerHidden: Boolean = false,
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
    fun startGame(playerName: String) {
        this.playerName = playerName
        val options = _uiState.value
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
            blindMode = options.blindMode,
            variableSpeed = options.variableSpeed,
        )
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var ticks = 0
            while (true) {
                delay(10L)
                ticks++
                // Bonus vitesse variable : on change l'amplitude (en gardant le
                // sens, pour que la cible reste atteignable) periodiquement.
                if (_uiState.value.variableSpeed && ticks % SPEED_CHANGE_TICKS == 0) {
                    val sign = if (step >= 0) 1 else -1
                    step = Random.nextInt(5, 26) * sign
                }
                _uiState.update {
                    val next = it.currentMs + step
                    // Bonus timer aveugle : on masque le timer a l'approche.
                    val hidden = it.blindMode && abs(next - it.target) < BLIND_THRESHOLD
                    it.copy(currentMs = next, timerHidden = hidden)
                }
            }
        }
    }

    /** Active/desactive le mode "timer aveugle" (avant la partie). */
    fun toggleBlindMode() {
        if (_uiState.value.phase != Phase.READY) return
        _uiState.update { it.copy(blindMode = !it.blindMode) }
    }

    /** Active/desactive le mode "vitesse variable" (avant la partie). */
    fun toggleVariableSpeed() {
        if (_uiState.value.phase != Phase.READY) return
        _uiState.update { it.copy(variableSpeed = !it.variableSpeed) }
    }

    /** Stoppe le timer, calcule l'ecart a la cible et sauvegarde le score. */
    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update {
            it.copy(phase = Phase.RESULT, gap = abs(it.currentMs - it.target))
        }
        saveScore()
    }

    /** Retour a l'etat initial (READY) en conservant les options choisies. */
    fun reset() {
        timerJob?.cancel()
        timerJob = null
        val options = _uiState.value
        _uiState.value = ReactionUiState(
            blindMode = options.blindMode,
            variableSpeed = options.variableSpeed,
        )
    }

    /**
     * Enregistre le score de la partie. L'ecart (plus petit = meilleur) est
     * converti en points (plus grand = meilleur) pour le classement commun.
     */
    private fun saveScore() {
        val points = (MAX_POINTS - _uiState.value.gap).coerceAtLeast(0)
        viewModelScope.launch {
            repository.insertScore(
                Score(playerName = playerName, gameName = GAME_NAME, score = points),
            )
        }
    }

    companion object {
        const val GAME_NAME = "Reaction"
        private const val MAX_POINTS = 10_000

        /** Sous cet ecart (ms), le timer aveugle se masque. */
        private const val BLIND_THRESHOLD = 1_500

        /** Nombre de ticks (x10 ms) entre deux changements de vitesse. */
        private const val SPEED_CHANGE_TICKS = 60
    }
}
