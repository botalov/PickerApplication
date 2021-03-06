package com.botalov.imagepicker.control.bottom_sheet.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import com.botalov.imagepicker.R
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.PickerBottomSheetBehavior
import com.botalov.imagepicker.control.bottom_sheet.presenter.PickerBottomSheetPresenter
import com.botalov.imagepicker.control.bottom_sheet.view.BaseBottomSheetActivity.Constants.COLOR_ARG

abstract class BaseBottomSheetActivity : AppCompatActivity() {
    object Constants {
        const val COLOR_ARG = "color_arg"
    }

    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    private val presenter =
        PickerBottomSheetPresenter()
    private var mColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        super.onCreate(savedInstanceState)
        setStatusBarColor(false)

        setContentView(R.layout.activity_bottom_sheet_base_image_picker)

        baseInit()

        presenter.attachView(this)
    }

    override fun finish() {
        presenter.detachView()
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    private fun baseInit() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val intent = intent
        if (intent != null) {
            mColor = intent.getIntExtra(COLOR_ARG, ContextCompat.getColor(this, R.color.colorPrimaryDark))
        }

        val touchOutside = findViewById<View>(R.id.touch_outside)
        touchOutside?.setOnClickListener { finish() }

        val appBar = findViewById<View>(R.id.app_bar_picker)
        val llBottomSheetContainer = findViewById<RelativeLayout>(R.id.rl_bottom_sheet_content_container)
        if (llBottomSheetContainer != null) {
            bottomSheetBehavior = BottomSheetBehavior.from<RelativeLayout>(llBottomSheetContainer)
            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(view: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> finish()
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            setStatusBarColor(true)
                        }

                        else -> setStatusBarColor(false)
                    }
                }

                override fun onSlide(view: View, slideOffset: Float) {
                    appBar.alpha = slideOffset
                }
            })

            if (bottomSheetBehavior is PickerBottomSheetBehavior<*>) {
                (bottomSheetBehavior as PickerBottomSheetBehavior<*>).setAllowUserDragging(true)
            }
        }
    }

    private fun setStatusBarColor(isTop: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = if (isTop) mColor else Color.TRANSPARENT
        }
    }

    protected fun setPeekHeight(peekHeight: Int) {
        bottomSheetBehavior.peekHeight = peekHeight
    }
}