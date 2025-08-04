package com.example.expensetracker.src.feature.home.domain.UseCase

import android.content.Context
import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageProcessingUseCase(
    private val context: Context
) {
    fun uriToMultipart(uri: Uri): Result<MultipartBody.Part> {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.let { stream ->
                val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)

                stream.copyTo(outputStream)
                stream.close()
                outputStream.close()

                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", file.name, requestBody)

                Result.success(part)
            } ?: Result.failure(Exception("No se pudo abrir el archivo"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cleanupTempFiles() {
        try {
            val cacheDir = context.cacheDir
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("temp_image_")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            //
        }
    }
}
