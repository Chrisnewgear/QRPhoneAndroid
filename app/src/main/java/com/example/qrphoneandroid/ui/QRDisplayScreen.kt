package com.example.qrphoneandroid.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.qrphoneandroid.R
import com.example.qrphoneandroid.ui.theme.NeumorphBase
import com.example.qrphoneandroid.ui.theme.QRError
import com.example.qrphoneandroid.ui.theme.QROnSurfaceVariant
import com.example.qrphoneandroid.ui.theme.QRPrimary
import com.example.qrphoneandroid.viewmodel.UserDataViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun QRDisplayScreen(navController: NavController, viewModel: UserDataViewModel) {
    val userData by viewModel.userData.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    val qrBitmap = remember(userData) { viewModel.generateQRCode() }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title   = { Text(stringResource(R.string.dialog_delete_title)) },
            text    = { Text(stringResource(R.string.dialog_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteData()
                    showDeleteDialog = false
                }) {
                    Text(
                        text  = stringResource(R.string.btn_confirm_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphBase),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                text     = stringResource(R.string.display_subtitle),
                color    = QROnSurfaceVariant,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 36.dp),
            )

            // QR card — raised neumorphic panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .neumorphRaised(cornerRadius = 20.dp, elevation = 10.dp),
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    userData?.let { data ->
                        Text(
                            text       = "${data.firstName} ${data.lastName}",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = QRPrimary,
                        )
                        Text(
                            text     = data.phoneNumber,
                            fontSize = 16.sp,
                            color    = QROnSurfaceVariant,
                        )
                        data.email?.let {
                            Text(
                                text     = it,
                                fontSize = 14.sp,
                                color    = QROnSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    qrBitmap?.let { bmp ->
                        // Inset (pressed) neumorphic frame around the QR bitmap
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier         = Modifier
                                .padding(8.dp)
                                .neumorphPressed(cornerRadius = 16.dp, elevation = 6.dp)
                                .padding(16.dp)
                                .clickable { shareQRCode(context, bmp) },
                        ) {
                            Image(
                                bitmap             = bmp.asImageBitmap(),
                                contentDescription = stringResource(R.string.qr_description),
                                modifier           = Modifier.size(200.dp),
                            )
                        }

                        Text(
                            text     = stringResource(R.string.tap_to_share),
                            fontSize = 12.sp,
                            color    = QROnSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action tiles — match the icon-grid style from the design reference
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),  // all tiles match the tallest
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                NeumorphIconTile(
                    icon     = Icons.Default.QrCodeScanner,
                    label    = stringResource(R.string.btn_scan),
                    onClick  = { navController.navigate("scanner") },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                NeumorphIconTile(
                    icon     = Icons.Default.Edit,
                    label    = stringResource(R.string.btn_edit),
                    onClick  = { viewModel.editData() },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                NeumorphIconTile(
                    icon     = Icons.Default.Delete,
                    label    = stringResource(R.string.btn_delete),
                    onClick  = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    tint     = QRError,
                )
            }
        }
    }
}

private fun shareQRCode(context: Context, bitmap: Bitmap) {
    val file = File(context.cacheDir, "qr_code.png")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir QR"))
}
