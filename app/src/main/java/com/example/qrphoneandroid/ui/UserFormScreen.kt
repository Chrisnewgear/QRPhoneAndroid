package com.example.qrphoneandroid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrphoneandroid.R
import com.example.qrphoneandroid.ui.theme.QRDeepBlue
import com.example.qrphoneandroid.ui.theme.QRLimeGreen
import com.example.qrphoneandroid.ui.theme.QRWhite
import com.example.qrphoneandroid.viewmodel.UserDataViewModel

@Composable
fun UserFormScreen(viewModel: UserDataViewModel) {
    val userData by viewModel.userData.collectAsState()
    
    var firstName by remember { mutableStateOf(userData?.firstName ?: "") }
    var lastName by remember { mutableStateOf(userData?.lastName ?: "") }
    var phoneNumber by remember { mutableStateOf(userData?.phoneNumber ?: "") }
    var email by remember { mutableStateOf(userData?.email ?: "") }

    val validationError by viewModel.validationError.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(QRDeepBlue, QRLimeGreen))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_title),
                color = QRWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.form_subtitle),
                color = QRWhite.copy(alpha = 0.85f),
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = QRWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it.take(50) },
                        label = { Text(stringResource(R.string.label_first_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it.take(50) },
                        label = { Text(stringResource(R.string.label_last_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it.take(20) },
                        label = { Text(stringResource(R.string.label_phone)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.take(100) },
                        label = { Text(stringResource(R.string.label_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    validationError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (userData != null) {
                            OutlinedButton(
                                onClick = { viewModel.cancelEdit() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = QRDeepBlue)
                            ) {
                                Text(stringResource(R.string.btn_cancel))
                            }
                        }
                        
                        Button(
                            onClick = { viewModel.saveData(firstName, lastName, phoneNumber, email) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = QRDeepBlue)
                        ) {
                            Text(
                                text = stringResource(R.string.btn_save_generate),
                                color = QRWhite,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
