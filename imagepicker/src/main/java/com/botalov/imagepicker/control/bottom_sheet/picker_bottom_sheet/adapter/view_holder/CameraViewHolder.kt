package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.view_holder

import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.botalov.imagepicker.R
import com.botalov.imagepicker.control.camera.CameraHolderCallback
import com.botalov.imagepicker.control.camera.FullCameraDialogFragment


class CameraViewHolder(private val view: View, private val activity: AppCompatActivity) : BaseViewHolder(view), View.OnClickListener {
    private val surfaceView = view.findViewById<SurfaceView>(R.id.camera_surface_view)
    private var camera: Camera? = null

    init {
        surfaceView.setOnClickListener(this)
        val holder = surfaceView.holder
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)

        val holderCallback = CameraHolderCallback(camera)
        holder.addCallback(holderCallback)
    }

    override fun onClick(v: View?) {
        val fullCamera = FullCameraDialogFragment.getNewInstance(view)
        fullCamera.show(activity.supportFragmentManager, "FULL_CAMERA")
    }
}