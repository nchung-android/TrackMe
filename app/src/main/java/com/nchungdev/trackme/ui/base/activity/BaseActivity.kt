package com.nchungdev.trackme.ui.base.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nchungdev.data.util.Util
import com.nchungdev.trackme.R
import com.nchungdev.trackme.ui.base.PermissionRequestable
import com.nchungdev.trackme.util.PermissionUtils

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity(), PermissionRequestable {
    private var permissionCallback: PermissionRequestable.Callback? = null

    private var binding: B? = null

    abstract fun initViewBinding(): B

    abstract fun onBind(binding: B, savedInstanceState: Bundle?)

    protected open fun injectDagger() = Unit

    @StringRes
    protected open fun getTitleResId() = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDagger()
        super.onCreate(savedInstanceState)
        binding = initViewBinding()
        setContentView(binding?.root)
        initToolbar(findViewById(R.id.toolbar))
        onBind(viewBinding(), savedInstanceState)
    }

    protected open fun initToolbar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(getTitleResId())
        }
    }

    protected fun showToast(@StringRes stringResId: Int) {
        Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    protected open fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, fragment)
            .commit()
    }

    override fun checkPermissions(permissions: Array<String>): Array<String> {
        return PermissionUtils.checkPermissions(this, permissions)
    }

    override fun requestPermissions(
        permissions: Array<String>,
        callback: PermissionRequestable.Callback,
    ) {
        if (Util.hasM()) {
            val notGrantedPermissions = checkPermissions(permissions)
            if (notGrantedPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    notGrantedPermissions,
                    PERMISSION_REQUEST_CODE
                )
                permissionCallback = callback
            } else {
                val ret = IntArray(permissions.size)
                ret.indices.forEach { i ->
                    ret[i] = PackageManager.PERMISSION_GRANTED
                }
                callback.onRequestPermissionsResult(
                    PERMISSION_REQUEST_CODE,
                    permissions,
                    ret,
                    false
                )
            }
        } else {
            val ret = IntArray(permissions.size)
            ret.indices.forEach { i ->
                ret[i] = PackageManager.PERMISSION_GRANTED
            }
            callback.onRequestPermissionsResult(PERMISSION_REQUEST_CODE, permissions, ret, false)
        }
    }

    override fun requestPermissions(permission: String, callback: PermissionRequestable.Callback) {
        if (Util.hasM() && isPermissionGranted(permission)) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            permissionCallback = callback
        } else {
            callback.onRequestPermissionsResult(
                PERMISSION_REQUEST_CODE,
                arrayOf(permission),
                intArrayOf(PackageManager.PERMISSION_GRANTED),
                false
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionCallback?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            true
        )
    }

    override fun onDestroy() {
        if (binding != null) {
            binding = null
        }
        super.onDestroy()
    }

    private fun viewBinding(): B {
        if (binding == null) {
            throw Exception("Not init layout")
        }
        return binding as B
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                try {
                    onBackPressed()
                } catch (e: IllegalStateException) {
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    companion object {
        const val PERMISSION_REQUEST_CODE: Int = 1234
    }
}