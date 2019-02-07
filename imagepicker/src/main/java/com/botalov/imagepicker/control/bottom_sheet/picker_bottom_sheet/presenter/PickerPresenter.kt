package com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.presenter

import com.botalov.imagepicker.constants.F.Constants.MAX_FILE_SIZE
import com.botalov.imagepicker.control.bottom_sheet.picker_bottom_sheet.view.PickerBottomSheet
import com.botalov.imagepicker.control.bottom_sheet.presenter.PickerBottomSheetPresenter
import java.io.File

class PickerPresenter: PickerBottomSheetPresenter() {

    fun onClickImage(file: File){
        if(view is PickerBottomSheet){
            if (file.length() > MAX_FILE_SIZE) {
                (view as PickerBottomSheet).showImageSizeError()
            }
            else {
                (view as PickerBottomSheet).sendImage(file)
            }
        }
    }
    fun onClickCameraPreview(){

    }
}