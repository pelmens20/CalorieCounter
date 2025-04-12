package com.example.caloriecounter

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SlavicFontFamily = FontFamily(Font(R.font.slavic_script, FontWeight.Normal))
val GlagolitsaFontFamily = FontFamily(Font(R.font.glagolitsa, FontWeight.Normal))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        BackgroundImage(R.drawable.oak_bark)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = TextStyle(
                    fontFamily = SlavicFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            var name by remember { mutableStateOf("") }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        stringResource(R.string.login_name_label),
                        style = TextStyle(fontFamily = GlagolitsaFontFamily)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
                textStyle = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            var password by remember { mutableStateOf("") }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        stringResource(R.string.login_password_label),
                        style = TextStyle(fontFamily = GlagolitsaFontFamily)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
                textStyle = TextStyle(fontFamily = GlagolitsaFontFamily, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLoginClick,
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
                    stringResource(R.string.login_button),
                    style = TextStyle(
                        fontFamily = GlagolitsaFontFamily,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}