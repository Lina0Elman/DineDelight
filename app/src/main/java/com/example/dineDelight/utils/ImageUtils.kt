package com.example.dineDelight.utils

import com.example.dineDelight.models.Image
import com.example.dineDelight.models.ImageEntity

object ImageUtils {
    fun Image.toImageEntity(): ImageEntity {
        return ImageEntity(id = this.id, blobBase64String = this.blobBase64String)
    }

    fun ImageEntity.toImage(): Image {
        return Image(id = this.id, blobBase64String = this.blobBase64String)
    }
}