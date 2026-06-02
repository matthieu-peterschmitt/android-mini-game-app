package com.peterschmittmatthieu.minigamesapp.ui.wordgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.peterschmittmatthieu.minigamesapp.ui.theme.MiniGamesAppTheme
import com.peterschmittmatthieu.minigamesapp.ui.wordgame.WordGameViewModel.Cell
import com.peterschmittmatthieu.minigamesapp.ui.wordgame.WordGameViewModel.Phase

/**
 * Jeu "Mot cache" : retrouver le mot cache dans une grille 3x3 de lettres.
 * Observe [WordGameViewModel] ; une nouvelle partie demarre a chaque entree
 * en composition.
 */
@Composable
fun WordGameScreen(
    playerName: String,
    onBackClick: () -> Unit,
    viewModel: WordGameViewModel = viewModel(),
) {
    LaunchedEffect(Unit) { viewModel.startGame(playerName) }
    val state by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (state.phase) {
                Phase.PLAYING -> PlayingContent(
                    state = state,
                    onCellClick = viewModel::selectCell,
                    onErase = viewModel::eraseLast,
                    onValidate = viewModel::validate,
                    onPass = viewModel::pass,
                    onReveal = viewModel::revealWord,
                    onHint = viewModel::useHint,
                    onBackClick = onBackClick,
                )

                Phase.GAME_OVER -> GameOverContent(
                    score = state.score,
                    onReplay = viewModel::reset,
                    onBackClick = onBackClick,
                )
            }
        }
    }
}

@Composable
private fun PlayingContent(
    state: WordGameViewModel.WordGameUiState,
    onCellClick: (Int) -> Unit,
    onErase: () -> Unit,
    onValidate: () -> Unit,
    onPass: () -> Unit,
    onReveal: () -> Unit,
    onHint: () -> Unit,
    onBackClick: () -> Unit,
) {
    Text(
        text = "${state.timeLeft}s",
        fontSize = 48.sp,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        text = "Score : ${state.score}",
        style = MaterialTheme.typography.titleMedium,
    )
    Text(
        text = "Mot de ${state.wordLength} lettres",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp),
    )
    if (state.hintLetter != null) {
        Text(
            text = "Commence par : ${state.hintLetter}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
    if (state.revealedWord != null) {
        Text(
            text = "Reponse : ${state.revealedWord}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp),
        )
    }

    // Zone de saisie + effacer
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp),
    ) {
        Text(
            text = state.typedWord.ifEmpty { "..." },
            fontSize = 28.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp,
        )
        OutlinedButton(
            onClick = onErase,
            enabled = state.selectedIndices.isNotEmpty(),
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text("⌫") // ⌫
        }
    }

    // Grille 3x3
    val rows = state.grid.chunked(3)
    rows.forEachIndexed { rowIndex, row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            row.forEachIndexed { colIndex, cell ->
                val index = rowIndex * 3 + colIndex
                LetterCell(cell = cell, onClick = { onCellClick(index) })
            }
        }
        if (rowIndex < rows.lastIndex) {
            Spacer(Modifier.size(8.dp))
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 24.dp),
    ) {
        Button(onClick = onValidate, enabled = state.selectedIndices.isNotEmpty()) {
            Text("Valider")
        }
        OutlinedButton(onClick = onPass) {
            Text("Passer")
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        OutlinedButton(
            onClick = onHint,
            enabled = !state.hintUsed,
        ) {
            Text("Indice (-1)")
        }
        OutlinedButton(
            onClick = onReveal,
            enabled = state.revealedWord == null,
        ) {
            Text("Reponse")
        }
    }

    OutlinedButton(
        onClick = onBackClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        Text("Accueil")
    }
}

@Composable
private fun LetterCell(cell: Cell, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !cell.isSelected,
        modifier = Modifier
            .size(72.dp)
            .aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(),
    ) {
        Text(text = cell.char.toString(), fontSize = 24.sp)
    }
}

@Composable
private fun GameOverContent(
    score: Int,
    onReplay: () -> Unit,
    onBackClick: () -> Unit,
) {
    Text(
        text = "Termine !",
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        text = "Mots trouves : $score",
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 12.dp),
    )
    Button(
        onClick = onReplay,
        modifier = Modifier.padding(top = 32.dp),
    ) {
        Text("Rejouer")
    }
    OutlinedButton(
        onClick = onBackClick,
        modifier = Modifier
            .width(200.dp)
            .padding(top = 12.dp),
    ) {
        Text("Accueil")
    }
}

@Preview(showBackground = true)
@Composable
private fun WordGameScreenPreview() {
    MiniGamesAppTheme {
        WordGameScreen(playerName = "Joueur", onBackClick = {})
    }
}
