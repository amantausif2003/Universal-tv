package com.remote.control.allsmarttv.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.remote.control.allsmarttv.R

class ConnectivityDialog(var activity: Activity) {

    private var customDialog: AlertDialog = AlertDialog.Builder(activity).create()

    init {

        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.connectivity_text_dialog, null)
        if (customDialog.window != null) {
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        customDialog.setView(view)

        view.findViewById<View>(R.id.cardOk).setOnClickListener {
            hideDialog()
        }

    }

    fun showDialog() {
        customDialog.show()
    }

    private fun hideDialog() {
        customDialog.dismiss()
    }
}