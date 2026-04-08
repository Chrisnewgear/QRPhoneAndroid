package com.example.qrphoneandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.qrphoneandroid.ui.ContactPreviewScreen
import com.example.qrphoneandroid.ui.ContentScreen
import com.example.qrphoneandroid.ui.QRScannerScreen
import com.example.qrphoneandroid.ui.theme.QRPhoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            QRPhoneTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "content") {
                    composable("content") {
                        ContentScreen(navController = navController)
                    }
                    composable("scanner") {
                        QRScannerScreen(navController = navController)
                    }
                    composable(
                        route = "contact?data={encodedData}",
                        arguments = listOf(
                            navArgument("encodedData") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) { backStackEntry ->
                        val encodedData = backStackEntry.arguments?.getString("encodedData") ?: ""
                        ContactPreviewScreen(
                            encodedData = encodedData,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
