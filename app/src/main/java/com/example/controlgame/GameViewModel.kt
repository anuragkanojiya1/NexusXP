package com.example.controlgame

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.utils.Numeric
import java.math.BigInteger

class GameViewModel : ViewModel() {

    private val _playerScore = MutableStateFlow<BigInteger>(BigInteger.ZERO)
    val playerScore: StateFlow<BigInteger> get() = _playerScore
    var score = mutableStateOf(0) // Mutable state for score


    private val _transactionStatus = MutableStateFlow<String>("")
    val transactionStatus: StateFlow<String> get() = _transactionStatus


    private val _isItemUnlockedStatus = MutableStateFlow<Boolean>(false)
    val isItemUnlockedStatus: StateFlow<Boolean> get() = _isItemUnlockedStatus

    private var playerAddress: String = ""
    private lateinit var credentials: Credentials

    // Function to initialize the player address
    fun initializeCredentials(key: Credentials, address: String) {
        credentials = key
        playerAddress = address
    }


    // Check if item is unlocked
    fun checkIfItemUnlocked(playerAddress: String, itemId: BigInteger) {
        viewModelScope.launch {
            _isItemUnlockedStatus.value = isItemUnlocked(playerAddress, itemId)
        }
    }

    fun fetchPlayerScore() {
        viewModelScope.launch {
            _playerScore.value = withContext(Dispatchers.IO) {
                try {
                    val encodedFunction = encodeFunctionCall(
                        "playerScores",
                        listOf(Address(playerAddress)),
                        listOf(object : TypeReference<Uint256>() {})
                    )
                    Log.d("EncodedFunction", "Encoded function for getPlayerScore: $encodedFunction")

                    val response = web3j.ethCall(
                        Transaction.createEthCallTransaction(
                            playerAddress,
                            contractAddress,
                            encodedFunction
                        ),
                        DefaultBlockParameterName.LATEST
                    ).send()

                    Log.d("GetPlayerScore", "Response from contract: ${response.value}")

                    val decodedResponse = decodeFunctionResponse(
                        response.value,
                        listOf(object : TypeReference<Uint256>() {})
                    )
                    Log.d("DecodedResponse", "Decoded response: $decodedResponse")

                    (decodedResponse[0] as Uint256).value
                } catch (e: Exception) {
                    Log.e("GetPlayerScore", "Error occurred: ${e.message}")
                    e.printStackTrace()
                    BigInteger.ZERO
                }
            }
        }
    }

    fun unlockItem(itemId: BigInteger) {
        viewModelScope.launch {
            _transactionStatus.value = executeBlockchainTransaction("unlockItem", listOf(Uint256(itemId)))
        }
    }

    fun buyItem(itemId: BigInteger, price: BigInteger) {
        viewModelScope.launch {
            _transactionStatus.value = executeBlockchainTransaction("buyItem", listOf(Uint256(itemId)), price)
        }
    }

    fun addItem(name: String, unlockScore: BigInteger, price: BigInteger) {
        viewModelScope.launch {
            try {
                Log.d("AddItem", "Name: $name, UnlockScore: $unlockScore, Price: $price")
                _transactionStatus.value = executeBlockchainTransaction(
                    "addItem",
                    listOf(Utf8String(name), Uint256(unlockScore), Uint256(price))
                )
            } catch (e: Exception) {
                Log.e("AddItem Error", e.message.orEmpty())
            }
        }
    }

    fun updatePlayerScore(score: BigInteger) {
        viewModelScope.launch {
            _transactionStatus.value = executeBlockchainTransaction(
                "updateScore",
                listOf(Address(playerAddress), Uint256(score))
            )
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            _transactionStatus.value = executeBlockchainTransaction("withdraw", emptyList())
        }
    }

    private suspend fun executeBlockchainTransaction(
        functionName: String,
        parameters: List<Type<*>>,
        value: BigInteger = BigInteger.ZERO
    ): String = withContext(Dispatchers.IO) {
        try {
            Log.d("ExecuteTransaction", "Function: $functionName, Parameters: $parameters, Value: $value")
            val encodedFunction = encodeFunctionCall(functionName, parameters)
            val signedTransaction = signTransaction(encodedFunction, value)
            val sendResponse = sendRawTransaction(signedTransaction)
            Log.d("Transaction $functionName", sendResponse)
            sendResponse
        } catch (e: Exception) {
            Log.e("$functionName Error", e.message.orEmpty())
            "Error: ${e.message}"
        }
    }


    private fun signTransaction(encodedFunction: String, value: BigInteger = BigInteger.ZERO): String {
        val nonce = web3j.ethGetTransactionCount(
            playerAddress,
            DefaultBlockParameterName.LATEST
        ).send().transactionCount

        Log.d("SignTransaction", "Nonce: $nonce, GasPrice: $gasPrice, GasLimit: $gasLimit, Value: $value")

        val rawTransaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            contractAddress,
            value,
            encodedFunction
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        return Numeric.toHexString(signedMessage)
    }


    private fun sendRawTransaction(signedTransaction: String): String {
        val response = web3j.ethSendRawTransaction(signedTransaction).send()
        if (response.hasError()) {
            throw RuntimeException("Transaction failed: ${response.error.message}")
        }
        return response.transactionHash
    }

    private fun encodeFunctionCall(
        functionName: String,
        inputParameters: List<Type<*>>,
        outputParameters: List<TypeReference<*>> = emptyList()
    ): String {
        val function = org.web3j.abi.datatypes.Function(functionName, inputParameters, outputParameters)
        return FunctionEncoder.encode(function)
    }

    private fun decodeFunctionResponse(encodedResponse: String, outputParameters: List<TypeReference<Uint256>>): List<Type<*>> {
        return FunctionReturnDecoder.decode(encodedResponse, outputParameters as List<TypeReference<Type<*>?>?>?)
    }
}
