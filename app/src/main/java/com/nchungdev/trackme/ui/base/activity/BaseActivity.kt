package com.nchungdev.trackme.ui.base.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nchungdev.data.util.Util
import com.nchungdev.trackme.R
import com.nchungdev.trackme.ui.base.PermissionRequestable
import com.nchungdev.trackme.ui.util.PermissionUtils

abstract class BaseActivity : AppCompatActivity(), PermissionRequestable {
    private var permissionsResultRequestable: PermissionRequestable.Callback? = null

    @MenuRes
    protected open fun getMenuResId(): Int = 0

    @LayoutRes
    protected open fun getLayoutResId(): Int = 0

    @Nullable
    protected var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDagger()
        super.onCreate(savedInstanceState)
        inflateLayout(savedInstanceState)
    }

    protected fun initToolbar(toolbar: Toolbar) {
        this.toolbar = toolbar
        setSupportActionBar(this.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    protected open fun injectDagger() = Unit

    protected open fun inflateLayout(savedInstanceState: Bundle?) {
        setContentView(getLayoutResId())
        inits(savedInstanceState)
    }

    protected open fun inits(savedInstanceState: Bundle?) = Unit

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (getMenuResId() != 0) {
            menuInflater.inflate(getMenuResId(), menu)
        }
        return super.onCreateOptionsMenu(menu);
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
                permissionsResultRequestable = callback
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
            permissionsResultRequestable = callback
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
        permissionsResultRequestable?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            true
        )
    }

    companion object {
        const val PERMISSION_REQUEST_CODE: Int = 1234
    }
}