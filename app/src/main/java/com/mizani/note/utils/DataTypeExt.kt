package com.mizani.note.utils

object DataTypeExt {

    fun Int?.orZero(): Int {
        return this?:0
    }

    fun Long?.orZero(): Long {
        return this?:0
    }

    fun Boolean?.orFalse(): Boolean {
        return this?:false
    }

}