package com.botalov.imagepicker.control.camera.preview_photo.presenter

import com.botalov.imagepicker.control.camera.preview_photo.view.IPreviewView
import java.io.File

interface IPreviewPresenter {
    fun attachView(view: IPreviewView)
    fun detachView()
    fun onOpenImage(file: File)
    fun onBack()
    fun onSend()
}