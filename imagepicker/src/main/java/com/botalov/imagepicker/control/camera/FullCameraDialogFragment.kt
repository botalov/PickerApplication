package com.botalov.imagepicker.control.camera

import android.app.Dialog
import android.graphics.*
import android.hardware.Camera
import android.os.Bundle
import android.view.*
import com.botalov.imagepicker.utils.AnimationUtils
import android.view.ViewGroup
import android.widget.ImageButton
import android.os.Environment
import com.botalov.imagepicker.constants.F
import java.io.ByteArrayOutputStream
import java.io.File
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import java.io.FileOutputStream
import java.io.IOException
import android.media.ExifInterface
import com.botalov.imagepicker.R
import io.reactivex.Observer
import android.content.Intent
import android.net.Uri
import com.botalov.imagepicker.constants.F.Constants.CAMERA_OPEN_CLODE_DURATION


class FullCameraDialogFragment : androidx.fragment.app.DialogFragment(), TextureView.SurfaceTextureListener {
    private var startView: View? = null
    private var textureView: TextureView? = null
    private var centerPreviewPoint: Point? = null
    private var size: Point? = null
    private var camera: Camera? = null
    private val pictureCallback: Camera.PictureCallback? = getPictureCallback()
    private var pathNewPhoto: File? = null
    private val takePhotoObservable = BehaviorSubject.create<File>()
    private var disposable: Disposable? = null

    private var flashImageButton: ImageButton? = null
    private val changeFlashMode: Observer<String> = object: Observer<String>{
        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
        }

        override fun onNext(mode: String) {
            val params = camera?.parameters
            params?.flashMode = mode
            val sizes = params?.supportedPreviewSizes
            val cs = sizes?.get(0)
            params?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            params?.setPreviewSize(cs!!.width, cs.height)
            camera?.parameters = params

            when(mode) {
                Camera.Parameters.FLASH_MODE_ON -> flashImageButton?.setImageResource(R.drawable.ic_flash_on)
                Camera.Parameters.FLASH_MODE_AUTO -> flashImageButton?.setImageResource(R.drawable.ic_flash_auto)
                Camera.Parameters.FLASH_MODE_OFF -> flashImageButton?.setImageResource(R.drawable.ic_flash_off)
            }
        }

        override fun onComplete() {

        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        camera?.stopPreview()
        camera?.release()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        try {
            camera?.setPreviewTexture(surface)
        } catch (t: IOException) {
        }
        camera?.setDisplayOrientation(90)
        camera?.startPreview()
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
        val hideObserver: Observer<Boolean> = object: Observer<Boolean>{
            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
            }

            override fun onNext(data: Boolean) {
                if(data) {
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

        calculateAndSetDialogPosition()
        initShutter(view)
    }

    private fun initShutter(view: View) {
        val shutter = view.findViewById<ImageButton>(R.id.shutter_image_button)
        shutter.setOnClickListener {
            //camera!!.takePicture(null, null, pictureCallback)
            camera!!.autoFocus { success, camera ->
                if (success) {
                    camera!!.takePicture(null, null, pictureCallback)
                }
            }
        }
    }

    private fun setStartView(view: View) {
        this.startView = view
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(com.botalov.imagepicker.R.layout.camera_full_view_layout, container, false)
        textureView = mainView.findViewById(com.botalov.imagepicker.R.id.texture_view)
        textureView?.surfaceTextureListener = this

        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        val params = camera!!.parameters
        val sizes = params.supportedPreviewSizes
        val cs = sizes.get(0)
        params?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        params?.setPreviewSize(cs!!.width, cs.height)
        camera!!.parameters = params

        flashImageButton = mainView.findViewById(R.id.flash_image_button)
        flashImageButton?.setOnClickListener { v -> changeFlash() }

        return mainView
    }

    private fun calculateAndSetDialogPosition() {
        if (startView == null) {
            return
        }

        val display = activity?.windowManager?.defaultDisplay
        size = Point()
        display?.getSize(size)

        val location = IntArray(2)
        startView?.getLocationInWindow(location)
        val sourceX = location[0]
        val sourceY = location[1]

        val window = dialog?.window

        val attrs = window!!.attributes
        val x = -size!!.x / 2 + sourceX + startView!!.width / 2
        val y = -size!!.y / 2 + sourceY + startView!!.height / 2
        attrs.x = x
        attrs.y = y

        window.attributes = attrs

        val locationInScreen = IntArray(2)
        startView?.getLocationOnScreen(locationInScreen)

        centerPreviewPoint = Point(locationInScreen!![0] + startView!!.width / 2, locationInScreen!![1] + startView!!.height / 2)
        textureView?.post {
            AnimationUtils.circleShowAnimationView(
                textureView!!,
                CAMERA_OPEN_CLODE_DURATION,
                centerPreviewPoint!!,
                size!!,
                null
            )
        }
    }

    private fun getPictureCallback(): Camera.PictureCallback {
        return Camera.PictureCallback { data, _ ->

            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            pathNewPhoto = saveImage(bitmap)
            if (pathNewPhoto != null) {
                takePhotoObservable.onNext(pathNewPhoto!!)
                if (disposable != null && !disposable!!.isDisposed) {
                    disposable!!.dispose()
                }
            }

            dismiss()

        }
    }

    private fun changeFlash(){
        val cameraParams = camera?.parameters
        val currentMode = cameraParams?.flashMode
        when(currentMode){
            Camera.Parameters.FLASH_MODE_AUTO -> changeFlashMode.onNext(Camera.Parameters.FLASH_MODE_OFF)
            Camera.Parameters.FLASH_MODE_OFF -> changeFlashMode.onNext(Camera.Parameters.FLASH_MODE_ON)
            Camera.Parameters.FLASH_MODE_ON -> changeFlashMode.onNext(Camera.Parameters.FLASH_MODE_AUTO)
            else -> changeFlashMode.onNext(Camera.Parameters.FLASH_MODE_AUTO)
        }
    }

    private fun saveImage(bitmap: Bitmap) : File? {
        val bytes = ByteArrayOutputStream()

        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().path + "/" + F.Constants.IMAGE_DIRECTORY
        )

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            val pictureFile = File(
                wallpaperDirectory, System.currentTimeMillis().toString() + ".jpg"
            )
            pictureFile.createNewFile()


            var bitMapRotate = bitmap
            val exif = ExifInterface(pictureFile.toString())
            if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true)) {
                bitMapRotate = rotate(bitMapRotate, 90)
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("8", ignoreCase = true)) {
                bitMapRotate = rotate(bitMapRotate, 270)
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("3", ignoreCase = true)) {
                bitMapRotate = rotate(bitMapRotate, 180)
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("0", ignoreCase = true)) {
                bitMapRotate = rotate(bitMapRotate, 90)
            }
            bitMapRotate.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

            val fo = FileOutputStream(pictureFile)
            fo.write(bytes.toByteArray())
            //fo.write();
            /*MediaScannerConnection.scanFile(
                this.context,
                arrayOf(pictureFile.path),
                arrayOf("image/jpeg"), null
            )*/

            val mediaScannerIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val fileContentUri = Uri.fromFile(pictureFile)
            mediaScannerIntent.data = fileContentUri
            this.activity?.sendBroadcast(mediaScannerIntent)

            fo.close()

            return pictureFile
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return null
    }

    fun setSubscribe(consumer: Consumer<File>) {
        disposable = takePhotoObservable.subscribe(consumer)
    }

    private fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        val mtx = Matrix()
        mtx.setRotate(degree.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }
}