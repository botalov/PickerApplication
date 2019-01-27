package com.botalov.imagepicker

import android.arch.lifecycle.Observer
import android.graphics.drawable.Drawable

class Picker {
    private val selectedImageObservers = ArrayList<Observer<Int>>()

    fun show(){

    }

    fun addSelectImageObserver(observer: Observer<Int>){
        selectedImageObservers.add(observer)
    }

    //TODO Возможно (даже наверное) не Drawable
    fun setFinishSelectObserver(observer: Observer<List<Drawable>>){

    }

}