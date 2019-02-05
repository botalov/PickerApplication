package com.botalov.imagepicker.control.camera

import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.FrameLayout
import com.botalov.imagepicker.R

class FullCameraDialogFragment : DialogFragment() {
    private var parentView: View? = null
    private var surfaceView: SurfaceView? = null
    private var camera: Camera? = null

    companion object{
        fun getNewInstance(parentView: View): FullCameraDialogFragment {
            val dialog = FullCameraDialogFragment()
            dialog.setParentView(parentView)
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CameraDialogStyle)
            return dialog
        }
    }

    private fun setParentView(view: View) {
        this.parentView = view
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(R.layout.full_camera_layout, container, false)
        surfaceView = mainView.findViewById(R.id.camera_surface_view)
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        val holderCallback = CameraHolderCallback(camera)
        val holder = surfaceView?.holder
        holder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        holder?.addCallback(holderCallback)

        return mainView
    }

    override fun onResume() {
        super.onResume()
        calculateAndSetDialogPosition()
    }

    private fun calculateAndSetDialogPosition() {
        if (parentView == null) {
            return
        }

        view?.layoutParams = FrameLayout.LayoutParams(parentView!!.width, parentView!!.height)

        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)

        val location = IntArray(2)
        parentView?.getLocationInWindow(location)
        val sourceX = location[0]
        val sourceY = location[1]

        val window = dialog.window

        val attrs = window!!.attributes
        attrs.x = -size.x / 2 + sourceX + parentView!!.width / 2
        attrs.y = -size.y / 2 + sourceY + parentView!!.height / 2

        window.attributes = attrs
    }
}