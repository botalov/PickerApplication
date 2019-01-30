package com.botalov.imagepicker.control.bottom_sheet

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
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

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        return if (!mAllowUserDragging) {
            false
        } else super.onInterceptTouchEvent(parent, child, event)
    }
}