package com.peterschmittmatthieu.minigamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peterschmittmatthieu.minigamesapp.ui.home.HomeScreen
import com.peterschmittmatthieu.minigamesapp.ui.reaction.ReactionScreen
import com.peterschmittmatthieu.minigamesapp.ui.theme.MiniGamesAppTheme
import com.peterschmittmatthieu.minigamesapp.ui.wordgame.WordGameScreen

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
 * Point d'entree de l'UI. Gere la pile d'ecrans via un NavHost et un
 * NavController cree une seule fois au sommet de l'arborescence.
 */
@Composable
fun MiniGamesApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                onReactionClick = { navController.navigate(Reaction) },
                onWordGameClick = { navController.navigate(WordGame) },
            )
        }
        composable<Reaction> {
            ReactionScreen(onBackClick = { navController.popBackStack() })
        }
        composable<WordGame> {
            WordGameScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
