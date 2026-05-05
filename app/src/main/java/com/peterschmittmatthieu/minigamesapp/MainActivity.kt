package com.peterschmittmatthieu.minigamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.peterschmittmatthieu.minigamesapp.ui.home.HomeScreen
import com.peterschmittmatthieu.minigamesapp.ui.reaction.ReactionScreen
import com.peterschmittmatthieu.minigamesapp.ui.theme.MiniGamesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniGamesAppTheme {
                MiniGamesApp()
            }
        }
    }
}

/**
 * Point d'entree de l'UI. Decide quel ecran afficher en fonction de l'etat
 * courant de l'application. La navigation est geree par un simple if/else sur
 * un etat booleen (Seance 1 — la navigation reelle arrive en Seance 2).
 */
@Composable
fun MiniGamesApp() {
    var isPlaying by remember { mutableStateOf(false) }

    if (isPlaying) {
        ReactionScreen(onBackClick = { isPlaying = false })
    } else {
        HomeScreen(onPlayClick = { isPlaying = true })
    }
}
