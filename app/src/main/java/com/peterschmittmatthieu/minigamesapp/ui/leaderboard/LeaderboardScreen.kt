package com.peterschmittmatthieu.minigamesapp.ui.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.peterschmittmatthieu.minigamesapp.data.Score
import com.peterschmittmatthieu.minigamesapp.ui.leaderboard.LeaderboardViewModel.GameFilter

/**
 * Ecran leaderboard : meilleurs scores avec filtre par jeu, statistiques
 * personnelles du joueur courant et reinitialisation des scores (bonus TP3).
 */
@Composable
fun LeaderboardScreen(
    playerName: String,
    onBackClick: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel(),
) {
    LaunchedEffect(playerName) { viewModel.setPlayer(playerName) }
    val scores by viewModel.scores.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val stats by viewModel.stats.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        ) {
            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            // Bonus : statistiques personnelles du joueur courant.
            stats?.let {
                Text(
                    text = "$playerName : ${it.games} partie(s), moyenne ${it.averageScore}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            // Bonus : filtre par jeu.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GameFilter.entries.forEach { option ->
                    FilterChip(
                        selected = filter == option,
                        onClick = { viewModel.setFilter(option) },
                        label = { Text(option.label) },
                    )
                }
            }

            if (scores.isEmpty()) {
                Text(
                    text = "Aucun score pour l'instant. A vous de jouer !",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(scores) { index, score ->
                        ScoreRow(rank = index + 1, score = score)
                        HorizontalDivider()
                    }
                }
            }

            // Bonus : reinitialisation des scores.
            TextButton(
                onClick = { viewModel.clearScores() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text("Reinitialiser les scores", color = MaterialTheme.colorScheme.error)
            }

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                Text("Accueil")
            }
        }
    }
}

@Composable
private fun ScoreRow(rank: Int, score: Score) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "$rank",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = score.playerName,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = score.gameName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "${score.score}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
