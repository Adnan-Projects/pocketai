package com.narimukkil.pocketai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.narimukkil.pocketai.ui.main.MainScreen
import com.narimukkil.pocketai.ui.dashboard.PermissionsScreen
import com.narimukkil.pocketai.ui.theme.PocketAITheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.foundation.isSystemInDarkTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            PocketAITheme(darkTheme = isDarkTheme) {
                var permissionsGranted by remember { mutableStateOf(false) }

                if (permissionsGranted) {
                    MainScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                } else {
                    PermissionsScreen(
                        onPermissionsGranted = { permissionsGranted = true }
                    )
                }
            }
        }
    }
}
