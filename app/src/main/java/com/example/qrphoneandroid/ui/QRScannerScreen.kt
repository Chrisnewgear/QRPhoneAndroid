package com.example.qrphoneandroid.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.qrphoneandroid.R
import com.example.qrphoneandroid.ui.theme.NeumorphBase
import com.example.qrphoneandroid.ui.theme.QROnSurfaceVariant
import com.example.qrphoneandroid.ui.theme.QRPrimary
import com.example.qrphoneandroid.ui.theme.QRWhite
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.net.URLEncoder
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun QRScannerScreen(navController: NavController) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val msgQrInvalid    = stringResource(R.string.qr_invalid)
    val msgCameraError  = stringResource(R.string.camera_init_error)

    val scannedRef     = remember { AtomicBoolean(false) }
    val executor       = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            barcodeScanner.close()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        // ── Camera view (full-screen, dark) ───────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView         = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(executor) { imageProxy ->
                                    if (scannedRef.get()) {
                                        imageProxy.close()
                                        return@setAnalyzer
                                    }
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees,
                                        )
                                        barcodeScanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                for (barcode in barcodes) {
                                                    if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                                        val raw = barcode.rawValue ?: continue
                                                        val validated = validateQRPayload(raw)
                                                        if (validated != null) {
                                                            if (scannedRef.compareAndSet(false, true)) {
                                                                val encoded = URLEncoder.encode(validated, "UTF-8")
                                                                navController.navigate("contact?data=$encoded")
                                                            }
                                                        } else {
                                                            errorMessage = msgQrInvalid
                                                        }
                                                        break
                                                    }
                                                }
                                            }
                                            .addOnCompleteListener { imageProxy.close() }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalyzer,
                            )
                        } catch (e: Exception) {
                            errorMessage = msgCameraError
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize(),
            )

            // Viewfinder — rounded corners, primary-color border
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, QRPrimary, RoundedCornerShape(16.dp)),
            )

            // Instruction text
            Text(
                text     = stringResource(R.string.scan_instruction),
                color    = QRWhite,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
            )

            // Back button — white icon on dark background
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(44.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable(
                        interactionSource = backInteraction,
                        indication        = null,
                        onClick           = { navController.popBackStack() },
                    ),
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint               = QRWhite,
                    modifier           = Modifier.size(22.dp),
                )
            }

            // Error snackbar
            errorMessage?.let { err ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { errorMessage = null }) { Text(stringResource(R.string.btn_ok)) }
                    },
                ) { Text(err) }
            }
        }
    } else {
        // ── Permission-denied state — neumorphic ──────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeumorphBase),
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Back button
                val backInteraction = remember { MutableInteractionSource() }
                val backPressed by backInteraction.collectIsPressedAsState()

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Start)
                        .then(
                            if (backPressed) Modifier.neumorphPressed(cornerRadius = 24.dp, elevation = 4.dp)
                            else             Modifier.neumorphRaised(cornerRadius = 24.dp, elevation = 6.dp)
                        )
                        .clickable(
                            interactionSource = backInteraction,
                            indication        = null,
                            onClick           = { navController.popBackStack() },
                        ),
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint               = QRPrimary,
                        modifier           = Modifier.size(22.dp),
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text       = stringResource(R.string.camera_permission_required),
                    color      = QROnSurfaceVariant,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.padding(bottom = 32.dp),
                )

                NeumorphPrimaryButton(
                    text    = stringResource(R.string.btn_grant_permission),
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                )
            }
        }
    }
}

private fun validateQRPayload(raw: String): String? {
    if (raw.length > 500) return null
    val parts = raw.split("\n")
    if (parts.size < 3) return null

    val firstName = parts[0].trim()
    val lastName  = parts[1].trim()
    val phone     = parts[2].trim()
    if (firstName.isEmpty() || lastName.isEmpty()) return null

    val phoneRegex = Regex("^\\+[1-9][0-9]{6,24}$")
    if (!phoneRegex.matches(phone)) return null

    if (parts.size >= 4) {
        val email = parts[3].trim()
        if (email.isNotEmpty()) {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            if (!emailRegex.matches(email)) return null
        }
    }
    return raw
}
