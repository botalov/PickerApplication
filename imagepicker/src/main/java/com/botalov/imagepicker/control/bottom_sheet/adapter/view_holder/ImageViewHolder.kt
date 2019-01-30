package com.botalov.imagepicker.control.bottom_sheet.adapter.view_holder

import android.view.View
import com.botalov.imagepicker.control.bottom_sheet.model.ImageEntity
import java.io.File
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.botalov.imagepicker.R


class ImageViewHolder(view: View) : BaseViewHolder(view) {

    private val view: View? = view

    override fun bind(image: ImageEntity?) {
        if (image != null) {
            val file = File(image.path)
            if (file.exists()) {
                val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                val myImage = view?.findViewById(R.id.image_view) as ImageView
                myImage.setImageBitmap(myBitmap)
            }
        }
    }
}