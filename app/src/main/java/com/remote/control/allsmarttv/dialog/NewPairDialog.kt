package com.remote.control.allsmarttv.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.remote.control.allsmarttv.R

class NewPairDialog(var activity: Activity, var callBack: NewDeleteDialogCallback) {

    private var customDialog: AlertDialog = AlertDialog.Builder(activity).create()

    init {
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.text_inpu_password, null)
        if (customDialog.window != null) {
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
//        customDialog.setCancelable(false)
        customDialog.setView(view)

        view.findViewById<View>(R.id.yesPair).setOnClickListener {
            callBack.yesPair(view.findViewById<EditText>(R.id.txt_pair).text.toString())
            hideDialog()

        }

        view.findViewById<View>(R.id.noPair).setOnClickListener {
            callBack.noPair()
            hideDialog()
        }

        /* noDelete.setOnClickListener {
             hideDialog()
         }

         yesDelete.setOnClickListener {
             callBack.deleteObjectCallBack()
             hideDialog()
         }*/

    }

    fun showDialog() {
        customDialog.show()
    }

    private fun hideDialog() {
        customDialog.dismiss()
    }

    interface NewDeleteDialogCallback {
        fun yesPair(string: String?)
        fun noPair()
    }
}