package com.game.arodyssey

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.math.BigInteger

@Preview(showBackground = true)
@Composable
fun M(){
    val context = LocalContext.current
    ModelsScreen(gameViewModel = GameViewModel(context.applicationContext as android.app.Application), navController = rememberNavController(), score = 100, context = context)
}

@Composable
fun ModelsScreen(gameViewModel: GameViewModel, navController: NavController, score: Int, context: Context) {
    val preferencesManager = remember { PreferencesManager(context) }
    val currentScore = remember { mutableStateOf(score) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
    ) {
        TopBar(navController)
        Text(
            text = "New models",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(models) { model ->
                ModelCard(
                    model = model,
                    playerScore = BigInteger.valueOf(currentScore.value.toLong()),
                    onBuyClick = { },
                    onUnlockClick = { },
                    preferencesManager = preferencesManager
                )
            }
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111418))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
                .clickable(onClick = {
                    navController.navigateUp()
                })
        )
        Text(
            text = "3D models",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ModelCard(
    model: ModelItem,
    playerScore: BigInteger,
    onBuyClick: () -> Unit,
    onUnlockClick: () -> Unit,
    preferencesManager: PreferencesManager
) {
    val coroutineScope = rememberCoroutineScope()
    val modelStateFlow = remember(model.id) { preferencesManager.getModelStateFlow(model.id) }
    val modelState by modelStateFlow.collectAsState(initial = "locked")

    val updateModelState: (String) -> Unit = { newState ->
        coroutineScope.launch {
            preferencesManager.saveModelState(model.id, newState)
        }
    }

    var isUnlocked = remember { mutableStateOf(false) }

    LaunchedEffect(model.id) {
        isUnlocked.value = playerScore >= BigInteger(model.unlockScore)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clip(RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(2f)
        ) {
            Text(text = "Price: "+model.price, color = Color(0xFF9DABB8), fontSize = 14.sp)
            Text(text = "Unlock Score: "+model.unlockScore, color = Color(0xFF9DABB8), fontSize = 14.sp)
            Text(text = model.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = model.type, color = Color(0xFF9DABB8), fontSize = 14.sp)

            Row {
                Button(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = {
                        onBuyClick()
                        updateModelState("bought")
                    },
                    enabled = modelState == "locked" && model.price != "Free"
                ) {
                    Text("Buy")
                }
                Button(
                    modifier = Modifier.padding(start = 4.dp),
                    onClick = {
                        onUnlockClick()
                        updateModelState("unlocked")
                    },
                    enabled = modelState == "locked" && isUnlocked.value && model.price != "Free"
                ) {
                    Text("Unlock")
                }
            }
            if (modelState != "locked") {
                Text(
                    text = if (modelState == "bought") "Already Bought" else "Already Unlocked",
                    color = Color.Green,
                    fontSize = 12.sp
                )
            }
        }

        Image(
            painter = painterResource(model.imageUrl),
            contentDescription = model.name,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
