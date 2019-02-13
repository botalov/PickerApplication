package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.presenter

import android.view.View
import com.botalov.imagepicker.Picker
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view.PickerBottomSheet
import com.botalov.imagepicker.control.bottom_sheet.presenter.PickerBottomSheetPresenter
import java.io.File

class PickerPresenter: PickerBottomSheetPresenter() {

    fun onClickImage(file: File){
        if(view is PickerBottomSheet){
            val isCorrectSize = Picker.getInstance().getImageMaxSizeInByte() > 0 && file.length() < Picker.getInstance().getImageMaxSizeInByte()

            if (isCorrectSize) {
                (view as PickerBottomSheet).sendImage(file)
            }
            else {
                (view as PickerBottomSheet).showImageSizeError()
            }
        }
    }
    fun onClickCameraPreview(startView: View){
        if(view is PickerBottomSheet){
            (view as PickerBottomSheet).showFullCamera(startView)
        }
    }
}