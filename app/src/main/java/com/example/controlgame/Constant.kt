package com.example.controlgame

import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger

val web3j: Web3j = Web3j.build(HttpService("https://sepolia.infura.io/v3/8c09f9f92fc4438fbbce47b4a3eb2b3d")) // Replace with Infura/Alchemy node URL

val gasPrice = BigInteger.valueOf(20_000_000_000L) // 20 Gwei
val gasLimit = BigInteger.valueOf(500_000)         // Gas limit
val gasProvider = StaticGasProvider(gasPrice, gasLimit)
val contractAddress = "0x94be16588154B0F2b80e5ff6F25032D5978f3C69"
const val kModelFile = "models/damaged_helmet.glb"
const val giftBox = "models/gift_box.glb"