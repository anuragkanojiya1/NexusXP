package com.game.arodyssey.navigation

sealed class Screen(val route: String) {
    object GameScreen: Screen("game_screen")
    object ModelsScreen: Screen("models_screen")
}
