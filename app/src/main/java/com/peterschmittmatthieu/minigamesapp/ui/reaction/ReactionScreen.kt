package com.peterschmittmatthieu.minigamesapp.ui.reaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.peterschmittmatthieu.minigamesapp.ui.reaction.ReactionViewModel.Phase
import com.peterschmittmatthieu.minigamesapp.ui.theme.MiniGamesAppTheme

/**
 * Jeu de reaction : le joueur arrete un timer au plus pres d'une valeur cible.
 * Toute la logique vit dans [ReactionViewModel] ; cet ecran ne fait que lire
 * l'etat et appeler ses methodes.
 */
@Composable
fun ReactionScreen(
    playerName: String,
    onBackClick: () -> Unit,
    viewModel: ReactionViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.phase == Phase.READY) {
                Text(
                    text = "Arrete le timer au plus pres de la cible",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    text = "Cible",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = formatMs(state.target),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = formatMs(state.currentMs),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 56.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 48.dp),
                )
            }

            when (state.phase) {
                Phase.READY -> {
                    Button(
                        onClick = { viewModel.startGame(playerName) },
                        modifier = Modifier.padding(top = 48.dp),
                    ) {
                        Text("Demarrer")
                    }
                }

                Phase.PLAYING -> {
                    Button(onClick = { viewModel.stopTimer() }) {
                        Text("Stop !")
                    }
                }

                Phase.RESULT -> {
                    Text(
                        text = "Ecart : ${formatMs(state.gap)}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = feedbackFor(state.gap),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Button(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.padding(top = 32.dp),
                    ) {
                        Text("Rejouer")
                    }
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
    }
}

/** Message de feedback selon l'ecart a la cible. */
private fun feedbackFor(gap: Int): String = when {
    gap < 100 -> "Incroyable !"
    gap < 500 -> "Excellent !"
    gap < 1_000 -> "Pas mal !"
    gap < 2_500 -> "Peut mieux faire"
    else -> "Rate !"
}

/** Formate des millisecondes en secondes, ex. 15478 -> "15.48 s". */
private fun formatMs(ms: Int): String = "%.2f s".format(ms / 1000.0)

@Preview(showBackground = true)
@Composable
private fun ReactionScreenPreview() {
    MiniGamesAppTheme {
        ReactionScreen(playerName = "Joueur", onBackClick = {})
    }
}
