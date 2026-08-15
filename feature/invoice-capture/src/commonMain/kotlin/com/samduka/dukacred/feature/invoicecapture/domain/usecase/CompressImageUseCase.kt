package com.samduka.dukacred.feature.invoicecapture.domain.usecase

/**
 * Validates the JPEG produced by the platform capture compressor.
 *
 * CameraX already performs JPEG compression before this common KMP boundary;
 * truncating bytes here would corrupt the image, so an oversize image is a
 * recoverable failure and must be recaptured/compressed by the platform.
 */
class CompressImageUseCase {
    operator fun invoke(rawCapture: ByteArray): Result<ByteArray> = runCatching {
        require(rawCapture.isNotEmpty()) { "Captured image is empty" }
        require(rawCapture.size <= MAX_JPEG_BYTES) {
            "Compressed image exceeds the 1 MB upload limit"
        }
        rawCapture
    }

    private companion object {
        const val MAX_JPEG_BYTES = 1_000_000
    }
}
