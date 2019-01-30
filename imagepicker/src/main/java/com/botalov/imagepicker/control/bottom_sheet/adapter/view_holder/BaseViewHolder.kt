package com.botalov.imagepicker.control.bottom_sheet.adapter.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.botalov.imagepicker.control.bottom_sheet.model.ImageEntity

abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(image: ImageEntity?){}
}