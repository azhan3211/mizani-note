package com.mizani.note.utils

import android.widget.EditText

object InputValidation {

    fun EditText.throwWhenEmpty() {
        this.error = null
        if (this.text.toString().isEmpty()) {
            this.error = "Cannot be empty"
            throw IllegalArgumentException()
        }
    }

}