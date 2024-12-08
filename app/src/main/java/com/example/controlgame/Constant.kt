package com.example.controlgame

import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger

val web3j: Web3j = Web3j.build(HttpService("https://mantle-sepolia.infura.io/v3/8c09f9f92fc4438fbbce47b4a3eb2b3d")) // Replace with Infura/Alchemy node URL

val gasPrice = BigInteger.valueOf(22_000_000L)
val gasLimit = BigInteger.valueOf(400_000_000L)


val gasProvider = StaticGasProvider(gasPrice, gasLimit)
val contractAddress = "0x855ca462005f7DacC1E5c9ea29D43A2f84B58bda"
const val kModelFile = "models/damaged_helmet.glb"
const val giftBox = "models/gift_box.glb"
