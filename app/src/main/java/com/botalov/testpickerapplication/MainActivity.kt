package com.botalov.testpickerapplication

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.botalov.imagepicker.Picker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val picker = Picker()
    private val selectObserver = Observer<Int> { it->
        selected_image_count_text_view.text = it.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        picker.addSelectImageObserver(selectObserver)

        open_picker_image_button.setOnClickListener { openPicker() }
    }


    private fun openPicker() {
        picker.show(this)
    }
}
