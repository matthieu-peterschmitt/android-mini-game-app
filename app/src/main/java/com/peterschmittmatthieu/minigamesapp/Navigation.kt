package com.peterschmittmatthieu.minigamesapp

import kotlinx.serialization.Serializable

/**
 * Routes de navigation. Ce sont des objets Kotlin serialisables : le
 * compilateur detecte ainsi toute erreur de navigation (type-safe navigation).
 */
@Serializable
object Home

@Serializable
object Reaction

@Serializable
object WordGame
