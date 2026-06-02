package com.peterschmittmatthieu.minigamesapp

import kotlinx.serialization.Serializable

/**
 * Routes de navigation. Ce sont des objets/classes Kotlin serialisables : le
 * compilateur detecte ainsi toute erreur de navigation (type-safe navigation).
 * Le pseudo du joueur est transporte jusqu'aux ecrans de jeu via les routes.
 */
@Serializable
object Home

@Serializable
data class Reaction(val playerName: String)

@Serializable
data class WordGame(val playerName: String)

@Serializable
object Leaderboard
