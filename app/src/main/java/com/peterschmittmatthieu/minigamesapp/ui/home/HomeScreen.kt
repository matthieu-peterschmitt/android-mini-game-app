package com.peterschmittmatthieu.minigamesapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peterschmittmatthieu.minigamesapp.ui.theme.MiniGamesAppTheme

/**
 * Ecran d'accueil : saisie du pseudo et acces aux deux jeux et au leaderboard.
 * Les boutons de jeu sont desactives tant que le pseudo est vide ; le pseudo
 * saisi est transmis au jeu choisi.
 */
@Composable
fun HomeScreen(
    onReactionClick: (String) -> Unit,
    onWordGameClick: (String) -> Unit,
    onLeaderboardClick: () -> Unit,
) {
    var playerName by remember { mutableStateOf("") }
    val canPlay = playerName.isNotBlank()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "MiniGames App",
                style = MaterialTheme.typography.headlineLarge,
            )

            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Pseudo") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            )

            Button(
                onClick = { onReactionClick(playerName.trim()) },
                enabled = canPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Text("Jeu de reaction")
            }
            Button(
                onClick = { onWordGameClick(playerName.trim()) },
                enabled = canPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Text("Mot cache")
            }
            OutlinedButton(
                onClick = onLeaderboardClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Text("Leaderboard")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MiniGamesAppTheme {
        HomeScreen(onReactionClick = {}, onWordGameClick = {}, onLeaderboardClick = {})
    }
}
