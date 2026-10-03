package com.game.arodyssey

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.game.arodyssey.navigation.Screen
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GameScreen(navController: NavController, gameViewModel: GameViewModel) {

    Box(modifier = Modifier.fillMaxSize()) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)

        var trackingFailureReason by remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }

        var frame by remember { mutableStateOf<Frame?>(null) }
        var helmetAnchor by remember { mutableStateOf<Anchor?>(null) }
        var giftBoxAnchor by remember { mutableStateOf<Anchor?>(null) }

        var helmetPosition by remember { mutableStateOf(Float3(0f, 0f, 0f)) }
        var giftBoxPosition by remember {
            mutableStateOf(getRandomGiftBoxPosition(Float3(0f, 0f, 0f)))
        }

        val context = LocalContext.current
        var score by remember { mutableStateOf(0) }
        var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

        DisposableEffect(context) {
            mediaPlayer = MediaPlayer.create(context, R.raw.ufo_sound)
            onDispose {
                mediaPlayer?.release()
            }
        }

        val preferencesManager = remember { PreferencesManager(context) }
        val allModelStates by preferencesManager.allModelsStateFlow.collectAsState(initial = emptyMap())

        LaunchedEffect(Unit) {
            preferencesManager.fetchAllModelsState()
        }

        val unlockedOrBoughtModels = models.filter {
            val modelState = allModelStates[it.id] ?: "locked"
            modelState == "bought" || modelState == "unlocked"
        }

        var currentIndex by remember { mutableStateOf(-1) }

        ARSceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            sessionConfiguration = { session, config ->
                config.depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        Config.DepthMode.AUTOMATIC
                    } else {
                        Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            planeRenderer = false,
            onSessionUpdated = { _, updatedFrame ->
                frame = updatedFrame

                if (helmetAnchor == null) {
                    updatedFrame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            helmetAnchor = anchor
                        }
                }

                if (giftBoxAnchor == null) {
                    updatedFrame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            giftBoxAnchor = anchor
                        }
                }
            },
            onTrackingFailureChanged = { reason ->
                trackingFailureReason = reason
            }
        ) {
            helmetAnchor?.let { anchor ->
                AnchorNode(anchor = anchor) {
                    val modelPath = if (currentIndex in unlockedOrBoughtModels.indices) {
                        unlockedOrBoughtModels[currentIndex].modelPath
                    } else {
                        kModelFile
                    }
                    rememberModelInstance(modelLoader, modelPath)?.let { instance ->
                        ModelNode(
                            modelInstance = instance,
                            scaleToUnits = 0.5f,
                            position = helmetPosition,
                            isEditable = true
                        )
                    }
                }
            }

            giftBoxAnchor?.let { anchor ->
                AnchorNode(anchor = anchor) {
                    rememberModelInstance(modelLoader, giftBox)?.let { instance ->
                        ModelNode(
                            modelInstance = instance,
                            scaleToUnits = 0.3f,
                            position = giftBoxPosition
                        )
                    }
                }
            }
        }

        // Collision detection and gift box respawn loop
        LaunchedEffect(Unit) {
            while (true) {
                val distance = calculateDistance(giftBoxPosition, helmetPosition)
                if (distance < 0.5f) {
                    score++
                    gameViewModel.saveScore(score)
                    mediaPlayer?.start()
                    giftBoxPosition = getRandomGiftBoxPosition(helmetPosition)
                    delay(1000L)
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

            val highScore by gameViewModel.highScore.collectAsState()

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
                Text(
                    text = "High Score: $highScore",
                    textAlign = TextAlign.End,
                    fontSize = 14.sp,
                    color = Color.Yellow
                )
            }
        }

        var view by remember { mutableStateOf(false) }

        if (unlockedOrBoughtModels.isNotEmpty()) {
            OutlinedButton(
                onClick = { view = !view },
                modifier = Modifier
                    .padding(16.dp)
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
                                        })
                                    }
                            ) {
                                Text(
                                    model.name,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
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
                    helmetPosition += Float3(-0.04f, 0f, 0f)
                },
                onMoveRight = {
                    helmetPosition += Float3(0.04f, 0f, 0f)
                },
                onMoveForward = {
                    helmetPosition += Float3(0f, 0f, -0.04f)
                },
                onMoveBackward = {
                    helmetPosition += Float3(0f, 0f, 0.04f)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    val context = LocalContext.current
    GameScreen(navController = rememberNavController(), GameViewModel(context.applicationContext as android.app.Application))
}

@Composable
fun ContinuousMovementButtons(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveForward: () -> Unit,
    onMoveBackward: () -> Unit
) {
    val buttonModifier = Modifier.padding(8.dp)

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

    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (isPressed) {
                onPress()
                delay(100L)
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
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .padding(8.dp)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

fun getRandomGiftBoxPosition(currentHelmetPos: Float3): Float3 {
    val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
    val distance = 1.5f + Random.nextFloat() * 2.0f // 1.5m to 3.5m away
    val x = currentHelmetPos.x + cos(angle) * distance
    val z = currentHelmetPos.z + sin(angle) * distance
    return Float3(x, 0f, z)
}

fun calculateDistance(pos1: Float3, pos2: Float3): Float {
    return kotlin.math.sqrt(
        (pos1.x - pos2.x).pow(2) +
                (pos1.y - pos2.y).pow(2) +
                (pos1.z - pos2.z).pow(2)
    )
}
