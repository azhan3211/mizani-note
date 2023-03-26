package com.mizani.note.utils

import android.os.Build
import android.os.Bundle
import java.io.Serializable

object BundleExt {

    fun <T: Serializable>Bundle.getSerializableData(key: String, clazz: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializable(key, clazz)?:clazz.newInstance()
        } else {
            this.getSerializable(key) as T
        }
    }

}