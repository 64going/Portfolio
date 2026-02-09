package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import com.example.test.navigation.ScreenNavigation
import com.example.test.screens.HomeScreen
import com.example.test.viewmodel.FinanceViewModel


//import com.example.test.ui.theme.RoomDatabaseDemoTheme

// MainActivity is the entry point of the Android application.
class MainActivity : ComponentActivity() {
    // onCreate is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure the window to use light status bar icons for a modern look.
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        val viewModel: FinanceViewModel by viewModels {
            FinanceViewModel.provideFactory(application)
        }
        setContent {
            MaterialTheme {
                Surface {
                    ScreenNavigation(viewModel)
                }
            }
        }
    }
}