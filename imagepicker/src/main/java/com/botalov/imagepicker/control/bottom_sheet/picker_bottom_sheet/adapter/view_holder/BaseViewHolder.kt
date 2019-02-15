package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.view_holder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.model.ImageEntity

abstract class BaseViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    open fun bind(image: ImageEntity?){}
}