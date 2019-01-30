package com.botalov.imagepicker.control.bottom_sheet.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.ViewStub
import com.botalov.imagepicker.R
import android.support.v7.widget.GridLayoutManager
import com.botalov.imagepicker.control.bottom_sheet.adapter.PickerRecyclerViewAdapter
import com.botalov.imagepicker.control.bottom_sheet.model.ImageEntity
import com.botalov.imagepicker.control.bottom_sheet.model.ImagesRepository
import com.botalov.imagepicker.utils.PermissionsUtils
import com.botalov.imagepicker.utils.PermissionsUtils.Companion.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE


class PickerBottomSheet : BaseBottomSheetActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewStubContent = findViewById<ViewStub>(R.id.vs_for_content_bottom_sheet)
        viewStubContent.layoutResource = R.layout.content_bottom_sheet_picker
        viewStubContent.inflate()

        initViews()
        setPeekHeight(700)
    }

    private fun initViews() {
        val permissionsUtils = PermissionsUtils()
        if (permissionsUtils.checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            val imagesRepository = ImagesRepository()
            imagesRepository.loadImages(this)
            val images = imagesRepository.getAllImagesPath()

            val recyclerView = findViewById<RecyclerView>(R.id.images_recycler_view)
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            val adapter = PickerRecyclerViewAdapter(this, images)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
       when(requestCode) {
           MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE->{
               if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   initViews()
               }
           }
           else->{
               super.onRequestPermissionsResult(requestCode, permissions, grantResults)
           }
       }
    }
}