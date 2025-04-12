package com.example.caloriecounter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SlavicFeastApp(activity: MainActivity?, db: AppDatabase) {
    val navController = rememberNavController()
    MaterialTheme {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginClick = { navController.navigate("main") },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
            composable("main") {
                MainScreen(
                    activity = activity,
                    db = db,
                    onHistoryClick = { navController.navigate("history") },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
            composable("history") {
                HistoryScreen(
                    db = db,
                    onBackClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }
    }
}

@Composable
fun BackgroundImage(resourceId: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds,
        alpha = 0.3f
    )
}