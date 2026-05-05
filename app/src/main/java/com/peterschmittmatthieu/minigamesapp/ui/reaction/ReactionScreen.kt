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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peterschmittmatthieu.minigamesapp.ui.theme.MiniGamesAppTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

/** Les trois phases du jeu de reaction. */
private enum class Phase { READY, PLAYING, RESULT }

/**
 * Jeu de reaction : un timer defile a l'ecran et le joueur doit l'arreter au
 * plus pres d'une valeur cible. Chaque partie est generee aleatoirement
 * (valeur de depart, vitesse, sens).
 */
@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    // Etat de la partie. Initialise aleatoirement des la premiere composition,
    // puis regenere via newRound() a chaque "Rejouer".
    var phase by remember { mutableStateOf(Phase.READY) }
    var isRunning by remember { mutableStateOf(false) }
    var target by remember { mutableIntStateOf(randomTarget()) }
    var step by remember { mutableIntStateOf(randomStep()) }
    var currentMs by remember { mutableIntStateOf(randomStart()) }

    fun newRound() {
        target = randomTarget()
        step = randomStep()
        currentMs = randomStart()
        isRunning = false
        phase = Phase.READY
    }

    // Timer : se relance a chaque changement de isRunning. La coroutine est
    // automatiquement annulee si le composable quitte la composition.
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(10L)
            currentMs += step
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Cible",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatMs(target),
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                text = formatMs(currentMs),
                fontFamily = FontFamily.Monospace,
                fontSize = 56.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 48.dp),
            )

            when (phase) {
                Phase.READY -> {
                    Button(
                        onClick = {
                            isRunning = true
                            phase = Phase.PLAYING
                        },
                    ) {
                        Text("Demarrer")
                    }
                }

                Phase.PLAYING -> {
                    Button(
                        onClick = {
                            isRunning = false
                            phase = Phase.RESULT
                        },
                    ) {
                        Text("Stop !")
                    }
                }

                Phase.RESULT -> {
                    val gap = abs(currentMs - target)
                    Text(
                        text = "Ecart : ${formatMs(gap)}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = feedbackFor(gap),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Button(
                        onClick = { newRound() },
                        modifier = Modifier.padding(top = 32.dp),
                    ) {
                        Text("Rejouer")
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
    }
}

/** Cible a atteindre, entre 2 s et 18 s. */
private fun randomTarget(): Int = Random.nextInt(2_000, 18_000)

/** Valeur de depart du timer : ne commence pas forcement a zero. */
private fun randomStart(): Int = Random.nextInt(0, 20_000)

/** Pas applique toutes les 10 ms : amplitude (vitesse) et signe (sens) aleatoires. */
private fun randomStep(): Int {
    val magnitude = Random.nextInt(5, 21)
    val direction = if (Random.nextBoolean()) 1 else -1
    return magnitude * direction
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
        ReactionScreen(onBackClick = {})
    }
}
