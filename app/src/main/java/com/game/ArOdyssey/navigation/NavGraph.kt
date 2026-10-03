package com.game.arodyssey.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.game.arodyssey.GameScreen
import com.game.arodyssey.GameViewModel
import com.game.arodyssey.ModelsScreen

@Composable
fun NavGraph(navController: NavController, gameViewModel: GameViewModel){

    val context = LocalContext.current
    val navHostController = rememberNavController()

    NavHost(navHostController, startDestination = Screen.GameScreen.route){
        composable(Screen.GameScreen.route){
            GameScreen(navHostController, gameViewModel)
        }
        composable(
            route = Screen.ModelsScreen.route + "/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            ModelsScreen(gameViewModel, navHostController, score, context = context)
        }
    }
}
