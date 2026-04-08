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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrphoneandroid.R
import com.example.qrphoneandroid.ui.theme.NeumorphBase
import com.example.qrphoneandroid.ui.theme.NeumorphDark
import com.example.qrphoneandroid.ui.theme.QROnBackground
import com.example.qrphoneandroid.ui.theme.QROnSurfaceVariant
import com.example.qrphoneandroid.ui.theme.QRPrimary
import com.example.qrphoneandroid.viewmodel.UserDataViewModel

// ── Screen-specific sub-composable ────────────────────────────────────────────

/** OutlinedTextField blended into the neumorphic surface. */
@Composable
private fun NeumorphTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        modifier      = Modifier.fillMaxWidth(),
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape  = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = NeumorphBase,
            unfocusedContainerColor = NeumorphBase,
            focusedBorderColor      = QRPrimary,
            unfocusedBorderColor    = NeumorphDark,
            focusedLabelColor       = QRPrimary,
            unfocusedLabelColor     = QROnSurfaceVariant,
            cursorColor             = QRPrimary,
            focusedTextColor        = QROnBackground,
            unfocusedTextColor      = QROnBackground,
        ),
    )
}

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun UserFormScreen(viewModel: UserDataViewModel) {
    val userData by viewModel.userData.collectAsState()

    var firstName   by remember { mutableStateOf(userData?.firstName   ?: "") }
    var lastName    by remember { mutableStateOf(userData?.lastName    ?: "") }
    var phoneNumber by remember { mutableStateOf(userData?.phoneNumber ?: "") }
    var email       by remember { mutableStateOf(userData?.email       ?: "") }

    val validationError by viewModel.validationError.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphBase),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Header
            Text(
                text       = stringResource(R.string.app_title),
                color      = QRPrimary,
                fontSize   = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(bottom = 6.dp),
            )
            Text(
                text     = stringResource(R.string.form_subtitle),
                color    = QROnSurfaceVariant,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 36.dp),
            )

            // Neumorphic card panel — extra padding gives shadows room to breathe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .neumorphRaised(cornerRadius = 20.dp, elevation = 10.dp),
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    NeumorphTextField(
                        value         = firstName,
                        onValueChange = { firstName = it.take(50) },
                        label         = stringResource(R.string.label_first_name),
                    )
                    NeumorphTextField(
                        value         = lastName,
                        onValueChange = { lastName = it.take(50) },
                        label         = stringResource(R.string.label_last_name),
                    )
                    NeumorphTextField(
                        value         = phoneNumber,
                        onValueChange = { phoneNumber = it.take(20) },
                        label         = stringResource(R.string.label_phone),
                        keyboardType  = KeyboardType.Phone,
                    )
                    NeumorphTextField(
                        value         = email,
                        onValueChange = { email = it.take(100) },
                        label         = stringResource(R.string.label_email),
                        keyboardType  = KeyboardType.Email,
                    )

                    validationError?.let {
                        Text(
                            text     = it,
                            color    = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (userData != null) {
                            NeumorphSecondaryButton(
                                text     = stringResource(R.string.btn_cancel),
                                onClick  = { viewModel.cancelEdit() },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        NeumorphPrimaryButton(
                            text     = stringResource(R.string.btn_save_generate),
                            onClick  = { viewModel.saveData(firstName, lastName, phoneNumber, email) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
