package com.yz.books.ui.manager

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
import android.view.View
import com.yz.books.BuildConfig
import com.yz.books.R
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.ext.*
import com.yz.books.ui.downloadlist.DownloadListActivity
import com.yz.books.ui.main.MainModel
import com.yz.books.ui.main.bean.AppUpdateBean
import com.yz.books.utils.*
import com.yz.books.widget.dialog.AppUpdateDialog
import com.yz.books.widget.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_manager.*
import java.io.File

/**
 * 管理类型
 *
 * @author lilin
 * @time on 2020/4/25 下午8:46
 */
class ManagerActivity : BaseMVVMActivity<ManagerViewModel>() {

//region var/val

    private val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    private val mAppUpdateDialog by lazy(LazyThreadSafetyMode.NONE) {
        AppUpdateDialog(this) {
            if (it) {
                downloadApp(mAppUrl)
            }
        }
    }

    /**
     * app下载url
     */
    private var mAppUrl: String? = null

    private var mFileMd5String: String? = null

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        hideSoftInput()
        mAppUpdateDialog.dismissDialog()
        mProgressDialog.dismissDialog()
    }

    override fun providerVMClass() = ManagerViewModel()

    override fun getLayoutId() = R.layout.activity_manager

    override fun initView() {
        initKeyboardView()
    }

    override fun initData() {
        tv_res_path.text = "存储路径：${FileUtils.getUsableStoragePath()}"

        tv_check_app_update.text = "当前版本：${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"

        val machineCode = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        tv_machine_code.text = "机器码：$machineCode"
    }

    override fun initListener() {
        super.initListener()
        cl_root.setOnClickListener {
            hideSoftInput()
        }

        btn_back.setOnClickListener { finish() }

        tv_setting.setOnClickListener {
            goToSettingPage()
        }

        tv_check_app_update.setOnClickListener {
            checkAppUpdate()
        }

        tv_clear_data.setOnClickListener {
            val result = FileUtils.deleteDirectory(FileUtils.getUsableStoragePath()+"/yzbooks")
            if (result) {
                showToast("清除成功")
            }
        }

        tv_download_list.setOnClickListener {
            startToActivity<DownloadListActivity>()
        }

        btn_sure.setOnClickListener {
            if (et_pwd.textContent != "yuezhi") {
                showToast("密码不正确,请重新输入")
                return@setOnClickListener
            }
            et_pwd.clearFocus()
            hideSoftInput()
            group_pwd.visibility = View.GONE
            group_setting.visibility = View.VISIBLE
        }
    }

    override fun observerUI(state: State) {
        when(state) {
            is LoadingState -> {
                showLoading()
            }
            is LoadedState -> {
                dismissLoading()
            }
            is ErrorState -> {
                showToast(state.errorMsg)
            }

            is ManagerModel.AppUpdateState -> {
                showAppUpdateInfo(state.appUpdateBean)
            }
        }
    }

    override fun observerForever() = false

//endregion

//region public methods

//endregion

//region private methods

    private fun goToSettingPage() {
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    private fun checkAppUpdate() {
        mViewModel?.checkAppUpdate()
    }

    private fun showAppUpdateInfo(appUpdateBean: AppUpdateBean?) {
        if (appUpdateBean == null || BuildConfig.VERSION_CODE >= appUpdateBean.versionCode) {
            showToast("当前已是最新版本！")
            return
        }

        mAppUrl = appUpdateBean.downUrl
        mFileMd5String = appUpdateBean.fileMd5String

        mAppUpdateDialog.show()
        mAppUpdateDialog.showUpdateDesc(appUpdateBean.updateInfo)
    }

    private fun downloadApp(appUrl: String?) {
        if (appUrl == null) return

        val fileName = appUrl.substring(appUrl.lastIndexOf("/") + 1)
        val file = FileUtils.createTempFile(fileName)
        DownloadUtils.download(appUrl.addFileHostUrl(), file.path,
            object : DownloadUtils.OnDownloadListener {
                override fun onDownloading(progress: Int) {
                    runOnUiThread {
                        with(mProgressDialog) {
                            if (!isShowing) {
                                setTitle("提示")
                                setMessage("正在下载App...请耐心等待")
                                isIndeterminate = false
                                max = 100
                                setCancelable(false)
                                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                                setButton(DialogInterface.BUTTON_NEGATIVE, "取消下载") { dialog, which ->
                                    DownloadUtils.mCancelDownload = true
                                    FileUtils.deleteFile(file.path)
                                }
                                show()
                            }
                            setProgress(progress)
                        }
                    }
                }

                override fun onDownloadFailed(msg: String?) {
                    LogUtils.e("onDownloadFailed==$msg")
                    runOnUiThread {
                        mProgressDialog.dismissDialog()
                        showToast("下载失败，请重新下载！")
                    }
                }

                override fun onDownloadSuccess(path: String?) {
                    runOnUiThread {
                        mProgressDialog.dismissDialog()
                        path?.let {
                            val appFile = File(it)
                            if (FileMD5Utils.getFileMD5(appFile) == mFileMd5String) {
                                installApk(appFile)
                            } else {
                                showToast("下载失败，请重新下载！")
                            }
                        }
                    }
                }
            })
    }

    private fun installApk(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val installAllowed = packageManager.canRequestPackageInstalls()
            if (installAllowed) {
                //权限许可，安装应用
                openFile(file)
            } else {
                val intent = Intent(ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        } else {
            openFile(file)
        }
    }

    private fun initKeyboardView() {
        /*

        val pinyin26KB = Keyboard(this, R.xml.pinyin_26) // 字母键盘
        val numberKB = Keyboard(this, R.xml.number) // 数字键盘

        view_keyboard.isPreviewEnabled = false
        view_keyboard.keyboard = pinyin26KB
        view_keyboard.setOnKeyboardActionListener(object : KeyboardView.OnKeyboardActionListener {
            override fun swipeRight() {

            }

            override fun onPress(primaryCode: Int) {

            }

            override fun onRelease(primaryCode: Int) {

            }

            override fun swipeLeft() {

            }

            override fun swipeUp() {

            }

            override fun swipeDown() {

            }

            override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
                val editable = et_pwd.text
                val start = et_pwd.selectionStart

                when(primaryCode) {
                    Keyboard.KEYCODE_SHIFT -> { // 设置shift状态然后刷新页面
                        pinyin26KB.isShifted = !pinyin26KB.isShifted
                        view_keyboard.invalidateAllKeys()
                    }
                    Keyboard.KEYCODE_DELETE -> { // 点击删除键，长按连续删除
                        if (editable != null && editable.isNotEmpty() && start > 0) {
                            editable.delete(start - 1, start)
                        }
                    }
                    -10 -> { // 自定义code，切换到拼音键盘
                        view_keyboard.keyboard = pinyin26KB
                    }
                    -11 -> { // 自定义code，切换到数字键盘
                        view_keyboard.keyboard = numberKB
                    }

                    else -> {
                        if (primaryCode >= 97 && primaryCode <= 97 + 26) {// 按下字母键
                            val text = if (pinyin26KB.isShifted) {
                                (primaryCode - 32).toChar().toString()
                            } else {
                                primaryCode.toChar().toString()
                            }
                            editable?.insert(start, text)
                        } else {// 其他code值，转字符在输入框中显示
                            editable?.insert(start, primaryCode.toChar().toString())
                        }
                    }
                }
            }

            override fun onText(text: CharSequence?) {

            }

        })

        */

    }

//endregion
}