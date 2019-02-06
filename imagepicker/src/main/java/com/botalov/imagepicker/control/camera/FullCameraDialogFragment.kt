package com.botalov.imagepicker.control.camera

import android.animation.ValueAnimator
import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.FrameLayout
import android.animation.AnimatorSet
import android.animation.Animator
import com.botalov.imagepicker.R
import android.animation.AnimatorListenerAdapter
import android.widget.ImageButton


class FullCameraDialogFragment : DialogFragment() {
    private var startView: View? = null
    private var surfaceView: SurfaceView? = null
    private var camera: Camera? = null

    companion object {
        fun getNewInstance(parentView: View): FullCameraDialogFragment {
            val dialog = FullCameraDialogFragment()
            dialog.setStartView(parentView)
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CameraDialogStyle)
            return dialog
        }
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
        calculateAndSetDialogPosition()
        openAnimation()
    }

    private fun calculateAndSetDialogPosition() {
        if (startView == null) {
            return
        }

        view?.layoutParams = FrameLayout.LayoutParams(startView!!.width, startView!!.height)

        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)

        val location = IntArray(2)
        startView?.getLocationInWindow(location)
        val sourceX = location[0]
        val sourceY = location[1]

        val window = dialog.window

        val attrs = window!!.attributes
        attrs.x = -size.x / 2 + sourceX + startView!!.width / 2
        attrs.y = -size.y / 2 + sourceY + startView!!.height / 2

        window.attributes = attrs
    }

    private fun openAnimation() {
        val animator = getViewScaleAnimator(view!!)
        animator.addListener(getAnimatorListener(view!!.findViewById(R.id.shutter_image_button)))
        animator.start()
    }

    private fun getViewScaleAnimator(from: View): Animator {
        val animatorSet = AnimatorSet()
        val desiredHeight = activity?.window?.windowManager?.defaultDisplay?.height
        val currentHeight = startView?.height
        val heightAnimator = ValueAnimator.ofInt(currentHeight!!, desiredHeight!!).setDuration(1000)
        heightAnimator.addUpdateListener { animation ->
            val params = from.layoutParams as FrameLayout.LayoutParams
            params.height = animation.animatedValue as Int
            from.layoutParams = params
        }
        animatorSet.play(heightAnimator)

        val desiredWidth = activity?.window?.windowManager?.defaultDisplay?.width
        val currentWidth = startView?.width
        val widthAnimator = ValueAnimator.ofInt(currentWidth!!, desiredWidth!!).setDuration(1000)
        widthAnimator.addUpdateListener { animation ->
            val params = from.layoutParams as FrameLayout.LayoutParams
            params.width = animation.animatedValue as Int
            from.layoutParams = params
        }

        animatorSet.play(widthAnimator)

        return animatorSet
    }

    private fun getAnimatorListener(view: ImageButton): AnimatorListenerAdapter {
        return object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                view.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.VISIBLE
            }
        }
    }
}