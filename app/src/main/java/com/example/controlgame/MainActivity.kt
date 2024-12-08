package com.example.controlgame

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.controlgame.navigation.NavGraph
import com.example.controlgame.ui.theme.ControlGameTheme
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.delay
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger
import kotlin.math.pow
import kotlin.random.Random

//private const val kModelFile = "models/damaged_helmet.glb"
//private const val giftBox = "models/gift_box.glb"

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavController
    private val gameViewModel: GameViewModel by viewModels()

    val web3j: Web3j =
        Web3j.build(HttpService("https://mantle-sepolia.infura.io/v3/8c09f9f92fc4438fbbce47b4a3eb2b3d")) // Replace with Infura/Alchemy node URL

    // Gas provider for transactions
    val gasPrice = BigInteger.valueOf(201_600_000_000L) // 201.6 Gwei
    val gasLimit = BigInteger.valueOf(500_000) // Gas limit

    val gasProvider = StaticGasProvider(gasPrice, gasLimit)
    val contractAddress = "0x855ca462005f7DacC1E5c9ea29D43A2f84B58bda"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ControlGameTheme {
                val navController = rememberNavController()


                // Mutable state for key check, initially null
                var isKeySaved by remember { mutableStateOf<Boolean?>(null) }

                // Check if the private key is saved
                LaunchedEffect(Unit) {
                    isKeySaved = isPrivateKeySaved(this@MainActivity)
                }

                // Display screen based on key check result
                when (isKeySaved) {
                    null -> {
                        // Show a loading screen while checking the key
                        SplashScreen()
                    }
                    true -> {
                        // Navigate to GameScreen if key is saved
                        NavGraph(navController, gameViewModel)
                    }
                    false -> {
                        // Show CredentialsInput if key is not saved
                        CredentialsInput(
                            navController,
                            gameViewModel,
                            onKeySaved = { isKeySaved = true }
                        )
                    }
                }
            }
        }
    }
}