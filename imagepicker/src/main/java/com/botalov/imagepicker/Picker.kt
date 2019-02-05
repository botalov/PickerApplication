package com.botalov.imagepicker

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view.PickerBottomSheet

class Picker {
    private val selectedImageObservers = ArrayList<Observer<Int>>()
    private var mHeightKeyboard: Int = 200

    fun show(context: AppCompatActivity) {
        val intent = Intent(context, PickerBottomSheet::class.java)
        context.startActivity(intent)
    }

    fun addSelectImageObserver(observer: Observer<Int>){
        selectedImageObservers.add(observer)
    }

    //TODO Возможно (даже наверное) не Drawable
    fun setFinishSelectObserver(observer: Observer<List<Drawable>>){

    }

    /**
     * Set keyboard height. Default value = 200(px)
     */
    fun setHeightKeyboard(height: Int){
        mHeightKeyboard = height
    }
}