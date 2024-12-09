package com.example.controlgame

import PreferencesManager
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.controlgame.MainActivity
import com.example.controlgame.navigation.Screen
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
import io.github.sceneview.ar.scene.PlaneRenderer
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import java.math.BigInteger
import kotlin.math.pow
import kotlin.random.Random

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GameScreen(navController: NavController, gameViewModel: GameViewModel){

    Box(modifier = Modifier.fillMaxSize()) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val childNodes = rememberNodes()

        var trackingFailureReason by remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }

        var frame by remember { mutableStateOf<Frame?>(null) }
        var modelNode by remember { mutableStateOf<ModelNode?>(null) }
        var modelNode2 by remember { mutableStateOf<ModelNode?>(null) }

        val context = LocalContext.current
        var score by remember { mutableStateOf(0) } // Initialize score
        var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

        DisposableEffect(context) {
            mediaPlayer = MediaPlayer.create(context, R.raw.ufo_sound)

            onDispose {
                mediaPlayer?.release()
            }
        }

        val credentials = remember {
            Credentials.create(getPrivateKey(context))
        }

        LaunchedEffect(credentials) {
            gameViewModel.initializeCredentials(credentials, credentials.address.toString())
        }

        CoroutineScope(Dispatchers.IO).launch {
            val result = isItemUnlocked(credentials.address, BigInteger.valueOf(3))
            Log.d("Check:", result.toString())
        }

        val preferencesManager = remember { PreferencesManager(context) }

        val unlockedOrBoughtModels = models.filter {
            val modelState = preferencesManager.getModelState(it.id)
            modelState == "bought" || modelState == "unlocked"
        }

        Log.d("Models", unlockedOrBoughtModels.toString())

        LaunchedEffect(Unit) {
            gameViewModel.addItem(models.get(0).name, BigInteger.valueOf(100), BigInteger.ZERO)
            delay(2000)
            gameViewModel.addItem(
                models.get(1).name,
                BigInteger.valueOf(10),
                BigInteger.valueOf(100)
            )
            delay(2000)
            gameViewModel.addItem(
                models.get(2).name,
                BigInteger.valueOf(20),
                BigInteger.valueOf(200)
            )
            delay(2000)
            gameViewModel.addItem(
                models.get(3).name,
                BigInteger.valueOf(300),
                BigInteger.valueOf(1000)
            )
            delay(2000)
            gameViewModel.addItem(
                models.get(4).name,
                BigInteger.valueOf(1000),
                BigInteger.valueOf(3000)
            )
        }

        var currentIndex by remember { mutableStateOf(-1) }



        Log.d("index", currentIndex.toString())

        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            modelLoader = modelLoader,
            sessionConfiguration = { session, config ->
                config.depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        Config.DepthMode.AUTOMATIC
                    } else {
                        Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode =
                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            planeRenderer = false,
            onSessionUpdated = { _, updatedFrame ->
                frame = updatedFrame

                if (childNodes.isEmpty()) {

                    updatedFrame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            val giftBoxNode = AnchorNode(engine, anchor).apply {
                                modelNode2 = ModelNode(
                                    modelInstance = modelLoader.createModelInstance(giftBox),
                                    scaleToUnits = 0.3f
                                ).apply {
                                    isScaleEditable = false
                                    isPositionEditable = false
                                    isEditable = false
                                    isTouchable = false
                                    name = "giftbox"
                                }
                                modelNode2?.position = Float3(
                                    Random.nextFloat(), 0f,
                                    Random.nextFloat()
                                )

                                addChildNode(modelNode2!!)
                            }
                            childNodes += giftBoxNode
                            childNodes.indexOf(giftBoxNode)
                            Log.d("gift Node:", childNodes.indexOf(giftBoxNode).toString())
                        }

                    updatedFrame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            val helmetNode = AnchorNode(engine, anchor).apply {
                                modelNode = ModelNode(
                                    modelInstance = modelLoader.createModelInstance(kModelFile),
                                    scaleToUnits = 0.5f
                                ).apply {
                                    isRotationEditable = true
                                    isEditable = false
                                    name = "helmet"
                                }
                                addChildNode(modelNode!!)
                            }
                            childNodes += helmetNode
                        }
                }
            }
        )

        Log.d("Childnode 1", modelNode2?.name.toString())
        Log.d("Childnode 2", modelNode?.name.toString())
        Log.d("Childnode 2 pos", modelNode?.position.toString())
        Log.d("Childnode 1 pos", modelNode2?.position.toString())

        LaunchedEffect(childNodes) {
            while (true) {

                val giftBoxPosition = modelNode2?.position
                val helmetPosition = modelNode?.position

                Log.d("Position Debug", "GiftBox Position: $giftBoxPosition")
                Log.d("Position Debug", "Helmet Position: $helmetPosition")

                if (giftBoxPosition != null && helmetPosition != null) {
                    val distance = calculateDistance(giftBoxPosition, helmetPosition)
                    Log.d("Distance", "Distance: $distance")

                    if (distance < 0.5f) {
                        Log.d("Collision", "Collision detected! Removing gift box.")
                        modelNode2!!.destroy()
                        modelNode2 = null
                        score++

                        mediaPlayer?.start()

                        delay(1000L)
                    }
                }

                if (modelNode2 == null) {
                    Log.d("GiftBoxSpawner", "No gift box found, attempting to spawn a new one.")

                    frame?.getUpdatedPlanes()
                        ?.firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            val newGiftBoxNode = AnchorNode(engine, anchor).apply {
                                modelNode2 = ModelNode(
                                    modelInstance = modelLoader.createModelInstance(giftBox),
                                    scaleToUnits = 0.3f
                                ).apply {
                                    name = "giftbox"
                                    position = Float3(
                                        Random.nextFloat() * 4 - 2,
                                        0f,
                                        Random.nextFloat() * 4 - 2
                                    )
//                                                position = Float3(
//                                                    Random.nextFloat(),
//                                                    0f,
//                                                    Random.nextFloat()
//                                                )

                                    Log.d("GiftBoxSpawner", "New gift box position: $position")

                                }
                                addChildNode(modelNode2!!)
                            }
                            childNodes += newGiftBoxNode
                            Log.d("GiftBoxSpawner", "New gift box successfully added to the scene.")
                        } ?: Log.d(
                        "GiftBoxSpawner",
                        "No suitable plane found for spawning a new gift box."
                    )
                }

                delay(100L)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = trackingFailureReason?.let {
                    it.getDescription(context)
                } ?: stringResource(R.string.app_name),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White
            )

            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = {
                    navController.navigate(Screen.ModelsScreen.route + "/$score")
                }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "List",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Score: $score",
                    textAlign = TextAlign.End,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        var view by remember { mutableStateOf(false) }

        if (unlockedOrBoughtModels.isNotEmpty()) {
            OutlinedButton(
                onClick = {
                    view = !view
                },
                modifier = Modifier.padding(16.dp)
                    .align(Alignment.TopStart),
            ) {
                Text(if (view) "Hide" else "View", color = Color.White)
            }

            if (view) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.BottomStart
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        unlockedOrBoughtModels.forEachIndexed { index, model ->
                            Box(
                                contentAlignment = Alignment.BottomCenter,
                                modifier = Modifier
                                    .size(76.dp)
                                    .padding(top = 8.dp, bottom = 4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(
                                        2.dp,
                                        if (index == currentIndex) Color.Blue else Color.Gray,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            currentIndex = index

                                            modelNode?.destroy()
                                            frame?.getUpdatedPlanes()
                                                ?.firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                                                ?.let { it.createAnchorOrNull(it.centerPose) }
                                                ?.let { anchor ->
                                                    val newModelNode = AnchorNode(engine, anchor).apply {
                                                        modelNode = ModelNode(
                                                            modelInstance = modelLoader.createModelInstance(
                                                                model.modelPath
                                                            ),
                                                            scaleToUnits = 0.6f
                                                        ).apply {
                                                            isRotationEditable = true
                                                            isEditable = false
                                                            name = "helmet"
                                                            Log.d("Model", model.modelPath)
                                                        }
                                                        addChildNode(modelNode!!)
                                                    }
                                                    childNodes += newModelNode
                                                }
                                        })
                                    }
                            ) {
                                Text(model.name, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }


        // Buttons for continuous movement
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                ContinuousMovementButtons(
                    onMoveLeft = {
                        modelNode?.position = modelNode?.position?.let { currentPosition ->
                            currentPosition + Float3(-0.04f, 0f, 0f) // Move left
                        }!!
                    },
                    onMoveRight = {
                        modelNode?.position = modelNode?.position?.let { currentPosition ->
                            currentPosition + Float3(0.04f, 0f, 0f) // Move right
                        }!!
                    },
                    onMoveForward = {
                        modelNode?.position = modelNode?.position?.let { currentPosition ->
                            currentPosition + Float3(0f, 0f, -0.04f) // Move forward
                        }!!
                    },
                    onMoveBackward = {
                        modelNode?.position = modelNode?.position?.let { currentPosition ->
                            currentPosition + Float3(0f, 0f, 0.04f) // Move backward
                        }!!
                    }
                )
            }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview(){
    GameScreen(navController = rememberNavController(), GameViewModel())
}

@Composable
fun ContinuousMovementButtons(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveForward: () -> Unit,
    onMoveBackward: () -> Unit
) {
    val buttonModifier = Modifier
        .padding(8.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MovementButton(
            text = "↑",
            modifier = buttonModifier,
            onPress = onMoveForward
        )
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            MovementButton(
                text = "←",
                modifier = buttonModifier,
                onPress = onMoveLeft
            )
            MovementButton(
                text = "→",
                modifier = buttonModifier,
                onPress = onMoveRight
            )
        }
        MovementButton(
            text = "↓",
            modifier = buttonModifier,
            onPress = onMoveBackward
        )
    }
}

@Composable
fun MovementButton(
    text: String,
    modifier: Modifier = Modifier,
    onPress: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (isPressed) {
                onPress()
                delay(100L) // Adjust the delay for movement speed
            }
        }
    }

    Box(
        modifier = modifier
            .border(2.dp, Color.Green, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease() // Wait for release
                        isPressed = false
                    }
                )
            }
            .padding(8.dp)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold)
    }
}

fun calculateDistance(pos1: Float3, pos2: Float3): Float {
    return kotlin.math.sqrt(
        (pos1.x - pos2.x).pow(2) +
                (pos1.y - pos2.y).pow(2) +
                (pos1.z - pos2.z).pow(2)
    )
}