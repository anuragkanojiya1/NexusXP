import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ModelPreferences", Context.MODE_PRIVATE)

    fun saveModelState(modelId: String, state: String) {
        sharedPreferences.edit().putString(modelId, state).apply()
    }

    fun getModelState(modelId: String): String {
        return sharedPreferences.getString(modelId, "locked") ?: "locked"
    }

    // Function to fetch all models' states
    fun fetchAllModelsState(preferencesManager: PreferencesManager): Map<String, String> {
        // Assuming you have a list of model IDs or use a static list
        val modelIds = listOf("0", "1", "2", "3", "4")  // Example model IDs
        return modelIds.associateWith { preferencesManager.getModelState(it) }
    }
}
