package com.example.qrphoneandroid.ui

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qrphoneandroid.viewmodel.UserDataViewModel

@Composable
fun ContentScreen(navController: NavController, viewModel: UserDataViewModel = viewModel()) {
    val isDataSaved by viewModel.isDataSaved.collectAsState()

    Crossfade(targetState = isDataSaved, label = "content_transition") { saved ->
        if (saved) {
            QRDisplayScreen(navController = navController, viewModel = viewModel)
        } else {
            UserFormScreen(viewModel = viewModel)
        }
    }
}
