package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.view_holder

import android.view.View
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.model.ImageEntity
import java.io.File
import android.widget.ImageView
import com.botalov.imagepicker.R
import android.net.Uri
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.IPickerContext
import com.bumptech.glide.Glide

class ImageViewHolder(private val view: View, private val pickerContext: IPickerContext) : BaseViewHolder(view),  View.OnClickListener {
    private var file: File? = null

    override fun bind(image: ImageEntity?) {
        if (image != null) {
            file = File(image.path)
            if (file!!.exists()) {
                val imgView = view.findViewById(R.id.image_view) as ImageView
                imgView.setOnClickListener(this)
                Glide.with(pickerContext.getContext())
                    .load(Uri.fromFile(file))
                    .into(imgView)
            }
        }
    }


    override fun onClick(v: View?) {
        pickerContext.clickImage(file!!)
    }
}