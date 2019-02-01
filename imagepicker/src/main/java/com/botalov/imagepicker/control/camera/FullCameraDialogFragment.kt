package com.botalov.imagepicker.control.camera

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.botalov.imagepicker.R


class FullCameraDialogFragment : DialogFragment() {

    private var parentView: View? = null

    fun setParentView(view: View) {
        this.parentView = view
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewX = inflater.inflate(R.layout.full_camera_layout, container, false)
        //setDialogPosition()
        return viewX
    }

    override fun onResume() {
        super.onResume()
        setDialogPosition()
    }

    private fun setDialogPosition() {
        if (parentView == null) {
            return
        }

        val location = IntArray(2)
        parentView?.getLocationOnScreen(location)
        val sourceX = location[0]
        val sourceY = location[1]

        val window = dialog.window

        val attrs = window!!.attributes
        attrs.x = 0// sourceX
        attrs.y = 0//sourceY
        attrs.width = parentView!!.width
        attrs.height = parentView!!.height

        window.attributes = attrs
    }
}