package com.botalov.testpickerapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.botalov.imagepicker.Picker
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val picker = Picker.getInstance()
    private val selectObserver: Observer<File> = object: Observer<File>{
        override fun onSubscribe(d: Disposable) {
            //disposable = d
        }

        override fun onError(e: Throwable) {
        }

        override fun onNext(data: File) {
            selected_image_count_text_view.text = data.path
        }

        override fun onComplete() {

        }
    }

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        picker.setFinishSelectObserver(selectObserver)
        //picker.setAccentColor(resources.getColor(R.color.colorAccent))
        //picker.setStartHeightPicker(500)

        open_picker_image_button.setOnClickListener { picker.show(this) }
    }
}
