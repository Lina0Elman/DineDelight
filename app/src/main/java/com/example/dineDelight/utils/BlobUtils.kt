package com.example.dineDelight.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import android.util.Base64

object BlobUtils {
    fun Uri.toBlob(context: Context): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(this)
            inputStream?.readBytes()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Usage example:
     * ```
     * // Assuming you have a ByteArray called blob
     * val bitmap = blob.toBitmap()
     *
     * // Now you can use this bitmap in your UI, for example, in an Image composable
     * if (bitmap != null) {
     *     Image(
     *         bitmap = bitmap.asImageBitmap(),
     *         contentDescription = null,
     *         modifier = Modifier.fillMaxWidth().height(150.dp)
     *     )
     * }
     * ```
     */
    fun ByteArray.toBitmap(): Bitmap? {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    fun Bitmap.toUri(context: Context): Uri? {
        return try {
            val bytes = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(context.contentResolver, this, "Title", null)
            Uri.parse(path)
        }
        catch (e: Exception) {
            null
        }
    }

    /**
     * Convert Base64 string to blob.
     */
    fun String.toBlob(): ByteArray? {
        return this.let { Base64.decode(it, Base64.DEFAULT) }
    }

    /**
     * Convert blob to Base64 string.
     */
    fun ByteArray.toBase64String(): String {
        return Base64.encodeToString(this, Base64.DEFAULT)
    }
} 