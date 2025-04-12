package com.example.caloriecounter

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(activity: MainActivity?, db: AppDatabase, onHistoryClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val foodList by remember { mutableStateOf(FoodDatabase.loadFoodList()) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf(0f) }
    var bmi by remember { mutableStateOf(0f) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var foodWeight by remember { mutableStateOf("") }
    var showCameraDialog by remember { mutableStateOf(false) }
    val animatedCalories by animateFloatAsState(targetValue = calories, animationSpec = tween(1000))
    val animatedBmi by animateFloatAsState(targetValue = bmi, animationSpec = tween(1000))
    val scope = rememberCoroutineScope()
    val viewModel: BarcodeViewModel = viewModel()

    Box(modifier = modifier) {
        BackgroundImage(R.drawable.woven_cloth)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.main_title),
                style = TextStyle(
                    fontFamily = SlavicFontFamily,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = {
                    Text(
                        stringResource(R.string.weight_label),
                        style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 14.sp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
                textStyle = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = {
                    Text(
                        stringResource(R.string.height_label),
                        style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 14.sp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
                textStyle = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val w = weight.toFloatOrNull() ?: 0f
                    val h = height.toFloatOrNull() ?: 0f
                    bmi = if (w > 0f && h > 0f) {
                        w / ((h / 100) * (h / 100))
                    } else {
                        0f
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    stringResource(R.string.bmi_button),
                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.bmi_result, "%.2f".format(animatedBmi)),
                style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(
                        selectedFood?.name ?: stringResource(R.string.food_label),
                        style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    foodList.forEach { food ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    food.name,
                                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                                )
                            },
                            onClick = {
                                selectedFood = food
                                expanded = false
                            }
                        )
                    }
                }
            }

            selectedFood?.let {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = foodWeight,
                    onValueChange = { foodWeight = it },
                    label = {
                        Text(
                            stringResource(R.string.food_weight_label),
                            style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 14.sp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
                    textStyle = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val w = foodWeight.toFloatOrNull() ?: 0f
                        if (w > 0f) {
                            val newCalories = (it.caloriesPer100g * w) / 100
                            calories += newCalories
                            scope.launch {
                                db.calorieDao().insert(
                                    CalorieData(
                                        calories = newCalories,
                                        date = SimpleDateFormat("yyyy-MM-dd").format(Date())
                                    )
                                )
                            }
                            foodWeight = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        stringResource(R.string.add_food_button),
                        style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        showCameraDialog = true
                    } else if (activity != null) {
                        activity.requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        viewModel.errorMessage = stringResource(R.string.error_message, "Активность недоступна")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    stringResource(R.string.barcode_button),
                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            viewModel.productName?.let { name ->
                Text(
                    text = stringResource(R.string.barcode_result, name),
                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                )
            }
            viewModel.caloriesFromBarcode?.let { barcodeCalories ->
                Text(
                    text = stringResource(R.string.barcode_calories, barcodeCalories.toString()),
                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        calories += barcodeCalories
                        viewModel.saveCaloriesToDb(barcodeCalories)
                        viewModel.clearBarcodeData()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(
                        stringResource(R.string.barcode_add_button),
                        style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                    )
                }
            }
            viewModel.errorMessage?.let { error ->
                Text(
                    text = stringResource(R.string.error_message, error),
                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.calories_total, "%.2f".format(animatedCalories)),
                style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onHistoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    stringResource(R.string.history_button),
                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                )
            }

            if (showCameraDialog) {
                Dialog(onDismissRequest = { showCameraDialog = false }) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.barcode_title),
                                style = TextStyle(
                                    fontFamily = SlavicFontFamily,
                                    fontSize = 28.sp
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                            val executor = remember { Executors.newSingleThreadExecutor() }
                            DisposableEffect(Unit) {
                                onDispose {
                                    executor.shutdown()
                                }
                            }
                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx)
                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                    cameraProviderFuture.addListener({
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.setSurfaceProvider(previewView.surfaceProvider)
                                        }
                                        val imageAnalysis = ImageAnalysis.Builder()
                                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                            .build()
                                        imageAnalysis.setAnalyzer(executor) { imageProxy ->
                                            val mediaImage = imageProxy.image
                                            if (mediaImage != null) {
                                                val inputImage = InputImage.fromMediaImage(
                                                    mediaImage,
                                                    imageProxy.imageInfo.rotationDegrees
                                                )
                                                viewModel.scanBarcode(inputImage)
                                                imageProxy.close()
                                            } else {
                                                imageProxy.close()
                                            }
                                        }
                                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                        try {
                                            cameraProvider.unbindAll()
                                            if (activity != null) {
                                                cameraProvider.bindToLifecycle(activity, cameraSelector, preview, imageAnalysis)
                                            } else {
                                                viewModel.errorMessage = stringResource(R.string.error_message, "Камера недоступна")
                                                showCameraDialog = false
                                            }
                                        } catch (e: Exception) {
                                            viewModel.errorMessage = stringResource(R.string.error_message, e.message ?: "Неизвестная ошибка")
                                            showCameraDialog = false
                                        }
                                    }, ContextCompat.getMainExecutor(ctx))
                                    previewView
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                            )
                            Button(
                                onClick = { showCameraDialog = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    stringResource(R.string.history_back_button),
                                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}