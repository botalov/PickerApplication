package com.botalov.imagepicker.control.bottom_sheet.presenter

import com.botalov.imagepicker.control.bottom_sheet.view.BaseBottomSheetActivity

open class PickerBottomSheetPresenter {
    var view: BaseBottomSheetActivity? = null
    fun attachView(view: BaseBottomSheetActivity){
        this.view = view
    }
    fun detachView(){
        this.view = null
    }
}