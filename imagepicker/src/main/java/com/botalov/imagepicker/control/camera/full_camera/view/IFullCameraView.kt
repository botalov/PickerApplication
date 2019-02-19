package com.botalov.imagepicker.control.camera.full_camera.view

import android.graphics.Point
import java.io.File

interface IFullCameraView {
    fun updateFlashButton(modeFlash: String?)
    fun updateSwitchCameraButton(camera: Int?)
    fun showError(message: String)

    fun showFullView(centerPreviewPoint: Point, size: Point)
    fun hideFullView()

    fun showPreview(file: File)
}