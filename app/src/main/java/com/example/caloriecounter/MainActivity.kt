package com.example.caloriecounter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorieCounterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun CalorieCounterTheme(content: @Composable () -> Unit) {
    val lightColors = lightColorScheme(
        primary = Color(0xFF2E7D32), // forest_green
        onPrimary = Color(0xFFF5F5F5), // ivory_white
        secondary = Color(0xFFFFA000), // amber_glow
        onSecondary = Color(0xFF212121), // midnight_black
        surface = Color(0xFFF5F5F5), // ivory_white
        background = Color(0xFFF5F5F5), // ivory_white
        error = Color(0xFFB00020) // error_red
    )
    val darkColors = darkColorScheme(
        primary = Color(0xFF2E7D32), // forest_green
        onPrimary = Color(0xFFF5F5F5), // ivory_white
        secondary = Color(0xFFFFA000), // amber_glow
        onSecondary = Color(0xFF212121), // midnight_black
        surface = Color(0xFF1B5E20), // deep_forest
        background = Color(0xFF1B5E20), // deep_forest
        error = Color(0xFFB00020) // error_red
    )
    MaterialTheme(
        colorScheme = lightColors, // Для темной темы можно использовать darkColors
        content = content
    )
}

@Composable
fun MainScreen() {
    var hasCameraPermission by remember { mutableStateOf(false) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.main_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // BMI Calculator
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text(stringResource(R.string.weight_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text(stringResource(R.string.height_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val w = weight.toFloatOrNull()
                val h = height.toFloatOrNull()?.div(100) // Convert cm to meters
                bmiResult = if (w != null && h != null && h > 0) {
                    val bmi = w / (h * h)
                    String.format("%.1f", bmi)
                } else {
                    "Ошибка"
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(stringResource(R.string.bmi_button))
        }
        bmiResult?.let {
            Text(
                text = stringResource(R.string.bmi_result, it),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Barcode Scanner
        if (hasCameraPermission) {
            Button(
                onClick = { /* TODO: Открыть экран сканирования */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(stringResource(R.string.barcode_button))
            }
        } else {
            Text(
                text = stringResource(R.string.error_message, "Требуется разрешение камеры"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }

        // History
        Button(
            onClick = { /* TODO: Открыть экран истории */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(stringResource(R.string.history_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CalorieCounterTheme {
        MainScreen()
    }
}