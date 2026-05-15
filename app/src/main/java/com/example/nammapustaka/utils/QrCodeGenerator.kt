package com.example.nammapustaka.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

/**
 * Generates square QR [Bitmap] stickers for books using ZXing core only.
 */
object QrCodeGenerator {
    fun encode(text: String, size: Int = 512): Bitmap {
        val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val w = bitMatrix.width
        val h = bitMatrix.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, w, 0, 0, w, h)
        }
    }
}
