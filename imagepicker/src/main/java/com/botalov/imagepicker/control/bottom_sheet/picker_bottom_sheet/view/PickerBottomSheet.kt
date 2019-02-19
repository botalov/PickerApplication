package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.botalov.imagepicker.Picker
import com.botalov.imagepicker.R
import com.botalov.imagepicker.constants.F.Constants.COUNT_COLUMN
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.IPickerContext
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.adapter.PickerRecyclerViewAdapter
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.model.ImagesRepository
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.presenter.PickerPresenter
import com.botalov.imagepicker.control.bottom_sheet.view.BaseBottomSheetActivity
import com.botalov.imagepicker.control.camera.full_camera.view.FullCameraDialogFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class PickerBottomSheet : BaseBottomSheetActivity(), IPickerContext {

    private var context: Context? = null
    private val presenter = PickerPresenter()
    private val peekHeight = Picker.getInstance().getStartHeightPicker()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this

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

    override fun getContext(): Context {
        return this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picker, menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            menu!!.findItem(R.id.action_close).icon.setTint(Picker.getInstance().getAcceptColor())
        } else {
            menu!!.findItem(R.id.action_close).icon.colorFilter =
                PorterDuffColorFilter(Picker.getInstance().getAcceptColor(), PorterDuff.Mode.SRC_IN)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id!!) {
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
        val permissionsLL: ConstraintLayout = findViewById(R.id.main_permissions_cl)
        if (!rxPermissions.isGranted(Manifest.permission.CAMERA)
            || !rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
            || !rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {

            permissionsLL.visibility = View.VISIBLE
            val separatorView = findViewById<View>(R.id.separator_view)
            val params = separatorView.layoutParams
            if (params is ViewGroup.MarginLayoutParams) {
                params.topMargin = peekHeight
                separatorView.requestLayout()
            }

            val button = findViewById<Button>(R.id.permissions_button)
            button.setTextColor(Picker.getInstance().getAcceptColor())
            button.setOnClickListener {
                rxPermissions
                    .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .subscribe(object : Observer<Boolean>{
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(isPermission: Boolean) {
                            if (isPermission) {
                                permissionsLL.visibility = View.GONE
                                loadImages()
                            } else {
                                finish()
                            }
                        }

                        override fun onError(e: Throwable) {
                            showError("Permissions error")
                        }

                    })

            }
        } else {
            permissionsLL.visibility = View.GONE
            loadImages()
        }

        initToolbar()
    }


    private fun loadImages() {
        val imagesRepository = ImagesRepository()
        val dis = Observable
            .just(true)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                imagesRepository.loadImages(context!!)
                Thread.sleep(1)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : Observer<Boolean>{override fun onSubscribe(d: Disposable) {
            }
                override fun onError(e: Throwable) {
                }

                override fun onNext(v: Boolean) {
                    val images = imagesRepository.getAllImagesPath()
                    val adapter = PickerRecyclerViewAdapter(this@PickerBottomSheet, images, presenter)

                    val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(com.botalov.imagepicker.R.id.images_recycler_view)
                    recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context!!, COUNT_COLUMN)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

                override fun onComplete() {

                }})

    }

    @SuppressLint("InflateParams")
    fun showImageSizeError() {
        val builder = AlertDialog.Builder(this)
        val alertDialog = builder.create()
        val view = layoutInflater.inflate(R.layout.error_size_alert_dialog, null)
        alertDialog.setView(view)
        val titleTextView = view.findViewById<TextView>(R.id.title_text_view)
        titleTextView.text = getString(R.string.error_image_size_title)

        val messageTextView = view.findViewById<TextView>(R.id.message_text_view)
        val maxsize = Picker.getInstance().getImageMaxSize()
        val message = String.format(getString(R.string.error_image_size_message), maxsize)
        messageTextView.text = message

        val closeButton = view.findViewById<Button>(R.id.close_error_dialog_button)
        closeButton.text = getString(R.string.error_cancel_button)
        closeButton.setTextColor(Picker.getInstance().getAcceptColor())
        closeButton.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    fun sendImage(file: File) {
        Observable.just(file).subscribe(Picker.getInstance().getObserver()!!)
        finish()
    }

    fun showFullCamera(startView: View) {
        val fullCamera = FullCameraDialogFragment.getNewInstance(startView)
        fullCamera.setSubscribe(object : Observer<File> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                Log.e("PickerBottomSheet", e.message)
                showError("Error")
            }

            override fun onNext(file: File) {

                run {
                    val isCorrectSize =
                        Picker.getInstance().getImageMaxSizeInByte() > 0 && file.length() < Picker.getInstance().getImageMaxSizeInByte()

                    if (isCorrectSize) {
                        sendImage(file)
                    } else {
                        showImageSizeError()
                    }
                }

            }
        })
        fullCamera.show((this.getContext() as AppCompatActivity).supportFragmentManager, "FULL_CAMERA")
    }

    private fun showError(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun initToolbar() {
        val appBar = findViewById<View>(R.id.app_bar_picker)
        val toolbar = appBar.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
    }
}