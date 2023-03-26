package com.mizani.note.utils

import android.view.View

object ViewExt {

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.goneIf(value: Boolean) {
        if (value) {
            this.visibility = View.GONE
            return
        }
        this.visibility = View.VISIBLE
    }

    fun View.visibleIf(value: Boolean) {
        if (value) {
            this.visibility = View.VISIBLE
            return
        }
        this.visibility = View.GONE
    }

    fun View.disableIf(value: Boolean) {
        if (value) {
            this.isEnabled = false
            return
        }
        this.isEnabled = true
    }

    fun View.enableIf(value: Boolean) {
        if (value) {
            this.isEnabled = true
            return
        }
        this.isEnabled = false
    }


}