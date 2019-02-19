package com.botalov.imagepicker.control.camera.preview_photo.presenter

import com.botalov.imagepicker.control.camera.preview_photo.view.IPreviewView
import java.io.File

class PreviewPresenter: IPreviewPresenter {
    private var view: IPreviewView? = null
    override fun attachView(view: IPreviewView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onOpenImage(file: File) {

    }

    override fun onBack() {

    }

    override fun onSend() {

    }
}