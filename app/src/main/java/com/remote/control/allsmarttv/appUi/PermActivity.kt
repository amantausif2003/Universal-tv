package com.remote.control.allsmarttv.appUi

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.remote.control.allsmarttv.databinding.ActivityPermissionBinding
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class PermActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mainBinding: ActivityPermissionBinding

    var permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        mainBinding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mainBinding.btnPerm.setOnClickListener { validation() }
    }

    private fun validation() {

        if (EasyPermissions.hasPermissions(this@PermActivity, *permission)) {
            Log.d("myPermission", "hasPermissions allow")
            startActivity(
                Intent(this@PermActivity, FirstActivity::class.java)
            )
            finish()
        } else {
            EasyPermissions.requestPermissions(
                this@PermActivity, "Please allow permissions to proceed further", 1010, *permission
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("myPermission", "onPermissionsGranted")
        if (requestCode == 1010) {
            if (perms.size == perms.size) {
                startActivity(
                    Intent(this@PermActivity, FirstActivity::class.java)
                )
                finish()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("myPermission", "not allow")
        if (EasyPermissions.somePermissionPermanentlyDenied(this@PermActivity, perms)) {
            AppSettingsDialog.Builder(this@PermActivity).build().show()
        }
    }
}