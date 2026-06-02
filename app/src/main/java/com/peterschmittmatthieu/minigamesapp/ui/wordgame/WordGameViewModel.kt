package com.peterschmittmatthieu.minigamesapp.ui.wordgame

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

/**
 * Detient l'etat et la logique du jeu "Mot cache". A chaque manche une grille
 * 3x3 est generee : un mot est cache parmi ses lettres melangees avec quelques
 * lettres aleatoires. Le joueur a 60 secondes pour trouver un maximum de mots.
 */
class WordGameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        ScoreRepository(AppDatabase.getDatabase(application).scoreDao())

    /** Pseudo du joueur courant, fixe au demarrage de la partie. */
    private var playerName: String = ""

    enum class Phase { PLAYING, GAME_OVER }

    /** Une cellule de la grille. Desactivee une fois selectionnee. */
    data class Cell(
        val char: Char,
        val isSelected: Boolean = false,
    )

    data class WordGameUiState(
        val phase: Phase = Phase.PLAYING,
        val grid: List<Cell> = emptyList(),
        val selectedIndices: List<Int> = emptyList(),
        val score: Int = 0,
        val timeLeft: Int = GAME_DURATION_SECONDS,
        val wordLength: Int = 0,
        val revealedWord: String? = null,
    ) {
        /** Lettres deja selectionnees, dans l'ordre. */
        val typedWord: String get() = selectedIndices.map { grid[it].char }.joinToString("")
    }

    private val _uiState = MutableStateFlow(WordGameUiState())
    val uiState: StateFlow<WordGameUiState> = _uiState.asStateFlow()

    private var hiddenWord: String = ""
    private var timerJob: Job? = null

    private val wordList = listOf(
        "SOLEIL", "MAISON", "JARDIN", "CHEMIN", "BOUTON",
        "MIROIR", "PLANTE", "CARTON", "FUSEAU", "CITRON",
        "VIOLON", "RAPIDE", "BLOQUE", "MOUTON", "GATEAU",
    )

    /** Demarre le timer et charge la premiere grille. */
    fun startGame(playerName: String) {
        this.playerName = playerName
        timerJob?.cancel()
        _uiState.value = WordGameUiState(phase = Phase.PLAYING)
        newGrid()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1_000L)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            _uiState.update { it.copy(phase = Phase.GAME_OVER) }
            saveScore()
        }
    }

    /** Ajoute la lettre de la cellule au mot et desactive la cellule. */
    fun selectCell(index: Int) {
        val state = _uiState.value
        if (state.phase != Phase.PLAYING || state.grid[index].isSelected) return
        val grid = state.grid.toMutableList()
        grid[index] = grid[index].copy(isSelected = true)
        _uiState.update {
            it.copy(grid = grid, selectedIndices = it.selectedIndices + index)
        }
    }

    /** Retire la derniere lettre saisie et reactive sa cellule. */
    fun eraseLast() {
        val state = _uiState.value
        val last = state.selectedIndices.lastOrNull() ?: return
        val grid = state.grid.toMutableList()
        grid[last] = grid[last].copy(isSelected = false)
        _uiState.update {
            it.copy(grid = grid, selectedIndices = it.selectedIndices.dropLast(1))
        }
    }

    /** Valide le mot : si correct, incremente le score et charge une grille. */
    fun validate() {
        if (_uiState.value.phase != Phase.PLAYING) return
        if (_uiState.value.typedWord == hiddenWord) {
            _uiState.update { it.copy(score = it.score + 1) }
            newGrid()
        }
    }

    /** Passe la grille courante sans marquer de point. */
    fun pass() {
        if (_uiState.value.phase != Phase.PLAYING) return
        newGrid()
    }

    /** Revele le mot cache de la grille courante (sans marquer de point). */
    fun revealWord() {
        if (_uiState.value.phase != Phase.PLAYING) return
        _uiState.update { it.copy(revealedWord = hiddenWord) }
    }

    /** Retour a PLAYING avec score et timer remis a zero. */
    fun reset() {
        startGame(playerName)
    }

    /** Enregistre le score final (nombre de mots trouves) en base. */
    private fun saveScore() {
        val finalScore = _uiState.value.score
        viewModelScope.launch {
            repository.insertScore(
                Score(playerName = playerName, gameName = GAME_NAME, score = finalScore),
            )
        }
    }

    /** Genere une nouvelle grille : 6 lettres du mot + 3 lettres aleatoires. */
    private fun newGrid() {
        val word = wordList.random()
        hiddenWord = word
        val letters = word.toMutableList()
        repeat(GRID_SIZE - word.length) { letters.add(('A'..'Z').random()) }
        letters.shuffle()
        _uiState.update {
            it.copy(
                grid = letters.map { c -> Cell(c) },
                selectedIndices = emptyList(),
                wordLength = word.length,
                revealedWord = null,
            )
        }
    }

    companion object {
        const val GAME_NAME = "Mot cache"
        const val GAME_DURATION_SECONDS = 60
        private const val GRID_SIZE = 9
    }
}
