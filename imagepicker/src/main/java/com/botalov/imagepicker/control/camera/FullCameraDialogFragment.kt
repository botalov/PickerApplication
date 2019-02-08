package com.botalov.imagepicker.control.camera

import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.botalov.imagepicker.utils.AnimationUtils
import android.view.ViewGroup
import com.botalov.imagepicker.R


class FullCameraDialogFragment : DialogFragment() {
    private var startView: View? = null
    private var surfaceView: SurfaceView? = null
    private var camera: Camera? = null

    companion object {
        fun getNewInstance(parentView: View): FullCameraDialogFragment {
            val dialog = FullCameraDialogFragment()
            dialog.setStartView(parentView)
            dialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CameraDialogStyle)
            return dialog
        }
    }

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calculateAndSetDialogPosition()
    }

    private fun setStartView(view: View) {
        this.startView = view
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(R.layout.camera_full_view_layout, container, false)
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
        //calculateAndSetDialogPosition()
        //openAnimation()
    }

    private fun calculateAndSetDialogPosition() {
        if (startView == null) {
            return
        }

        //view?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        //view?.requestLayout()

        /*view?.layoutParams = FrameLayout.LayoutParams(startView!!.width, startView!!.height)*/
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)

        val location = IntArray(2)
        startView?.getLocationInWindow(location)
        val sourceX = location[0]
        val sourceY = location[1]

        val window = dialog.window

        val attrs = window!!.attributes
        val x = -size.x / 2 + sourceX + startView!!.width / 2
        val y = -size.y / 2 + sourceY + startView!!.height / 2
        attrs.x = x
        attrs.y = y

        window.attributes = attrs

        val locationInScreen: IntArray? = IntArray(2)
        startView?.getLocationOnScreen(locationInScreen)

        AnimationUtils.circleAnimationView(surfaceView!!,
            true,
            100000,
            locationInScreen!![0] + startView!!.width / 2,
            locationInScreen!![1] + startView!!.height / 2,
            size)
    }
}