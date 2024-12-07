package com.example.controlgame.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.controlgame.CredentialsInput
import com.example.controlgame.GameScreen
import com.example.controlgame.GameViewModel
import com.example.controlgame.ModelsScreen

@Composable
fun NavGraph(navController: NavController, gameViewModel: GameViewModel){

    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.GameScreen.route){

        composable(Screen.CredentialsInput.route){
            CredentialsInput(navController, gameViewModel, onKeySaved = {
                navController.navigate(Screen.GameScreen.route)
            })
        }
        composable(Screen.GameScreen.route){
            GameScreen(navController, gameViewModel)
        }
        composable(Screen.ModelsScreen.route+"/{score}",
            arguments = listOf(navArgument("score"){
                type = NavType.IntType
            })){backStackEntry ->

            val score = backStackEntry.arguments?.getInt("score") ?: 0
            ModelsScreen(gameViewModel, navController, score, context = context)
        }
    }
}