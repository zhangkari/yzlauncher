package com.yz.books

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yz.books.common.Constant
import com.yz.books.ext.showToast
import com.yz.books.ext.startToActivity
import com.yz.books.ui.main.MainActivity
import com.yz.books.utils.LogUtils
import com.yz.books.utils.sp.PreferencesUtils
import com.yz.books.utils.sp.SpConstant
import com.zhou.zpermission.annotation.PermissionDenied
import com.zhou.zpermission.annotation.PermissionDeniedForever
import com.zhou.zpermission.annotation.PermissionNeed

/**
 * 启动页
 *
 * @author lilin
 * @time on 2020-02-22 23:11
 */
class StartActivity : AppCompatActivity() {
//region var/val

//endregion

//region implement methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE//横屏
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContentView(R.layout.activity_start)

        getMachineCode()

        //checkPermission()
        applyInstallPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            applyInstallPermission()
        }
    }

//endregion

//region public methods

//endregion

//region private methods
    /**
     * 获取机器码
     */
    private fun getMachineCode() {
        val machineCode = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        PreferencesUtils.putSpValue(SpConstant.MACHINE_CODE, machineCode)
    }

    /*@PermissionNeed(
        permissions = [
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE],
        requestCode = 1)*/
    private fun startMainActivity() {
        LogUtils.e("权限获取成功")
        startToActivity<MainActivity>()
        finish()
    }

    @PermissionDenied
    private fun denied(requestCode: Int) {
        showToast("权限被拒绝")
    }

    @PermissionDeniedForever
    private fun deniedForever(requestCode: Int) {
        showToast("权限被永久拒绝")
    }

    private fun checkPermission() {
        val perArray = arrayOf(
            Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
            Permission.READ_PHONE_STATE)

        if (AndPermission.hasPermissions(this, perArray)) {
            startMainActivity()
        } else {
            AndPermission.with(this)
                .runtime()
                .permission(perArray)
                //.rationale(RuntimeRationale())
                .onGranted {
                    startMainActivity()
                }
                .onDenied {
                    showToast("权限被拒绝")
                    finish()
                }
                .start()
        }
    }

    private fun applyInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val requestPackageInstalls = packageManager.canRequestPackageInstalls()
            if (requestPackageInstalls) {
                checkPermission()
                return
            }
            showToast("请开启安装应用所需的权限！")
            val uri = Uri.parse("package:${packageName}")
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
            startActivityForResult(intent, 100)
        } else {
            checkPermission()
        }
    }

//endregion
}