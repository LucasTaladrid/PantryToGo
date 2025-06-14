package com.lucasdev.apprecetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.lucasdev.apprecetas.ui.navigation.AppNavHost
import com.lucasdev.apprecetas.ui.theme.AppRecetasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRecetasTheme {
                AppNavHost(navController = rememberNavController())
                }
            }
        }
    }

