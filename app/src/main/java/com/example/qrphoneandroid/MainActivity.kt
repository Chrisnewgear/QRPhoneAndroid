package com.example.qrphoneandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.example.qrphoneandroid.ui.theme.QRPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            QRPhoneTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top bar — 10 dp bluish accent
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(QRPrimary)
                    )

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "content",
                        modifier = Modifier.weight(1f),
                    ) {
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

                    // Bottom bar — 15 dp bluish accent
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(60.dp)
//                            .background(QRPrimary)
//                    )
                }
            }
        }
    }
}
