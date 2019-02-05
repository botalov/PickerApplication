package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import com.botalov.imagepicker.R
import com.botalov.imagepicker.constants.F.Constants.COUNT_COLUMN
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.PickerRecyclerViewAdapter
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.model.ImagesRepository
import com.botalov.imagepicker.control.bottom_sheet.view.BaseBottomSheetActivity
import com.tbruyelle.rxpermissions2.RxPermissions

class PickerBottomSheet : BaseBottomSheetActivity() {

    val peekHeight = 700
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewStubContent = findViewById<ViewStub>(R.id.vs_for_content_bottom_sheet)
        viewStubContent.layoutResource = R.layout.content_bottom_sheet_picker
        viewStubContent.inflate()

        initViews()
        setPeekHeight(peekHeight)
    }

    @SuppressLint("CheckResult")
    private fun initViews() {
        val rxPermissions = RxPermissions(this)
        val permissionsLL = findViewById<ConstraintLayout>(R.id.main_permissions_cl)
        if(!rxPermissions.isGranted(Manifest.permission.CAMERA) || !rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionsLL.visibility = View.VISIBLE
            val separatorView = findViewById<View>(R.id.separator_view)
            val params = separatorView.layoutParams
            if(params is ViewGroup.MarginLayoutParams) {
                params.topMargin = peekHeight
                separatorView.requestLayout()
            }

            val button = findViewById<Button>(R.id.permissions_button)
            button.setOnClickListener {
                val rxPermissions = RxPermissions(this)
                rxPermissions
                    .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .subscribe { granted ->
                        if(granted) {
                            permissionsLL.visibility = View.GONE
                            loadImages()
                        }
                        else {
                            finish()
                        }
                    }
            }
        }
        else {
            permissionsLL.visibility = View.GONE
           loadImages()
        }
    }

    private fun loadImages() {
        val imagesRepository = ImagesRepository()
        imagesRepository.loadImages(this)
        val images = imagesRepository.getAllImagesPath()

        val recyclerView = findViewById<RecyclerView>(com.botalov.imagepicker.R.id.images_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, COUNT_COLUMN) as RecyclerView.LayoutManager?
        val adapter = PickerRecyclerViewAdapter(this, images)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}