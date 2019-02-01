package com.botalov.imagepicker.control.bottom_sheet.adapter.view_holder

import android.content.Context
import android.view.View
import com.botalov.imagepicker.control.bottom_sheet.model.ImageEntity
import java.io.File
import android.widget.ImageView
import com.botalov.imagepicker.R
import android.net.Uri
import com.bumptech.glide.Glide

class ImageViewHolder(private val view: View, private val context: Context) : BaseViewHolder(view) {
    override fun bind(image: ImageEntity?) {
        if (image != null) {
            val file = File(image.path)
            if (file.exists()) {
                val imgView = view.findViewById(R.id.image_view) as ImageView
                Glide.with(context)
                    .load(Uri.fromFile(file))
                    .into(imgView)
            }
        }
    }
}