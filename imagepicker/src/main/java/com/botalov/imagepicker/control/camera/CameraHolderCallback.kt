package com.botalov.imagepicker.control.camera

import android.hardware.Camera
import android.view.SurfaceHolder

class CameraHolderCallback(private val camera: Camera?) : SurfaceHolder.Callback {

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        camera?.setPreviewDisplay(holder)
        camera?.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camera!!.setPreviewDisplay(holder)

        camera.setDisplayOrientation(90)
    }
}