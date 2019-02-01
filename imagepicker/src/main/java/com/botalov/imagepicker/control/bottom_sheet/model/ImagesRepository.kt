package com.botalov.imagepicker.control.bottom_sheet.model

import android.content.Context
import android.provider.MediaStore



class ImagesRepository {

    private val allImages = ArrayList<ImageEntity>()

    fun loadImages(context: Context) {
        allImages.clear()
        val starStr = arrayOf("*")
        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            starStr,
            null,
            null,
            null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA))
                    allImages.add(ImageEntity(path))
                } while (cursor.moveToNext())

            }
            cursor.close()
        }
    }


    fun getAllImagesPath(): ArrayList<ImageEntity> {return allImages}
}