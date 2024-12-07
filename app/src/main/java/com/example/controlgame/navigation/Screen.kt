package com.example.controlgame.navigation

sealed class Screen(val route: String) {
    object GameScreen: Screen("game_screen")
    object CredentialsInput: Screen("credentials_input")
    object ModelsScreen: Screen("models_screen")
}