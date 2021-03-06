package com.botalov.imagepicker

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view.PickerBottomSheet
import io.reactivex.Observer
import java.io.File

class Picker private constructor() {
    private val selectedImageObservers = ArrayList<Observer<File>>()
    private var mStartHeightPicker: Int = 700
    private var mAccentColor: Int = Color.parseColor("#a3d200")
    private var mImageMaxSize = 10 //Mb

    private var observer: Observer<File>? = null

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

    fun addSelectImageObserver(observer: Observer<File>) {
        selectedImageObservers.add(observer)
    }

    /**
     * Add observer for selected image or make photo
     */
    fun setFinishSelectObserver(observer: Observer<File>) {
        this.observer = observer
    }

    /**
     * Remove observer for selected image or make photo
     */
    fun removeFinishSelectedObserver(observer: Observer<File>){
        this.observer = null
    }
    internal fun getObserver(): Observer<File>?{
        return observer
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
     * Set accent color (buttons text, etc.) Default value = #a3d200
     */
    fun setAccentColor(color: Int) {
        mAccentColor = color
    }
    internal fun getAcceptColor(): Int {
        return mAccentColor
    }

    /**
     * Set max size for uploaded image (MB). Set '0' value for disable the check.
     * Default value = 10 MB
     * @param size Image's size (MB)
     */
    fun setImageMaxSize(size: Int) {
        mImageMaxSize = size
    }
    internal fun getImageMaxSize(): Int {
        return mImageMaxSize
    }
    internal fun getImageMaxSizeInByte(): Int {
        return mImageMaxSize * 1024 * 1024
    }
}