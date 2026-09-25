package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.GameScreen
import com.example.ui.theme.CasinoDarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CasinoDarkBg
                ) {
                    ColorPredictorApp()
                }
            }
        }
    }
}

@Composable
fun ColorPredictorApp(gameViewModel: GameViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf("game") } // "game" or "admin"

    val adminSettings by gameViewModel.adminSettings.collectAsState()
    val allProfiles by gameViewModel.allProfiles.collectAsState()

    when (currentScreen) {
        "game" -> {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateToAdmin = { currentScreen = "admin" }
            )
        }
        "admin" -> {
            AdminPanelScreen(
                settings = adminSettings,
                profiles = allProfiles,
                onUpdateSettings = { newSettings ->
                    gameViewModel.updateAdminSettings(newSettings)
                },
                onSetUserBalance = { userId, newBalance ->
                    gameViewModel.adminSetUserBalance(userId, newBalance)
                },
                onResetData = {
                    gameViewModel.adminResetGameData()
                },
                onBackClick = { currentScreen = "game" }
            )
        }
    }
}
