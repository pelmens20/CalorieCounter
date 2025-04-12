package com.example.caloriecounter

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalorieViewModel(private val dao: CalorieDao) : ViewModel() {
    var caloriesFromBarcode by mutableStateOf<Float?>(null)
    var productName by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    private val client = OkHttpClient()
    private lateinit var activity: MainActivity

    fun initialize(activity: MainActivity) {
        this.activity = activity
    }

    fun scanBarcode(image: InputImage): Task<List<Barcode>> {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            val errorString = googleApiAvailability.getErrorString(resultCode)
            errorMessage = "Google Play Services недоступны: $errorString (код: $resultCode)"
            Log.e("CalorieViewModel", "Google Play Services unavailable: $errorString (code: $resultCode)")
            throw IllegalStateException("Google Play Services unavailable")
        }

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            throw IllegalStateException("Camera permission not granted")
        }

        Log.d("CalorieViewModel", "Starting barcode scan with image: ${image.width}x${image.height}")
        val task = scanner.process(image)
        viewModelScope.launch {
            task.addOnSuccessListener { barcodes ->
                Log.d("CalorieViewModel", "Scan success, found ${barcodes.size} barcodes")
                for (barcode in barcodes) {
                    val barcodeValue = barcode.rawValue
                    if (barcodeValue != null) {
                        Log.d("CalorieViewModel", "Barcode value: $barcodeValue")
                        fetchCaloriesFromBarcode(barcodeValue)
                    } else {
                        errorMessage = "Не удалось получить значение штрих-кода"
                        Log.e("CalorieViewModel", "Barcode rawValue is null")
                    }
                }
                scanner.close()
            }.addOnFailureListener { e: Exception ->
                errorMessage = "Ошибка сканирования: ${e.message ?: e.toString()}"
                Log.e("CalorieViewModel", "Scan failed", e)
                scanner.close()
            }
        }
        return task
    }

    private fun fetchCaloriesFromBarcode(barcode: String) {
        viewModelScope.launch {
            try {
                Log.d("CalorieViewModel", "Fetching data for barcode: $barcode")
                val request = Request.Builder()
                    .url("https://world.openfoodfacts.org/api/v0/product/$barcode.json")
                    .build()

                Log.d("CalorieViewModel", "Executing request to ${request.url}")
                // Выполняем сетевой запрос в фоновом потоке
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                Log.d("CalorieViewModel", "Response received, code: ${response.code}")
                if (!response.isSuccessful) {
                    errorMessage = "Ошибка сети: ${response.code}"
                    Log.e("CalorieViewModel", "Network error: ${response.code} - ${response.message}")
                    return@launch
                }

                val json = response.body?.string()
                if (json == null) {
                    errorMessage = "Ошибка получения данных: тело ответа пустое"
                    Log.e("CalorieViewModel", "Response body is null")
                    return@launch
                }

                Log.d("CalorieViewModel", "Response JSON: $json")
                val jsonObject = JSONObject(json)

                if (jsonObject.getString("status") == "1") {
                    val product = jsonObject.getJSONObject("product")

                    productName = if (product.has("product_name")) {
                        product.getString("product_name")
                    } else {
                        Log.w("CalorieViewModel", "Product name not found in JSON")
                        "Имя продукта недоступно"
                    }

                    if (product.has("nutriments")) {
                        val nutrients = product.getJSONObject("nutriments")
                        caloriesFromBarcode = nutrients.optDouble("energy-kcal_100g", 0.0).toFloat()
                        Log.d("CalorieViewModel", "Calories found: $caloriesFromBarcode kcal/100g")
                    } else {
                        caloriesFromBarcode = 0f
                        Log.w("CalorieViewModel", "Nutriments not found in JSON")
                    }

                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    dao.insert(CalorieData(calories = caloriesFromBarcode ?: 0f, date = date))
                    Log.d("CalorieViewModel", "Data inserted into database")
                } else {
                    errorMessage = "Продукт не найден (status: ${jsonObject.optString("status_verbose")})"
                    Log.w("CalorieViewModel", "Product not found: ${jsonObject.toString()}")
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки данных: ${e.message ?: e.toString()}"
                Log.e("CalorieViewModel", "Error fetching data for barcode $barcode", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}