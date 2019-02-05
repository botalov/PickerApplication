package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.botalov.imagepicker.R
import com.botalov.imagepicker.constants.F.Constants.COUNT_COLUMN
import com.botalov.imagepicker.constants.F.Constants.MAX_FILE_SIZE
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.IPickerContext
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.PickerRecyclerViewAdapter
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.model.ImagesRepository
import com.botalov.imagepicker.control.bottom_sheet.view.BaseBottomSheetActivity
import com.botalov.imagepicker.control.camera.FullCameraDialogFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File

class PickerBottomSheet : BaseBottomSheetActivity(), IPickerContext {

    private val peekHeight = 700
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewStubContent = findViewById<ViewStub>(R.id.vs_for_content_bottom_sheet)
        viewStubContent.layoutResource = R.layout.content_bottom_sheet_picker
        viewStubContent.inflate()

        initViews()
        setPeekHeight(peekHeight)
    }

    override fun openImage(file: File) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clickImage(file: File) {
        if (file.length() > MAX_FILE_SIZE) {
            showImageSizeError()
        }
        else {
            sendImage(file)
        }
    }

    override fun openCamera(parentView: View) {
        val fullCamera = FullCameraDialogFragment.getNewInstance(parentView)
        fullCamera.show((this.getContext() as AppCompatActivity).supportFragmentManager, "FULL_CAMERA")
    }

    override fun getContext(): Context {
        return this
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
        recyclerView.layoutManager = GridLayoutManager(this, COUNT_COLUMN)
        val adapter = PickerRecyclerViewAdapter(this, images)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    private fun showImageSizeError(){
        val builder = AlertDialog.Builder(this)
        val alertDialog = builder.create()
        val view = layoutInflater.inflate(R.layout.error_size_alert_dialog, null)
        alertDialog.setView(view)
        val titleTextView = view.findViewById<TextView>(R.id.title_text_view)
        titleTextView.text = getString(R.string.error_image_size_title)

        val messageTextView = view.findViewById<TextView>(R.id.message_text_view)
        messageTextView.text = getString(R.string.error_image_size_message)

        val closeButton = view.findViewById<Button>(R.id.close_error_dialog_button)
        closeButton.text = getString(R.string.error_cancel_button)
        closeButton.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    private fun sendImage(file: File) {
        Toast.makeText(this, "Select image with path: ${file.path}", Toast.LENGTH_SHORT).show()
    }
}