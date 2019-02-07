package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.botalov.imagepicker.Picker
import com.botalov.imagepicker.R
import com.botalov.imagepicker.constants.F.Constants.COUNT_COLUMN
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.IPickerContext
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.PickerRecyclerViewAdapter
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.model.ImagesRepository
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.presenter.PickerPresenter
import com.botalov.imagepicker.control.bottom_sheet.view.BaseBottomSheetActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import java.io.File

class PickerBottomSheet : BaseBottomSheetActivity(), IPickerContext {

    private val presenter = PickerPresenter()
    private val peekHeight = Picker.getInstance().getStartHeightPicker()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewStubContent = findViewById<ViewStub>(R.id.vs_for_content_bottom_sheet)
        viewStubContent.layoutResource = R.layout.content_bottom_sheet_picker
        viewStubContent.inflate()

        initViews()
        setPeekHeight(peekHeight)

        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

   /* override fun openImage(file: File) {
        TODO("not implemented") //To change body of created functions use Filжe | Settings | File Templates.
    }

    override fun openCamera(parentView: View) {
        val fullCamera = FullCameraDialogFragment.getNewInstance(parentView)
        fullCamera.show((this.getContext() as AppCompatActivity).supportFragmentManager, "FULL_CAMERA")
    }
    */
    override fun getContext(): Context {
        return this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picker, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id!!){
            R.id.action_close -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
            button.setTextColor(Picker.getInstance().getAcceptColor())
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

        initToolbar()
    }

    private fun loadImages() {
        val imagesRepository = ImagesRepository()
        imagesRepository.loadImages(this)
        val images = imagesRepository.getAllImagesPath()

        val recyclerView = findViewById<RecyclerView>(com.botalov.imagepicker.R.id.images_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, COUNT_COLUMN)
        val adapter = PickerRecyclerViewAdapter(this, images, presenter)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    fun showImageSizeError(){
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

    fun sendImage(file: File) {
        Observable.just(file).subscribe(Picker.getInstance().getObserver()!!)
        finish()
    }

    private fun initToolbar() {
        val appBar = findViewById<View>(R.id.app_bar_picker)
        val toolbar = appBar.findViewById<Toolbar>(R.id.toolbar)
        /*val toolbarView = layoutInflater.inflate(R.layout.toolbar_picker_view, null)
        toolbar.addView(toolbarView)*/
        toolbar.title=""
        setSupportActionBar(toolbar)
    }
}