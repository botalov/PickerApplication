package com.botalov.imagepicker.control.bottom_sheet.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewStub
import com.botalov.imagepicker.R
import com.botalov.imagepicker.constants.F.Constants.COUNT_COLUMN
import com.botalov.imagepicker.control.bottom_sheet.adapter.PickerRecyclerViewAdapter
import com.botalov.imagepicker.control.bottom_sheet.model.ImagesRepository
import com.tbruyelle.rxpermissions2.RxPermissions

class PickerBottomSheet : BaseBottomSheetActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewStubContent = findViewById<ViewStub>(R.id.vs_for_content_bottom_sheet)
        viewStubContent.layoutResource = R.layout.content_bottom_sheet_picker
        viewStubContent.inflate()

        initViews()
        setPeekHeight(700)
    }

    @SuppressLint("CheckResult")
    private fun initViews() {
        val rxPermissions = RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .subscribe { granted ->
                if (granted) {
                    val imagesRepository = ImagesRepository()
                    imagesRepository.loadImages(this)
                    val images = imagesRepository.getAllImagesPath()

                    val recyclerView = findViewById<RecyclerView>(com.botalov.imagepicker.R.id.images_recycler_view)
                    recyclerView.layoutManager = GridLayoutManager(this, COUNT_COLUMN)
                    val adapter = PickerRecyclerViewAdapter(this, images)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                  finish()
                }
            }
    }

}