package com.mizani.note.utils

import android.app.AlertDialog
import android.content.Context

object DialogUtils {

    fun showConfirmationDialog(
        context: Context,
        title: String,
        description: String = "",
        positiveAction: () -> Unit,
        negativeAction: () -> Unit = {}
    ) {
        AlertDialog
            .Builder(context)
            .setTitle(title)
            .setMessage(description.orEmpty())
            .setPositiveButton("Yes") { _, _ ->
                positiveAction.invoke()
            }.setNegativeButton("No") { _, _ -> negativeAction.invoke() }
            .show()
    }

}