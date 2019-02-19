package com.botalov.imagepicker.control.camera.full_camera.presenter

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.view.View
import com.botalov.imagepicker.control.camera.full_camera.view.IFullCameraView

interface IFullCameraPresenter {
    fun onCreated(startView: View?, activity: Activity)
    fun onPressButtonTakePhoto()
    fun onDonePhoto()
    fun onSwitchCamera()
    fun onChangeFlashMode()
    fun attachView(view: IFullCameraView, context: Context)
    fun detachView()

    fun onUpdatePreview(surface: SurfaceTexture?)
    fun onDestroyCamera()
}