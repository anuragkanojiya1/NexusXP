package com.game.arodyssey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.game.arodyssey.navigation.NavGraph
import com.game.arodyssey.ui.theme.ArOdysseyTheme

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArOdysseyTheme {
                val navController = rememberNavController()
                NavGraph(navController, gameViewModel)
            }
        }
    }
}
