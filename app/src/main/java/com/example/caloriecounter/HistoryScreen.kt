package com.example.caloriecounter

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(db: AppDatabase, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var calorieData by remember { mutableStateOf<List<CalorieData>>(emptyList()) }
    LaunchedEffect(Unit) {
        scope.launch {
            calorieData = db.calorieDao().getAll()
        }
    }

    Box(modifier = modifier) {
        BackgroundImage(R.drawable.oak_bark)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.history_title),
                style = TextStyle(
                    fontFamily = SlavicFontFamily,
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                items(calorieData) { data ->
                    Text(
                        text = "${data.date}: ${data.calories} зёрен силы",
                        style = TextStyle(
                            fontFamily = GlagolitsaFontFamily,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackClick,
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
                    stringResource(R.string.history_back_button),
                    style = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 18.sp)
                )
            }
        }
    }
}