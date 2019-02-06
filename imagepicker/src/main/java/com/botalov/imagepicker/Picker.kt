package com.botalov.imagepicker

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view.PickerBottomSheet

class Picker private constructor() {
    private val selectedImageObservers = ArrayList<Observer<Int>>()
    private var mStartHeightPicker: Int = 700
    private var mAccentColor: Int = Color.parseColor("#a3d200")

    companion object {
        private var instance: Picker? = null
        fun getInstance(): Picker{
            if(instance == null) {
                instance = Picker()
            }
            return instance as Picker
        }
    }

    fun show(context: AppCompatActivity) {
        val intent = Intent(context, PickerBottomSheet::class.java)
        context.startActivity(intent)
    }

    fun addSelectImageObserver(observer: Observer<Int>) {
        selectedImageObservers.add(observer)
    }

    //TODO Возможно (даже наверное) не Drawable
    fun setFinishSelectObserver(observer: Observer<List<Drawable>>) {

    }

    /**
     * Set keyboard height. Default value = 700(px)
     */
    fun setStartHeightPicker(height: Int) {
        mStartHeightPicker = height
    }
    internal fun getStartHeightPicker(): Int {
        return mStartHeightPicker
    }

    /**
     * Set accent color (buttons, etc.) Default value = #a3d200
     */
    fun setAccentColor(color: Int) {
        mAccentColor = color
    }
    internal fun getAcceptColor(): Int {
        return mAccentColor
    }
}