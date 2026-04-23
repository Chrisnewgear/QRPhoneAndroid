package com.example.qrphoneandroid.service

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

class QRCodeService {

    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = mapOf(EncodeHintType.MARGIN to 1)
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val pixels = IntArray(size * size) { i ->
                val x = i % size
                val y = i / size
                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
            val bmp = createBitmap(size, size, Bitmap.Config.ARGB_8888)
            bmp.setPixels(pixels, 0, size, 0, 0, size, size)
            bmp
        } catch (e: Exception) {
            null
        }
    }
}
