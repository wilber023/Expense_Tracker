package com.example.expensetracker.src.home.domain.UseCase


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageProcessingUseCase(private val context: Context) {


    suspend fun uriToMultipart(uri: Uri, fieldName: String = "image"): Result<MultipartBody.Part> {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {

                val resizedBitmap = resizeBitmap(bitmap, 1024, 768)


                val file = createTempImageFile(resizedBitmap)


                bitmap.recycle()
                resizedBitmap.recycle()


                val requestFile = file.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(fieldName, file.name, requestFile)

                Result.success(multipartBody)
            } else {
                Result.failure(Exception("No se pudo decodificar la imagen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createTempImageFile(bitmap: Bitmap): File {
        val timeStamp = System.currentTimeMillis()
        val fileName = "expense_image_$timeStamp.jpg"
        val file = File(context.cacheDir, fileName)

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Comprimir al 80%
        outputStream.flush()
        outputStream.close()

        return file
    }

    private fun resizeBitmap(original: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = original.width
        val height = original.height

        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)

        if (ratio >= 1.0f) {
            return original
        }

        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    }


    fun cleanupTempFiles() {
        try {
            val cacheDir = context.cacheDir
            val files = cacheDir.listFiles { file ->
                file.name.startsWith("expense_image_") && file.name.endsWith(".jpg")
            }

            files?.forEach { file ->
                val fileAge = System.currentTimeMillis() - file.lastModified()
                val oneHour = 60 * 60 * 1000

                if (fileAge > oneHour) {
                    file.delete()
                }
            }
        } catch (e: Exception) {

        }
    }
}