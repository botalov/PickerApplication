package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PickerBottomSheetBehavior<V: View> : BottomSheetBehavior<V> {
    private var mAllowUserDragging = true

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    fun setAllowUserDragging(allowUserDragging: Boolean) {
        mAllowUserDragging = allowUserDragging
    }

    override fun onInterceptTouchEvent(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        return if (!mAllowUserDragging) {
            false
        } else super.onInterceptTouchEvent(parent, child, event)
    }
}