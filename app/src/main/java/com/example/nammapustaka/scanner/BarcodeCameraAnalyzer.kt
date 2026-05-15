package com.example.nammapustaka.scanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage

/**
 * ML Kit barcode analyzer for CameraX preview (QR / library codes).
 */
class BarcodeCameraAnalyzer(
    private val scanner: BarcodeScanner,
    private val onBarcode: (String) -> Unit
) : ImageAnalysis.Analyzer {
    @Volatile
    private var stopped = false

    fun stop() {
        stopped = true
    }

    override fun analyze(image: ImageProxy) {
        if (stopped) {
            image.close()
            return
        }
        val mediaImage = image.image
        if (mediaImage == null) {
            image.close()
            return
        }
        val input = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
        scanner.process(input)
            .addOnSuccessListener { barcodes ->
                val raw = barcodes.firstOrNull()?.rawValue
                if (!raw.isNullOrBlank()) {
                    stopped = true
                    onBarcode(raw)
                }
            }
            .addOnFailureListener { }
            .addOnCompleteListener { image.close() }
    }
}
