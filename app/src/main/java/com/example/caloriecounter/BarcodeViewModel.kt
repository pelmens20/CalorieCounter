package com.example.caloriecounter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeViewModel : ViewModel() {
    var productName: String? by mutableStateOf(null)
    var caloriesFromBarcode: Float? by mutableStateOf(null)
    var errorMessage: String? by mutableStateOf(null)

    // Настройка сканера штрих-кодов для всех форматов
    private val scanner: BarcodeScanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        BarcodeScanning.getClient(options)
    }

    // Тестовая база данных продуктов (штрих-код -> продукт)
    private val productDatabase = mapOf(
        "1234567890123" to Pair("Яблочный пирог", 250f),
        "9876543210987" to Pair("Куриная грудка", 165f),
        "0123456789012" to Pair("Шоколадный батончик", 500f)
    )

    fun scanBarcode(inputImage: InputImage) {
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    val barcodeValue = barcode.rawValue
                    if (barcodeValue != null) {
                        // Проверяем, есть ли штрих-код в "базе данных"
                        val product = productDatabase[barcodeValue]
                        if (product != null) {
                            productName = product.first
                            caloriesFromBarcode = product.second
                            errorMessage = null
                        } else {
                            errorMessage = "Продукт не найден для штрих-кода: $barcodeValue"
                            productName = null
                            caloriesFromBarcode = null
                        }
                    } else {
                        errorMessage = "Не удалось распознать штрих-код"
                    }
                } else {
                    errorMessage = "Штрих-код не обнаружен"
                }
            }
            .addOnFailureListener { e ->
                errorMessage = "Ошибка сканирования: ${e.message}"
                productName = null
                caloriesFromBarcode = null
            }
    }

    fun saveCaloriesToDb(calories: Float) {
        // Заглушка для сохранения в БД (можно доработать для Room)
    }

    fun clearBarcodeData() {
        caloriesFromBarcode = null
        productName = null
        errorMessage = null
    }
}