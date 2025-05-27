package com.pokellect.mobilefrontend

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window

object DialogUtils {
    private var dialog: Dialog? = null

    fun show(context: Context) {
        if (dialog?.isShowing == true) return

        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(LayoutInflater.from(context).inflate(R.layout.loading_dialog, null))
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    fun hide() {
        dialog?.dismiss()
        dialog = null
    }
}