package com.botalov.imagepicker.control.camera.full_camera.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.view.View
import com.botalov.imagepicker.constants.F
import com.botalov.imagepicker.control.camera.full_camera.view.IFullCameraView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FullCameraPresenter: IFullCameraPresenter{
    private var view: IFullCameraView? = null
    private var context: Context? = null

    private var currentMode: String? = null
    private var currentCameraId: Int? = null

    private var camera: Camera? = null
    private val pictureCallback: Camera.PictureCallback? = getPictureCallback()

    private var size: Point? = null
    private var centerPreviewPoint: Point? = null

    override fun onCreated(startView: View?, activity: Activity) {
        if (startView == null) {
            return
        }

        if(camera == null) {
            initCamera()
        }

        val display = activity.windowManager?.defaultDisplay
        size = Point()
        display?.getSize(size)

        val location = IntArray(2)
        startView.getLocationInWindow(location)
        val sourceX = location[0]
        val sourceY = location[1]

        val window = activity.window

        val attrs = window!!.attributes
        val x = -size!!.x / 2 + sourceX + startView.width / 2
        val y = -size!!.y / 2 + sourceY + startView.height / 2
        attrs.x = x
        attrs.y = y

        window.attributes = attrs

        val locationInScreen = IntArray(2)
        startView.getLocationOnScreen(locationInScreen)

        centerPreviewPoint = Point(locationInScreen[0] + startView.width / 2, locationInScreen[1] + startView.height / 2)

        view?.showFullView(centerPreviewPoint!!, size!!)
        onChangeFlashMode()
    }

    override fun onPressButtonTakePhoto() {
        camera!!.autoFocus { success, camera ->
            if (success) {
                camera!!.takePicture(null, null, pictureCallback)
            }
        }
    }

    override fun onDonePhoto() {

    }

    override fun attachView(view: IFullCameraView, context: Context){
        this.view = view
        this.context = context
    }
    override fun detachView(){
        this.view = null
    }

    override fun onSwitchCamera() {
        when (currentCameraId) {
            Camera.CameraInfo.CAMERA_FACING_FRONT -> switchCameraObserver.onNext(Camera.CameraInfo.CAMERA_FACING_BACK)
            Camera.CameraInfo.CAMERA_FACING_BACK -> switchCameraObserver.onNext(Camera.CameraInfo.CAMERA_FACING_FRONT)
        }
    }

    override fun onChangeFlashMode() {
        val params = camera?.parameters
        currentMode = params?.flashMode

        if(currentMode == null) {
            if(params!!.supportedFlashModes == null || params.supportedFlashModes.size == 0) {
                view?.updateFlashButton(null)
                return
            }

            currentMode = params.supportedFlashModes[0]
        }

        when (currentMode) {
            Camera.Parameters.FLASH_MODE_AUTO -> changeFlashModeObserver.onNext(Camera.Parameters.FLASH_MODE_OFF)
            Camera.Parameters.FLASH_MODE_OFF -> changeFlashModeObserver.onNext(Camera.Parameters.FLASH_MODE_ON)
            Camera.Parameters.FLASH_MODE_ON -> changeFlashModeObserver.onNext(Camera.Parameters.FLASH_MODE_AUTO)
            else -> changeFlashModeObserver.onNext(Camera.Parameters.FLASH_MODE_AUTO)
        }
    }

    override fun onUpdatePreview(surface: SurfaceTexture?){
        if(camera == null) {
            initCamera()
        }
        try {
            camera?.setPreviewTexture(surface)
        } catch (t: IOException) {
        }
        camera?.setDisplayOrientation(90)
        camera?.startPreview()
    }

    override fun onDestroyCamera() {
        camera?.stopPreview()
        camera?.release()
    }

    private fun getPictureCallback(): Camera.PictureCallback {
        return Camera.PictureCallback { data, _ ->

            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val pathNewPhoto = saveImage(bitmap)
            if (pathNewPhoto != null) {
                view?.showPreview(pathNewPhoto)
                /*takePhotoObservable.onNext(pathNewPhoto!!)
                if (disposable != null && !disposable!!.isDisposed) {
                    disposable!!.dispose()
                }*/
            }


        }
    }

    private fun initCamera(){
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
        camera = Camera.open(currentCameraId!!)
        val params = camera!!.parameters
        val sizes = params.supportedPreviewSizes
        if(sizes != null && sizes.size > 0) {
            val cs = sizes[sizes.size - 1]
            params?.setPreviewSize(cs!!.width, cs.height)
        }
        params?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        camera!!.parameters = params
    }

    private fun saveImage(bitmap: Bitmap): File? {
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

            val mediaScannerIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val fileContentUri = Uri.fromFile(pictureFile)
            mediaScannerIntent.data = fileContentUri
            context?.sendBroadcast(mediaScannerIntent)

            fo.close()

            return pictureFile
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return null
    }

    private fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        val mtx = Matrix()
        mtx.setRotate(degree.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }

    private val changeFlashModeObserver: Observer<String> = object : Observer<String> {
        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
        }

        override fun onNext(mode: String) {
            val params = camera?.parameters
            val sizes = params?.supportedPreviewSizes
            if(sizes != null && sizes.size > 0) {
                val cs = sizes[sizes.size - 1]
                params.setPreviewSize(cs!!.width, cs.height)
            }
            params?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

            val supportedModes = params?.supportedFlashModes
            if (supportedModes != null && supportedModes.size > 0 && supportedModes.contains(mode)) {
                params.flashMode = mode
                view?.updateFlashButton(mode)
            }
            camera?.parameters = params
        }

        override fun onComplete() {

        }
    }

    private val switchCameraObserver: Observer<Int> = object : Observer<Int> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onNext(cameraId: Int) {
            currentCameraId = cameraId
            camera?.stopPreview();
            camera?.release()
            camera = Camera.open(currentCameraId!!)
            val params = camera!!.parameters
            val sizes = params.supportedPreviewSizes
            if(sizes != null && sizes.size > 0) {
                val cs = sizes[sizes.size - 1]
                params?.setPreviewSize(cs!!.width, cs.height)
            }
            if (params?.supportedFocusModes != null && params.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            }
            camera!!.parameters = params
            view?.updateSwitchCameraButton(cameraId)
        }

        override fun onError(e: Throwable) {

        }

    }
}