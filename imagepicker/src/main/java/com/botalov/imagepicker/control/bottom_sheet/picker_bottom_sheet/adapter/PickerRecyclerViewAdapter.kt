package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.botalov.imagepicker.R
import com.botalov.imagepicker.constants.F.Constants.COUNT_COLUMN
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.view_holder.BaseViewHolder
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.view_holder.CameraViewHolder
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.view_holder.ImageViewHolder
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.view_holder.ViewHolderType
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.model.ImageEntity
import java.lang.Exception


class PickerRecyclerViewAdapter(private val context: Context, images: List<ImageEntity>) : RecyclerView.Adapter<BaseViewHolder>() {
    private var mInflater: LayoutInflater? = null
    private var mImages: List<ImageEntity>? = images

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder {
        val height = parent.measuredWidth / COUNT_COLUMN
        val type = getItemViewType(position)

        val clParamsViewHolder =
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        clParamsViewHolder.height = height
        clParamsViewHolder.width = height

        return when (type) {
            ViewHolderType.CAMERA.ordinal -> {
                val view: View = mInflater!!.inflate(R.layout.camera_view_holder, parent, false)
                view.layoutParams = clParamsViewHolder
                CameraViewHolder(view, context as AppCompatActivity)
            }
            ViewHolderType.IMAGE.ordinal -> {
                val view: View = mInflater!!.inflate(R.layout.image_view_holder, parent, false)
                view.layoutParams = clParamsViewHolder
                ImageViewHolder(view, context)
            }
            else -> {
                throw Exception("Type of view holder is wrong")
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mImages == null) 0 else mImages!!.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ViewHolderType.CAMERA.ordinal
            else -> ViewHolderType.IMAGE.ordinal
        }
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            ViewHolderType.CAMERA.ordinal -> {
                val cameraViewHolder = viewHolder as CameraViewHolder
                cameraViewHolder.bind(null)
            }
            ViewHolderType.IMAGE.ordinal -> {
                val imageViewHolder = viewHolder as ImageViewHolder
                imageViewHolder.bind(mImages?.get(position - 1))
            }
        }
    }
}