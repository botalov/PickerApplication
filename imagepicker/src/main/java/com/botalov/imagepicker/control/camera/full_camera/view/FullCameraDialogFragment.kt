package com.botalov.imagepicker.control.camera.full_camera.view

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.*
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.botalov.imagepicker.utils.AnimationUtils
import android.view.ViewGroup
import android.widget.ImageButton
import java.io.File
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import android.os.Build
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.botalov.imagepicker.R
import com.botalov.imagepicker.constants.F
import io.reactivex.Observer
import com.botalov.imagepicker.constants.F.Constants.CAMERA_OPEN_CLODE_DURATION
import com.botalov.imagepicker.control.camera.full_camera.presenter.FullCameraPresenter
import com.botalov.imagepicker.control.camera.full_camera.presenter.IFullCameraPresenter
import com.github.chrisbanes.photoview.PhotoView


class FullCameraDialogFragment : androidx.fragment.app.DialogFragment(), TextureView.SurfaceTextureListener, IFullCameraView {


    private val presenter: IFullCameraPresenter = FullCameraPresenter()

    private var startView: View? = null
    var textureView: TextureView? = null
    private var centerPreviewPoint: Point? = null
    private var size: Point? = null

    private val takePhotoObservable = BehaviorSubject.create<File>()
    private var disposable: Disposable? = null

    private var shutter: ImageButton? = null

    private var flashImageButton: ImageButton? = null

    private var surfacePreview: SurfaceTexture? = null
    private var switchCameraImageButton: ImageButton? = null

    private var fullPreview: ConstraintLayout? = null
    private var previewImageView: PhotoView? = null
    private var previewBackButton: ImageButton? = null
    private var previewSendButton: ImageButton? = null

    private var previewFile: File? = null

    override fun updateFlashButton(modeFlash: String?) {
        if(modeFlash == null) {
            flashImageButton?.visibility = View.GONE
            return
        }

        when (modeFlash) {
            Camera.Parameters.FLASH_MODE_ON -> flashImageButton?.setImageResource(R.drawable.ic_flash_on)
            Camera.Parameters.FLASH_MODE_AUTO -> flashImageButton?.setImageResource(R.drawable.ic_flash_auto)
            Camera.Parameters.FLASH_MODE_OFF -> flashImageButton?.setImageResource(R.drawable.ic_flash_off)
        }
    }

    override fun updateSwitchCameraButton(camera: Int?) {
        presenter.onUpdatePreview(surfacePreview)
        when (camera!!) {
            Camera.CameraInfo.CAMERA_FACING_FRONT -> switchCameraImageButton!!.setImageResource(R.drawable.ic_camera_front)
            Camera.CameraInfo.CAMERA_FACING_BACK -> switchCameraImageButton!!.setImageResource(R.drawable.ic_camera_rear)
        }
    }

    override fun showError(message: String) {

    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        presenter.onDestroyCamera()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        surfacePreview = surface
        presenter.onUpdatePreview(surfacePreview)
    }

    companion object {
        fun getNewInstance(parentView: View): FullCameraDialogFragment {
            val dialog = FullCameraDialogFragment()
            dialog.setStartView(parentView)
            dialog.setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_FRAME, R.style.CameraDialogStyle)

            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hideObserver: Observer<Boolean> = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
            }

            override fun onNext(data: Boolean) {
                if (data) {
                    dismiss()
                }
            }

            override fun onComplete() {

            }
        }
        val d = super.onCreateDialog(savedInstanceState)
        d.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                if (centerPreviewPoint != null && textureView != null && size != null) {
                    AnimationUtils.circleHideAnimationView(
                        textureView!!,
                        CAMERA_OPEN_CLODE_DURATION,
                        centerPreviewPoint!!,
                        size!!,
                        hideObserver
                    )
                } else {
                    dialog.dismiss()
                }
                true
            } else {
                false
            }
        }
        return d
    }

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shutter = view.findViewById<ImageButton>(R.id.shutter_inner_image_button)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shutter?.elevation = 8f
        }

        presenter.attachView(this, activity!!)
        presenter.onCreated(startView, activity!!)
        shutter?.setOnClickListener {
            presenter.onPressButtonTakePhoto()
        }
    }

    override fun showFullView(centerPreviewPoint: Point, size: Point){
        textureView?.post {
            AnimationUtils.circleShowAnimationView(
                textureView!!,
                F.Constants.CAMERA_OPEN_CLODE_DURATION,
                centerPreviewPoint,
                size,
                null
            )
        }
    }
    override fun hideFullView(){

    }

    override fun showPreview(file: File){
        previewFile = file
        shutter?.visibility = View.GONE
        fullPreview?.visibility = View.VISIBLE
        previewImageView?.setImageURI(Uri.fromFile(file))
        //textureView?.visibility = View.GONE
    }


    private fun setStartView(view: View) {
        this.startView = view
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(com.botalov.imagepicker.R.layout.camera_full_view_layout, container, false)
        textureView = mainView.findViewById(com.botalov.imagepicker.R.id.texture_view)
        textureView?.surfaceTextureListener = this

        flashImageButton = mainView.findViewById(R.id.flash_image_button)
        flashImageButton?.setOnClickListener { presenter.onChangeFlashMode() }

        switchCameraImageButton = mainView.findViewById(R.id.camera_switch_image_button)
        switchCameraImageButton?.setOnClickListener { presenter.onSwitchCamera() }


        fullPreview = mainView.findViewById(R.id.full_preview_container)
        fullPreview?.visibility = View.GONE
        previewImageView = mainView.findViewById(R.id.full_preview_image_view)
        previewBackButton = mainView.findViewById(R.id.back_preview_button)
        previewBackButton?.setOnClickListener {
            run {
                fullPreview?.visibility = View.GONE
                presenter.onUpdatePreview(surfacePreview)
                shutter?.visibility = View.VISIBLE
            }
        }
        previewSendButton = mainView.findViewById(R.id.send_preview_button)
        previewSendButton?.setOnClickListener {
            run {
                takePhotoObservable.onNext(previewFile!!)
                dismiss()
            }
        }

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        return mainView
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }


    fun setSubscribe(consumer: Consumer<File>) {
        disposable = takePhotoObservable.subscribe(consumer)
    }


}