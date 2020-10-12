package com.yz.books.ui.downloadlist

import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.yz.books.R
import com.yz.books.adapter.DownloadAdapter
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoManager
import com.yz.books.ext.*
import com.yz.books.ui.downloadlist.bean.DownloadResourceBean
import com.yz.books.ui.downloadlist.download.DownloadFacade
import com.yz.books.utils.*
import com.yz.books.widget.dialog.TipsDialog
import kotlinx.android.synthetic.main.activity_download_list.*
import kotlinx.coroutines.*
import java.io.File

/**
 * @author lilin
 * @time on 2020/3/23 上午11:12
 */
class DownloadListActivity : BaseMVVMActivity<DownloadListViewModel>() {

//region var/val

    private val mDownloadAdapter by lazy(LazyThreadSafetyMode.NONE) {
        DownloadAdapter(this, null)
    }

    private val mTipsDialog by lazy(LazyThreadSafetyMode.NONE) {
        TipsDialog(this) {
            if (it) {
                finish()
            }
        }
    }

    //private var mTotal = 1
    private var mPageNum = 1

    private var mCurrentPosition = 0
    /**
     * 是否下载中
     */
    private var mDownloading = false

    private var mDownloadingUrl: String? = null

    private var mZipJob: Job? = null

    private var mFromSource: String? = null

    private var mListSize = 0
    private var mTotalPages = 0

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        stopDownload()
        mTipsDialog.dismissDialog()
        mZipJob?.cancel()
    }

    override fun providerVMClass() = DownloadListViewModel()

    override fun getLayoutId() = R.layout.activity_download_list

    override fun initView() {
        cl_pwd.visibility = View.GONE

        initRecyclerView()
        initAdapter()
        initRefreshLayout()

        initKeyboardView()
    }

    override fun initData() {
        mFromSource = intent?.extras?.getString(Constant.DOWNLOAD_LIST_TAG_KEY_EXTRA)

        DownloadFacade.getFacade().init(this)
        if (!File(FileUtils.getLocalPath() + "downloadRecord.txt").exists()) {
            val file = FileUtils.createRecordTxtFile("downloadRecord.txt")
            //LogUtils.e("是否存在==${file.exists()}")
        }

        getDownloadList()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            if (mDownloading) {
                mTipsDialog.show()
                mTipsDialog.showDesc("有资源正在下载中，现在退出则会停止下载。确定退出？")
            } else {
                finish()
            }
        }

        /*btn_next.setOnClickListener {
            if (mPageNum < mTotal) {
                if (mDownloading) {
                    showToast("下载中，请勿翻页")
                    return@setOnClickListener
                }
                mPageNum++
                getDownloadList()
            }
        }

        btn_previous.setOnClickListener {
            if (mPageNum > 1) {
                if (mDownloading) {
                    showToast("下载中，请勿翻页")
                    return@setOnClickListener
                }
                mPageNum--
                getDownloadList()
            }
        }*/

        mDownloadAdapter.setOnItemChildClick { adapter, view, position ->
            val data = adapter.data[position] as DownloadResourceBean.ResourceInfo
            if (!readyDownloadData(data, position)) return@setOnItemChildClick

            if (view.id == R.id.iv_remove) {
                stopDownload()
                adapter.remove(position)
            } else {
                downloadNext(data, position)
            }
        }

        btn_sure.setOnClickListener {
            if (et_pwd.textContent != "yuezhi") {
                showToast("密码不正确,请重新输入")
                return@setOnClickListener
            }
            et_pwd.clearFocus()
            hideSoftInput()
            cl_pwd.visibility = View.GONE
        }
    }

    override fun observerForever() = false

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

            is DownloadListModel.DownloadListState -> {
                mTotalPages = state.downloadResourceBean?.total ?: 0
                showDownloadList(state.downloadResourceBean)
            }
        }
    }

//endregion

//region public methods

//endregion

//region private methods

    private fun initRecyclerView() {
        recycler_view_download.run {
            layoutManager = LinearLayoutManager(this@DownloadListActivity)
            adapter = mDownloadAdapter
        }
    }

    private fun initAdapter() {
        mDownloadAdapter.run {
            bindToRecyclerView(recycler_view_download)
        }
    }

    private fun initRefreshLayout() {
        refresh_layout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mPageNum ++
                getDownloadList()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                refresh_layout.setNoMoreData(false)
                mPageNum = 1
                getDownloadList()
            }
        })
    }

    private fun getDownloadList() {
        mViewModel?.getDownloadList(mPageNum)
    }

    private fun showDownloadList(downloadResourceBean: DownloadResourceBean?) {
        mListSize += (downloadResourceBean?.list?.size ?: 0)

        if (downloadResourceBean == null || downloadResourceBean.list.isEmpty()) {
            refresh_layout.apply {
                finishRefresh()
                finishLoadMore()
                isEnableRefresh = true
                isEnableLoadMore = true
                finishLoadMore(0, true, true)
            }
            return
        }

        val recordMap = getRecordMap()

        downloadResourceBean.apply {
            list.forEach {
                if (recordMap.containsKey(it.id)) {
                    if (recordMap[it.id] == it.updateTime) {
                        it.completed = true
                    }
                }
            }

            val filterList = list.filter {
                !it.completed
            }

            if (mPageNum == 1) {
                refresh_layout.finishRefresh()
                mDownloadAdapter.replaceData(filterList)
            } else {
                refresh_layout.finishLoadMore()
                mDownloadAdapter.addData(filterList)
            }
            refresh_layout.isEnableRefresh = true
            refresh_layout.isEnableLoadMore = true

            //过滤后为空，继续自动请求下一页
            if (filterList.isEmpty()) {
                mPageNum ++
                getDownloadList()
            } else {
                //首页进入开启自动下载
                if (!mFromSource.isNullOrEmpty()) {
                    mFromSource = ""
                    val pos = 0
                    val data = filterList[pos]
                    readyDownloadData(data, pos)
                    downloadNext(data, pos)
                }
            }
        }
    }

    private fun readyDownloadData(data: DownloadResourceBean.ResourceInfo, position: Int): Boolean {
        val url = data.resourcePath.addFileHostUrl()
        LogUtils.e("url==$url//$mDownloadingUrl")
        if (mDownloading && url != mDownloadingUrl) {
            showToast("已有资源正在下载中，请等待")
            return false
        }
        mCurrentPosition = position
        mDownloadingUrl = data.resourcePath.addFileHostUrl()

        return true
    }

    private fun getRecordMap(): ArrayMap<Int, String> {
        val recordMap = ArrayMap<Int, String>()
        try {
            val recordTxt = File(FileUtils.getLocalPath() + "downloadRecord.txt").readText()
            LogUtils.e("recordTxt==$recordTxt")
            if (recordTxt.contains(",")) {
                recordTxt.split(",").forEach {
                    if (it.contains("#")) {
                        val (id, time) = it.split("#")
                        recordMap[id.toInt()] = time
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return recordMap
    }

    private fun writeDownloadRecordTxt(id: Int, time: String) {
        val recordMap = getRecordMap()
        recordMap[id] = time

        val stringBuilder = StringBuilder()
        for ((index, value) in recordMap) {
            val item = "${index}#$value"
            stringBuilder.append(item)
            stringBuilder.append(",")
        }
        stringBuilder.dropLast(1)

        try {
            File(FileUtils.getLocalPath() + "downloadRecord.txt").writeText(stringBuilder.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadNext(
        data: DownloadResourceBean.ResourceInfo,
        position: Int
    ) {
        if (data.completed) {
            val dataSize = mDownloadAdapter.data.size
            if (mCurrentPosition < dataSize) {
                mCurrentPosition++
                if (mCurrentPosition == dataSize) {
                    if (mTotalPages == mListSize) {
                        finish()
                    }
                    return
                }
                downloadNext(mDownloadAdapter.data[mCurrentPosition], mCurrentPosition)
            }
            if (mTotalPages == mListSize) {
                finish()
            }
            return
        }

        data.started = !data.started
        data.progress = 0
        mDownloadAdapter.notifyItemChanged(position)

        mCurrentPosition = position
        mDownloadingUrl = data.resourcePath.addFileHostUrl()

        if (!data.started) {
            stopDownload()
        } else {
            DownloadUtils.mCancelDownload = false
            handleDownloadResource(data, position)
        }
    }

    private fun handleDownloadResource(data: DownloadResourceBean.ResourceInfo, position: Int) {
        data.apply {
            if (resourceType == "db") {
                //val dbPath = Constant.ROOT_PATH + Constant.DB_PATH
                //val dbFile = File(dbPath)

                val dbUrl = resourcePath
                if (dbUrl != null && dbUrl.isNotEmpty()) {
                    val dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1).replace("zip", "db")
                    DaoManager.setDBName(dbName)
                }
                DaoManager.closeConnection()

                downloadDB(this, position)
            } else {
                downloadResource(this, position)
            }

           /* val path = FileUtils.getLocalPath()
            val file = File(path)
            if (file.exists() && file.listFiles().isNotEmpty()) {
                file.listFiles { dir, name ->
                    if (name in resourcePath &&
                        fileMd5String == FileMD5Utils.getFileMD5(File("$dir/$name"))) {
                    } else {
                        FileUtils.deleteFile("$dir/$name")
                        downloadResource(this, position)
                    }
                    false
                }
            } else {
                downloadResource(this, position)
            }*/
        }
    }

    private fun downloadDB(resourceInfo: DownloadResourceBean.ResourceInfo, position: Int) {
        resourceInfo.apply {
            val dbUrl = resourcePath
            val fileName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1)
            val file = FileUtils.createDBFile(fileName)
            DownloadUtils.download(dbUrl.addFileHostUrl(), file.path,
                object : DownloadUtils.OnDownloadListener {
                    override fun onDownloading(progress: Int) {
                        LogUtils.e("onDownloading==$progress//$resourceName")
                        runOnUiThread {
                            refresh_layout.isEnableRefresh = false
                            mDownloading = true
                            if (progress % 2 == 0) {
                                resourceInfo.progress = progress
                                mDownloadAdapter.notifyItemChanged(position)
                            }
                        }
                    }

                    override fun onDownloadFailed(msg: String?) {
                        LogUtils.e("onDownloadFailed==$msg")
                        runOnUiThread {
                            refresh_layout.isEnableRefresh = true
                            mDownloading = false
                            started = false
                            progress = 0
                            mDownloadAdapter.notifyItemChanged(position)
                            showToast("下载失败，请重新下载！")

                            downloadNext()
                        }
                    }

                    override fun onDownloadSuccess(path: String?) {
                        path?.let {
                            val job = GlobalScope.launch(Dispatchers.Main) {
                                val deferred = GlobalScope.async (context = Dispatchers.IO, start = CoroutineStart.LAZY) {
                                    if (FileMD5Utils.getFileMD5(File(it)) == fileMd5String) {
                                        if (it.endsWith(".zip")) {
                                            ZipUtils.unZipFolder(it, file.parent)
                                        }
                                    }
                                }
                                deferred.await()
                            }
                            mZipJob = job
                            job.invokeOnCompletion {
                                runOnUiThread {
                                    LogUtils.e("md5比较=${FileMD5Utils.getFileMD5(File(path))}//${fileMd5String}//${file.parent}")
                                    refresh_layout.isEnableRefresh = true
                                    mDownloading = false
                                    if (FileMD5Utils.getFileMD5(File(path)) == fileMd5String) {
                                        if (path.endsWith(".zip")) {
                                            //ZipUtils.unZipFolder(it, file.parent)

                                            writeDownloadRecordTxt(id, updateTime)

                                            completed = true
                                            mDownloadAdapter.notifyItemChanged(position)

                                            if (mCurrentPosition < mDownloadAdapter.data.size) {
                                                mCurrentPosition++
                                                if (mCurrentPosition == mDownloadAdapter.data.size) {
                                                    return@runOnUiThread
                                                }
                                                downloadNext(mDownloadAdapter.data[mCurrentPosition], mCurrentPosition)
                                            } else {
                                                showToast("下载完成！")
                                            }

                                        } else {
                                            started = false
                                            mDownloadAdapter.notifyItemChanged(position)
                                            showToast("下载失败，请重新下载！")
                                            FileUtils.deleteFile(path)

                                            downloadNext()
                                        }
                                    } else {
                                        started = false
                                        mDownloadAdapter.notifyItemChanged(position)
                                        showToast("下载失败，请重新下载！")
                                        FileUtils.deleteFile(path)

                                        downloadNext()
                                    }
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun downloadNext() {
        if (DownloadUtils.mCancelDownload) return

        if (mCurrentPosition < mDownloadAdapter.data.size) {
            mCurrentPosition++
            if (mCurrentPosition == mDownloadAdapter.data.size) {
                return
            }
            downloadNext(mDownloadAdapter.data[mCurrentPosition], mCurrentPosition)
        }
    }

    private fun downloadResource(resourceInfo: DownloadResourceBean.ResourceInfo, position: Int) {
        resourceInfo.apply {
            LogUtils.e("resourceInfo==$resourceName//$position")
            //|| type != "1"
            if (resourcePath == null) {
                return@apply
            }
            val fileName = resourcePath.substring(resourcePath.lastIndexOf("/") + 1)
            val file = FileUtils.createTempFile(fileName)
            LogUtils.e("filepath==${file.path}//${resourcePath.addFileHostUrl()}")
            DownloadUtils.download(resourcePath.addFileHostUrl(), file.path,
                object : DownloadUtils.OnDownloadListener {
                    override fun onDownloading(progress: Int) {
                        LogUtils.e("onDownloading==$progress//$resourceName")
                        runOnUiThread {
                            refresh_layout.isEnableRefresh = false
                            mDownloading = true
                            if (progress % 2 == 0) {
                                resourceInfo.progress = progress
                                mDownloadAdapter.notifyItemChanged(position)
                            }
                        }
                    }

                    override fun onDownloadFailed(msg: String?) {
                        LogUtils.e("onDownloadFailed==$msg")
                        runOnUiThread {
                            refresh_layout.isEnableRefresh = true
                            mDownloading = false
                            started = false
                            progress = 0
                            mDownloadAdapter.notifyItemChanged(position)
                            showToast("下载失败，请重新下载！")

                            downloadNext()
                        }
                    }

                    override fun onDownloadSuccess(path: String?) {
                        path?.let {
                            val job = GlobalScope.launch(Dispatchers.Main) {
                                val deferred = GlobalScope.async (context = Dispatchers.IO, start = CoroutineStart.LAZY) {
                                    if (FileMD5Utils.getFileMD5(File(it)) == fileMd5String) {
                                        if (it.endsWith(".zip")) {
                                            //ZipUtils.unZipFolder(it, file.parent)
                                            ZipUtils.decompressFile(file.parent, it)
                                        }
                                    }
                                }
                                deferred.await()
                            }
                            mZipJob = job
                            job.invokeOnCompletion {
                                runOnUiThread {
                                    LogUtils.e("md5比较=${FileMD5Utils.getFileMD5(File(path))}//${fileMd5String}")
                                    refresh_layout.isEnableRefresh = true
                                    mDownloading = false
                                    if (FileMD5Utils.getFileMD5(File(path)) == fileMd5String) {
                                        if (path.endsWith(".zip")) {
                                            //ZipUtils.unZipFolder(it, FileUtils.getLocalPath())
                                            writeDownloadRecordTxt(id, updateTime)

                                            completed = true
                                            mDownloadAdapter.notifyItemChanged(position)

                                            if (mCurrentPosition < mDownloadAdapter.data.size) {
                                                mCurrentPosition++
                                                if (mCurrentPosition == mDownloadAdapter.data.size) {
                                                    return@runOnUiThread
                                                }
                                                downloadNext(mDownloadAdapter.data[mCurrentPosition], mCurrentPosition)
                                            } else {
                                                showToast("下载完成！")
                                            }
                                        } else {
                                            started = false
                                            mDownloadAdapter.notifyItemChanged(position)
                                            showToast("下载失败，请重新下载！")
                                            FileUtils.deleteFile(path)

                                            downloadNext()
                                        }
                                    } else {
                                        started = false
                                        mDownloadAdapter.notifyItemChanged(position)
                                        showToast("下载失败，请重新下载！")
                                        FileUtils.deleteFile(path)

                                        downloadNext()
                                    }
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun stopDownload() {
        //DownloadFacade.getFacade().stopDownload(mDownloadingUrl)
        DownloadUtils.mCancelDownload = true
    }

    private fun initKeyboardView() {
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
    }

//endregion
}