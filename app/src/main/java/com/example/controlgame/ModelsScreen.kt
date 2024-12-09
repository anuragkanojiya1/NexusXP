package com.example.controlgame

import PreferencesManager
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import java.math.BigInteger

//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//fun ModelsGallery() {
//    // Dummy player address and itemId (replace with actual data)
//    val playerAddress = credentials.address
//
//    LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {
//        items(Models.models) { item ->
//            var isUnlocked = false
//
//            // Check if the item is unlocked
//            CoroutineScope(Dispatchers.Main).launch {
//                isUnlocked = isItemUnlocked(playerAddress, item.id.toBigInteger())
//            }
//
//            Text(
//                text = "ID: ${item.id}",
//                modifier = Modifier
//                    .padding(8.dp)
//            )
//            Text(
//                text = item.modelPath
//            )
//            Text(
//                text = item.modelName
//            )
//            Text(
//                text = "Unlocked: ${if (isUnlocked) "Yes" else "No"}",
//                modifier = Modifier.padding(8.dp)
//            )
//        }
//    }
//}


@Preview(showBackground = true)
@Composable
fun M(){
    ModelsScreen(gameViewModel = GameViewModel(), navController = rememberNavController(), score = 100, context = LocalContext.current)
}

@Composable
fun ModelsScreen(gameViewModel: GameViewModel, navController: NavController, score: Int, context: Context) {
    val playerScore = gameViewModel.playerScore.collectAsState()
    val transactionStatus = gameViewModel.transactionStatus.collectAsState()

    val preferencesManager = remember { PreferencesManager(context) }

    var currentScore = remember { mutableStateOf(score) }

    val context = LocalContext.current

    val credentials = remember {
        Credentials.create(getPrivateKey(context))
    }

//    // List of models
//    val models = listOf(
//        ModelItem("0", "Free", "100", "Audi", "3D model", ""),
//        ModelItem("1", "10", "100", "Car", "3D model", ""),
//        ModelItem("2", "20", "200", "Robo Bun", "3D model", ""),
//        ModelItem("3", "100", "300", "Temelia", "3D model", ""),
//        ModelItem("4", "3000", "1000", "Advanced Vehicle", "3D model", ""),
//    )

    LaunchedEffect(credentials){
        gameViewModel.initializeCredentials(credentials, credentials.address.toString())
    }

    LaunchedEffect(Unit) {
            gameViewModel.updatePlayerScore(score.toBigInteger()).toString()
            gameViewModel.fetchPlayerScore()
    }

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
                    onBuyClick = { gameViewModel.buyItem(BigInteger(model.id), BigInteger(model.price)) },
                    onUnlockClick = { gameViewModel.unlockItem(BigInteger(model.id)) },
                    preferencesManager = preferencesManager
                )
            }
        }
        if (transactionStatus.value.isNotEmpty()) {
            Text(
                text = "Transaction Status: $transactionStatus",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
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

    val modelState = remember { mutableStateOf(preferencesManager.getModelState(model.id)) }

    val updateModelState: (String) -> Unit = { newState ->
        preferencesManager.saveModelState(model.id, newState)
        modelState.value = newState
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
            Text(text = "Unlock Score: "+model.price, color = Color(0xFF9DABB8), fontSize = 14.sp)
            Text(text = model.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = model.type, color = Color(0xFF9DABB8), fontSize = 14.sp)

            Row {
                Button(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = {
                        onBuyClick()
                        updateModelState("bought")
                    },
                    enabled = modelState.value == "locked" && model.price != "Free"
                ) {
                    Text("Buy")
                }
                Button(
                    modifier = Modifier.padding(start = 4.dp),
                    onClick = {
                        onUnlockClick()
                        updateModelState("unlocked")
                    },
                    enabled = modelState.value == "locked" && isUnlocked.value && model.price != "Free"
                ) {
                    Text("Unlock")
                }
            }
            if (modelState.value != "locked") {
                Text(
                    text = if (modelState.value == "bought") "Already Bought" else "Already Unlocked",
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
          //      .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

