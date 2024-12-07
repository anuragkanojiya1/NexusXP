package com.example.controlgame

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.utils.Numeric
import java.math.BigInteger

//fun getPlayerScore(playerAddress: String): BigInteger {
//    return try {
//        val encodedFunction = encodeFunctionCall(
//            "playerScores",
//            listOf(Address(playerAddress)),
//            listOf(object : TypeReference<Uint256>() {})
//        )
//        Log.d("EncodedFunction", "Encoded function for getPlayerScore: $encodedFunction")
//
//
//        val response = web3j.ethCall(
//            Transaction.createEthCallTransaction(
//                credentials.address,
//                contractAddress,
//                encodedFunction
//            ),
//            DefaultBlockParameterName.LATEST
//        ).send()
//
//        Log.d("GetPlayerScore", "Response from contract: ${response.value}")
//
//        val decodedResponse = decodeFunctionResponse(
//            response.value,
//            listOf(object : TypeReference<Uint256>() {})
//        )
//        Log.d("DecodedResponse", "Decoded response: $decodedResponse")
//
//        (decodedResponse[0] as Uint256).value
//    } catch (e: Exception) {
//        Log.e("GetPlayerScore", "Error occurred: ${e.message}")
//
//        e.printStackTrace()
//        BigInteger.ZERO
//    }
//}
//
//suspend fun unlockItem(itemId: BigInteger): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            // Encode the function call
//            val encodedFunction = encodeFunctionCall(
//                "unlockItem",
//                listOf(Uint256(itemId))
//            )
//
//            val signedTransaction = signTransaction(encodedFunction)
//            val sendResponse = sendRawTransaction(signedTransaction)
//
//            Log.d("UnlockItem TxHash", sendResponse)
//            sendResponse
//        } catch (e: Exception) {
//            Log.e("UnlockItem Error", e.message.orEmpty())
//            "Error: ${e.message}"
//        }
//    }
//}
//
//suspend fun buyItem(itemId: BigInteger, price: BigInteger): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            // Encode the function call
//            val encodedFunction = encodeFunctionCall(
//                "buyItem",
//                listOf(Uint256(itemId))
//            )
//
//            val signedTransaction = signTransaction(
//                encodedFunction,
//                value = price // ETH to send
//            )
//            val sendResponse = sendRawTransaction(signedTransaction)
//
//            Log.d("BuyItem TxHash", sendResponse)
//            sendResponse
//        } catch (e: Exception) {
//            Log.e("BuyItem Error", e.message.orEmpty())
//            "Error: ${e.message}"
//        }
//    }
//}
//
//suspend fun withdraw(): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            // Encode the function call
//            val encodedFunction = encodeFunctionCall("withdraw", emptyList())
//
//            val signedTransaction = signTransaction(encodedFunction)
//            val sendResponse = sendRawTransaction(signedTransaction)
//
//            Log.d("Withdraw TxHash", sendResponse)
//            sendResponse
//        } catch (e: Exception) {
//            Log.e("Withdraw Error", e.message.orEmpty())
//            "Error: ${e.message}"
//        }
//    }
//}
//
//suspend fun updatePlayerScore(playerAddress: String, score: BigInteger): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            // Encode the function call
//            val encodedFunction = encodeFunctionCall(
//                "updateScore",
//                listOf(Address(playerAddress), Uint256(score))
//            )
//
//            Log.d("EncodedFunction", "Encoded function for updatePlayerScore: $encodedFunction")
//
//            // Get the nonce (transaction count for the sender's address)
//            val nonce = web3j.ethGetTransactionCount(
//                credentials.address,
//                DefaultBlockParameterName.LATEST
//            ).send().transactionCount
//
//            // Create the raw transaction
//            val rawTransaction = RawTransaction.createTransaction(
//                nonce,        // Nonce
//                gasPrice,     // Gas price
//                gasLimit,     // Gas limit
//                contractAddress, // Contract address
//                encodedFunction // Data payload
//            )
//
//            // Sign the transaction with the credentials
//            val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
//            val signedTransactionHex = Numeric.toHexString(signedMessage)
//
//            // Send the signed transaction
//            val sendResponse = web3j.ethSendRawTransaction(signedTransactionHex).send()
//
//            // Check for errors
//            if (sendResponse.hasError()) {
//                Log.e("Transaction Error", sendResponse.error.message)
//                return@withContext "Transaction Failed: ${sendResponse.error.message}"
//            }
//
//            // Log and return the transaction hash
//            Log.d("Transaction Hash", sendResponse.transactionHash)
//            sendResponse.transactionHash
//        } catch (e: Exception) {
//            e.printStackTrace()
//            "Error: ${e.message}"
//        }
//    }
//}

suspend fun isItemUnlocked(playerAddress: String, itemId: BigInteger): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val encodedFunction = encodeFunctionCall(
                "playerItems",
                listOf(Address(playerAddress), Uint256(itemId)),
                listOf(object : TypeReference<org.web3j.abi.datatypes.Bool>() {})
            )

            val response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                    playerAddress,
                    contractAddress,
                    encodedFunction
                ),
                DefaultBlockParameterName.LATEST
            ).send()

            Log.d("Item unlocked:",response.value)
            val decodedResponse = decodeFunctionResponseGetItem(
                response.value,
                listOf(object : TypeReference<Bool>() {})
            )

            (decodedResponse[0] as org.web3j.abi.datatypes.Bool).value
        } catch (e: Exception) {
            Log.e("IsItemUnlocked Error", e.message.orEmpty())
            false
        }
    }
}



//suspend fun addItem(name: String, unlockScore: BigInteger, price: BigInteger): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            // Encode the function call
//            val encodedFunction = encodeFunctionCall(
//                "addItem",
//                listOf(
//                    Utf8String(name),
//                    Uint256(unlockScore),
//                    Uint256(price)
//                )
//            )
//
//            val signedTransaction = signTransaction(encodedFunction)
//            val sendResponse = sendRawTransaction(signedTransaction)
//
//            Log.d("AddItem TxHash", sendResponse)
//            sendResponse
//        } catch (e: Exception) {
//            Log.e("AddItem Error", e.message.orEmpty())
//            "Error: ${e.message}"
//        }
//    }
//}
//
//
//fun signTransaction(
//    encodedFunction: String,
//    value: BigInteger = BigInteger.ZERO
//): String {
//    val nonce = web3j.ethGetTransactionCount(
//        credentials.address,
//        DefaultBlockParameterName.LATEST
//    ).send().transactionCount
//
//    val rawTransaction = RawTransaction.createTransaction(
//        nonce,
//        gasPrice,
//        gasLimit,
//        contractAddress,
//        value,
//        encodedFunction
//    )
//
//    val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
//    return Numeric.toHexString(signedMessage)
//}
//
//fun sendRawTransaction(signedTransaction: String): String {
//    val response = web3j.ethSendRawTransaction(signedTransaction).send()
//    if (response.hasError()) {
//        throw RuntimeException("Transaction failed: ${response.error.message}")
//    }
//    return response.transactionHash
//}
//
fun encodeFunctionCall(functionName: String, inputParameters: List<Type<*>>, outputParameters: List<TypeReference<*>> = emptyList()): String {
    val function = org.web3j.abi.datatypes.Function(functionName, inputParameters, outputParameters)
    return FunctionEncoder.encode(function)
}

fun decodeFunctionResponse(encodedResponse: String, outputParameters: List<TypeReference<Uint256>>): List<Type<*>> {
    return FunctionReturnDecoder.decode(encodedResponse,
        outputParameters as List<TypeReference<Type<*>?>?>?
    )
}
fun decodeFunctionResponseGetItem(encodedResponse: String, outputParameters: List<TypeReference<Bool>>): List<Type<*>> {
    return FunctionReturnDecoder.decode(encodedResponse,
        outputParameters as List<TypeReference<Type<*>?>?>?
    )
}
