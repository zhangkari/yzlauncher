package com.yz.books.ui.main

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.collection.ArrayMap
import com.yz.books.BuildConfig
import com.yz.books.R
import com.yz.books.adapter.BannerAdapter
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoManager
import com.yz.books.ext.*
import com.yz.books.ui.activities.ActivityListActivity
import com.yz.books.ui.audio.AudioBooksActivity
import com.yz.books.ui.downloadlist.DownloadListActivity
import com.yz.books.ui.h5.H5Activity
import com.yz.books.ui.journal.JournalBooksActivity
import com.yz.books.ui.main.bean.*
import com.yz.books.ui.manager.ManagerActivity
import com.yz.books.ui.massive.MassiveBooksActivity
import com.yz.books.ui.notice.NoticeActivity
import com.yz.books.ui.thematic.ThematicActivity
import com.yz.books.ui.video.VideoBooksActivity
import com.yz.books.utils.*
import com.yz.books.utils.sp.PreferencesUtils
import com.yz.books.utils.sp.SpConstant
import com.yz.books.widget.dialog.AppUpdateDialog
import com.yz.books.widget.dialog.LoadingDialog
import com.yz.books.widget.pop.MenuPopupWindow
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException


/**
 * 主界面
 */
class MainActivity : BaseMVVMActivity<MainViewModel>() {

    private val mMenuPopupWindow by lazy(LazyThreadSafetyMode.NONE) {
        MenuPopupWindow(this)
    }

    private val mBannerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BannerAdapter(banner_view)
    }

    private val mHandler by lazy(LazyThreadSafetyMode.NONE) {
        CommonHandler(this, Handler.Callback { false })
    }

    private val mTimerRunnable = object : Runnable {
        override fun run() {
            tv_current_time.text = "${TimeUtils.getCurrentTime(
                TimeUtils.PATTERN_HMS,
                System.currentTimeMillis()
            )}"
            mHandler.postDelayed(this, 1000)
        }
    }

    private val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    private val mAppUpdateDialog by lazy(LazyThreadSafetyMode.NONE) {
        AppUpdateDialog(this) {
            if (it) {
                downloadApp(mAppUrl)
            } else {
                checkMachineCode()
            }
        }
    }

    private val mMachineCode: String
        get() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    /**
     * app下载url
     */
    private var mAppUrl: String? = null

    private var mFileMd5String: String? = null

    private var mDownloadResourceUpdate = false

    private var mIsFirstOpen = true

    //存储时间的数组
    private val mHits = LongArray(3)

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK ||
            // keyCode == KeyEvent.KEYCODE_HOME ||
            keyCode == KeyEvent.KEYCODE_MENU
        ) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mIsFirstOpen = true
        mMenuPopupWindow.dismiss()
        mProgressDialog.dismissDialog()
        mAppUpdateDialog.dismissDialog()
        DaoManager.closeConnection()
        unregisterReceiver(mHomeReceiver)
    }

    override fun onResume() {
        super.onResume()
        banner_view.resume()

        if (!isNetworkConnected() && !Constant.ONLINE_VERSION) {
            group_root.visibility = View.VISIBLE
            group_machine_code.visibility = View.GONE
            val dbPath = FileUtils.getLocalPath() + Constant.DB_PATH
            val dbFile = File(dbPath)
            if (dbFile.exists() && dbFile.list().isNotEmpty()) {
                dbFile.listFiles { _, name ->
                    if (name.endsWith(".db")) {
                        DaoManager.setDBName(name)
                        getMainResources()
                    } else {
                        if (mIsFirstOpen) {
                            showToast("请到下载列表下载资源")
                        }
                    }
                    false
                }
                //getMainResources()
            } else {
                if (mIsFirstOpen) {
                    showToast("请到下载列表下载资源")
                }
            }
        } else if (isNetworkConnected()) {
            checkAppUpdate()
        }
    }

    override fun onPause() {
        super.onPause()
        banner_view.pause()
    }

    override fun providerVMClass() = MainViewModel()

    override fun getLayoutId() = R.layout.activity_main

    override fun initView() {
        initBannerView()
        initLauncher()

        setupExceptionHandler()
    }

    override fun initData() {
        mHandler.postDelayed(mTimerRunnable, 1_000)

        /*if (isNetworkConnected()) {
            checkMachineCode()
        }*/
    }

    override fun initListener() {
        super.initListener()
        tv_current_time.setOnClickListener {
            goToManagerPage()
        }

        tv_user_name.setOnClickListener {
            mMenuPopupWindow.showAtBottom(it)
            //mFeedbackDialog.show()
        }

        tv_download_list.setOnClickListener {
            startToActivity<DownloadListActivity>()
        }

        iv_book_massive.setOnClickListener {
            startToActivity<MassiveBooksActivity>()
        }

        iv_book_audio.setOnClickListener {
            startToActivity<AudioBooksActivity>()
        }

        iv_book_video.setOnClickListener {
            startToActivity<VideoBooksActivity>()
        }

        iv_book_journal.setOnClickListener {
            startToActivity<JournalBooksActivity>()
        }

        iv_book_display.setOnClickListener {
            startToActivity<NoticeActivity>()
        }

        iv_book_online.setOnClickListener {
            val url = it.tag
            if (url != null) {
                startToActivity<H5Activity>(Constant.H5_URL_KEY_EXTRA to url.toString())
            }
        }

        iv_topic.setOnClickListener {
            startToActivity<ThematicActivity>(Constant.THEMATIC_ID_KEY_EXTRA to 0)
        }
    }

    override fun observerForever() = false

    override fun observerUI(state: State) {
        when (state) {
            is LoadingState -> {
                showLoading()
            }
            is LoadedState -> {
                dismissLoading()
            }
            is ErrorState -> {
                showToast(state.errorMsg)
            }
            is MainModel.MachineCodeState -> {
                handleMachineCodeData(state.machineInfoBean)
            }

            is MainModel.UpdateDBState -> {
                handleUpdateDBData(state.updateDBBean)
            }

            is MainModel.MainResourcesState -> {
                showMainResourcesData(state.mainResourcesBean)
            }

            is MainModel.AppUpdateState -> {
                showAppUpdateInfo(state.appUpdateBean)
            }

            is MainModel.DownloadResourceState -> {
                handleDownloadResourceData(state.downloadResourceList)
            }
        }
    }

    private fun showAppUpdateInfo(appUpdateBean: AppUpdateBean?) {
        if (appUpdateBean == null || BuildConfig.VERSION_CODE >= appUpdateBean.versionCode) {
            if (mIsFirstOpen) {
                mIsFirstOpen = false
                showToast("当前已是最新版本！")
            }
            checkMachineCode()
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
                                setButton(
                                    DialogInterface.BUTTON_NEGATIVE,
                                    "取消下载"
                                ) { dialog, which ->
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
                        //showToast("下载失败，请重新下载！")
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
                                //showToast("下载失败，请重新下载！")
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
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        } else {
            openFile(file)
        }
    }

    private fun handleMachineCodeData(machineInfoBean: MachineInfoBean?) {
        LogUtils.e("code===${machineInfoBean?.status}//${machineInfoBean?.organId}//$mMachineCode")
        if (machineInfoBean?.status != MachineInfoBean.AUTHORITY_NORMAL) {
            group_machine_code.visibility = View.VISIBLE
            iv_yz_cover.setImageResource(R.drawable.img_yz_cover)
            group_root.visibility = View.GONE
            tv_machine_code.text = "机器码：$mMachineCode"
            btn_retry?.setOnClickListener {
                checkMachineCode()
            }
            btn_retry?.requestFocus()
            return
        }

        group_root.visibility = View.VISIBLE
        group_machine_code.visibility = View.GONE

        PreferencesUtils.putSpValue(SpConstant.ORGAN_ID, machineInfoBean.organId)

        /*val downloadUpdateTime = machineInfoBean.downloadUpdateTime ?: 0L
        if (downloadUpdateTime > PreferencesUtils.getSpValue(SpConstant.DOWNLOAD_UPDATE_TIME, 0L)) {
            PreferencesUtils.putSpValue(SpConstant.DOWNLOAD_UPDATE_TIME, downloadUpdateTime)
            showToast("有最新资源，请到下载列表下载")
            startToActivity<DownloadListActivity>(Constant.DOWNLOAD_LIST_TAG_KEY_EXTRA to "main")
            return
        }*/

        updateBD()
        /*if (Constant.ONLINE_VERSION) {
            getMainResources()
        } else {
            updateBD()
        }*/
    }

    private fun handleUpdateDBData(updateDBBean: UpdateDBBean?) {
        var needUpdate = true
        val dbPath = FileUtils.getLocalPath() + Constant.DB_PATH
        val dbFile = File(dbPath)

        val dbUrl = updateDBBean?.dbUrl
        if (dbUrl != null && dbUrl.isNotEmpty()) {
            val dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1).replace("zip", "db")
            DaoManager.setDBName(dbName)

            if (dbFile.exists()) {
                dbFile.listFiles { dir, name ->
                    if (name in dbUrl &&
                        updateDBBean.fileMd5String == FileMD5Utils.getFileMD5(File("$dir/$name"))
                    ) {
                        needUpdate = false
                    }
                    false
                }
            }
        } else {
            needUpdate = false
        }


        if (!needUpdate) {
            getMainResources()
            return
        }

        DaoManager.closeConnection()
        FileUtils.deleteDirectory(dbPath)
        downloadDB(dbUrl!!)
    }

    private fun downloadDB(dbUrl: String) {
        val fileName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1)
        val file = FileUtils.createDBFile(fileName)
        DownloadUtils.download(dbUrl.addFileHostUrl(), file.path,
            object : DownloadUtils.OnDownloadListener {
                override fun onDownloading(progress: Int) {
                    runOnUiThread {
                        with(mProgressDialog) {
                            if (!isShowing) {
                                setTitle("提示")
                                setMessage("正在更新图书数据...请耐心等待")
                                isIndeterminate = false
                                max = 100
                                setCancelable(false)
                                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                                setButton(
                                    DialogInterface.BUTTON_NEGATIVE,
                                    "取消下载"
                                ) { dialog, which ->
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
                        //showToast("下载失败，请重新下载！")
                    }
                }

                override fun onDownloadSuccess(path: String?) {
                    runOnUiThread {
                        mProgressDialog.dismissDialog()
                        path?.let {
                            ZipUtils.unZipFolder(it, file.parent)

                            showToast("更新完成！")

                            getMainResources()
                        }
                    }
                }
            })
    }

    private fun initBannerView() {
        with(banner_view) {
            setAdapter(mBannerAdapter)
            setOnBannerClickListener {
                startToActivity<ActivityListActivity>()
            }
            setOnClickListener {
                startToActivity<ActivityListActivity>()
            }
        }
    }

    private fun checkAppUpdate() {
        mViewModel?.checkAppUpdate()
    }

    private fun checkMachineCode() {
        LogUtils.e("machineCode==$mMachineCode//${mViewModel == null}")
        mViewModel?.checkMachineCode(mMachineCode)
    }

    private fun updateBD() {
        mViewModel?.updateBD(mMachineCode)
    }

    private fun getMainResources() {
        mViewModel?.getMainResources()
    }

    private fun checkDownloadResource() {
        if (!isNetworkConnected()) return

        mViewModel?.checkDownloadResource()
    }

    private fun showMainResourcesData(mainResourcesBean: MainResourcesBean?) {
        checkDownloadResource()

        if (mainResourcesBean == null) {
            return
        }

        with(mainResourcesBean) {
            mBannerAdapter.setBannerList(banners)
            banner_view.requestFocus()

            navs.forEach {
                val imgUrl = it.imgPath.addFileHostUrl()
                when (it.type) {
                    NavType.TYPE_LOGO -> {
                        //LogUtils.e("logo==$imgUrl")
                        if (!Constant.ONLINE_VERSION && !File(imgUrl).exists()) {
                            return
                        }
                        if (it.imgPath != null && it.imgPath.isNotEmpty()) {
                            //showToast(imgUrl)
                            ImageLoaderUtils.withLogo(
                                imgUrl,
                                iv_logo
                            )
                        }
                    }
                    NavType.TYPE_BOOK ->
                        ImageLoaderUtils.withBookCover(
                            imgUrl,
                            iv_book_massive,
                            ImageView.ScaleType.FIT_XY
                        )
                    NavType.TYPE_AUDIO ->
                        ImageLoaderUtils.withBookCover(
                            imgUrl,
                            iv_book_audio,
                            ImageView.ScaleType.FIT_XY
                        )
                    NavType.TYPE_VIDEO ->
                        ImageLoaderUtils.withBookCover(
                            imgUrl,
                            iv_book_video,
                            ImageView.ScaleType.FIT_XY
                        )
                    NavType.TYPE_JOURNAL ->
                        ImageLoaderUtils.withBookCover(
                            imgUrl,
                            iv_book_journal,
                            ImageView.ScaleType.FIT_XY
                        )
                    NavType.TYPE_NOTICE ->
                        ImageLoaderUtils.withBookCover(
                            imgUrl,
                            iv_book_display,
                            ImageView.ScaleType.FIT_XY
                        )
                    else -> {
                        iv_book_online.tag = it.url
                        ImageLoaderUtils.withBookCover(
                            imgUrl,
                            iv_book_online,
                            ImageView.ScaleType.FIT_XY
                        )
                    }
                }
            }
        }
    }

    private fun handleDownloadResourceData(downloadResourceList: MutableList<CheckDownloadResourceBean>?) {
        if (downloadResourceList == null || downloadResourceList.isEmpty() || mDownloadResourceUpdate) return

        val recordMap = getRecordMap()

        downloadResourceList.forEach {
            if (mDownloadResourceUpdate) {
                return@forEach
            }
            if (recordMap.containsKey(it.id)) {
                if (recordMap[it.id] != it.updateTime) {
                    mDownloadResourceUpdate = true
                    return@forEach
                }
            } else {
                mDownloadResourceUpdate = true
                return@forEach
            }
        }

        if (mDownloadResourceUpdate) {
            goToDownloadListPage()
        }
    }

    private fun goToDownloadListPage() {
        showToast("有最新资源，请到下载列表下载")
        startToActivity<DownloadListActivity>(Constant.DOWNLOAD_LIST_TAG_KEY_EXTRA to "main")
    }

    private fun goToManagerPage() {
        //实现数组的移位操作，点击一次，左移一位，末尾补上当前开机时间（cpu的时间）
        System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
        mHits[mHits.size - 1] = SystemClock.uptimeMillis()
        //双击事件的时间间隔1s
        if (mHits[0] >= SystemClock.uptimeMillis() - 1000) {
            //三击后具体的操作
            startActivity(Intent(this, ManagerActivity::class.java))
        }
    }

    private fun getRecordMap(): ArrayMap<Int, String> {
        val recordMap = ArrayMap<Int, String>()
        val file = File(FileUtils.getLocalPath() + "downloadRecord.txt")
        if (!file.exists()) return recordMap

        val recordTxt = file.readText()
        LogUtils.e("recordTxt==$recordTxt")
        if (recordTxt.contains(",")) {
            recordTxt.split(",").forEach {
                if (it.contains("#")) {
                    val (id, time) = it.split("#")
                    recordMap[id.toInt()] = time
                }
            }
        }
        return recordMap
    }

    // Home键侦测---[----
    internal val mHomeReceiver = object : BroadcastReceiver() {
        private val SYS_KEY = "reason" // 标注下这里必须是这么一个字符串值
        private val SYS_HOME_KEY = "homekey"// 标注下这里必须是这么一个字符串值
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYS_KEY)
                if (reason != null && reason == SYS_HOME_KEY) {
                    //loge("home键监听")
                    val currentHome = getHomeLauncher()
                    //loge("currentHome=$currentHome")
                    if (isDefaultHome()) {
                        return
                    }
                    setDefaultL()
                }
            }
        }
    }

    private fun setupExceptionHandler() {
        Thread.currentThread().uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { _, _ -> showForceUpgradeDialog("内部运行异常，请联系工作人员") }
    }

    private fun showForceUpgradeDialog(message: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setCancelable(false)
                .create()
                .show()
        }
    }
}
