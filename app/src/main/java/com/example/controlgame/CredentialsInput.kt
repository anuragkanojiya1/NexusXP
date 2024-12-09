package com.example.controlgame

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.crypto.TransactionEncoder
import java.math.BigInteger

@Composable
fun Enterprivatekey(){

    val privateKey = remember { mutableStateOf("") }
    val context = LocalContext.current

    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    LaunchedEffect(Unit) {
        val storedKey = encryptedSharedPreferences.getString("privateKey", "")
        privateKey.value = storedKey ?: ""
    }


    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch{
           GameViewModel().addItem(models.get(0).modelPath, BigInteger.valueOf(100), BigInteger.valueOf(20))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = privateKey.value,
            onValueChange = { privateKey.value = it }
        )
    }
}

@Composable
fun CredentialsInput(
    navController: NavController,
    gameViewModel: GameViewModel,
    onKeySaved: () -> Unit
) {
    var privateKey = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val models = listOf(
        ModelItem("0", "10", "100", "Audi", "3D model", R.drawable.audi, "models/audi.glb"),
        ModelItem("1", "10", "100", "Car", "3D model", R.drawable.audi, "models/car1.glb"),
        ModelItem("2", "20", "200", "BMW", "3D model", R.drawable.audi, "models/bmw.glb"),
        ModelItem("3", "100", "300", "Temelia", "3D model", R.drawable.audi, "models/telimia__original_work.glb"),
        ModelItem("4", "3000", "1000", "Advanced Vehicle", "3D model", R.drawable.audi, "models/advanced_vehicle.glb"),
    )

//    LaunchedEffect(Unit) {
////                gameViewModel.addItem(models.get(0).name, BigInteger.valueOf(100), BigInteger.ZERO)
////            delay(100)
////                gameViewModel.addItem(models.get(1).name, BigInteger.valueOf(10), BigInteger.valueOf(100))
////            delay(100)
//            gameViewModel.addItem(models.get(2).name, BigInteger.valueOf(20), BigInteger.valueOf(200))
//            delay(100)
//            gameViewModel.addItem(models.get(3).name, BigInteger.valueOf(300), BigInteger.valueOf(1000))
//            delay(100)
//            gameViewModel.addItem(models.get(4).name, BigInteger.valueOf(1000), BigInteger.valueOf(3000))
//    }

    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    LaunchedEffect(Unit) {
        val storedKey = encryptedSharedPreferences.getString("privateKey", "")
        privateKey.value = storedKey ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }


        Text(
            text = "Enter your private key",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = "Your private key is a secret code that gives you access to your wallet. It should never be shared.",
            color = Color(0xFF9DABB8),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            BasicTextField(
                value = privateKey.value,
                onValueChange = { privateKey.value = it },
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        color = Color(0xFF1C2126),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            )

            if (privateKey.value.isEmpty()) {
                Text(
                    text = "Enter your private key",
                    color = Color(0xFF9DABB8),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        errorMessage.value?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    if (privateKey.value.length == 66) {
                        encryptedSharedPreferences.edit().putString("privateKey", privateKey.value).apply()
                        errorMessage.value = null
                        onKeySaved()

                    } else {
                        errorMessage.value = "Private key must be 66 characters long, including '0x' prefix."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1980E6)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun x(){
    CredentialsInput(navController = rememberNavController(), gameViewModel = GameViewModel()) {  }
}

fun isPrivateKeySaved(context: Context): Boolean {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    return !encryptedSharedPreferences.getString("privateKey", "").isNullOrEmpty()
}

fun getPrivateKey(context: Context): String? {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    return encryptedSharedPreferences.getString("privateKey", null)
}
