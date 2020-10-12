package com.yz.books.ui.massive.readbook

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.yz.books.R
import com.yz.books.api.ApiConstant
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.*
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.utils.DownloadUtils
import com.yz.books.utils.FileMD5Utils
import com.yz.books.utils.FileUtils
import com.yz.books.utils.LogUtils
import com.yz.books.widget.dialog.LoadingDialog
import com.yz.books.widget.pop.MassiveBookChapterPopupWindow
import com.yz.books.widget.pop.VoicePopupWindow
import kotlinx.android.synthetic.main.activity_read_book.*
import me.jessyan.autosize.internal.CancelAdapt
import java.io.File


/**
 * 图书阅读
 *
 * @author lilin
 * @time on 2020-01-12 16:05
 */
abstract class ReadBookActivity : BaseMVVMActivity<ReadBookViewModel>(), IReadBookView,
    CancelAdapt {

//region var/val

    protected val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    private val mFontPopupWindow by lazy(LazyThreadSafetyMode.NONE) {
        VoicePopupWindow(this) {
            setFontSize(it)
        }
    }

    private val mMassiveBookChapterPop by lazy(LazyThreadSafetyMode.NONE) {
        MassiveBookChapterPopupWindow(this) {
            setChapterIndex(it)
        }
    }

    private lateinit var mDocType: DocTypeEnum
    private lateinit var mDocView: View

    private var mReadBookBean: ReadBookBean? = null

    /**
     * 图书信息
     */
    private var mBookId: Int? = null
    private var mBookName: String? = null

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        mProgressDialog.dismissDialog()
        mFontPopupWindow.dismiss()
        mMassiveBookChapterPop.dismiss()
    }

    override fun getAssets(): AssetManager {
        return resources.assets
    }

    override fun providerVMClass() = ReadBookViewModel()

    override fun getLayoutId() = R.layout.activity_read_book

    override fun initView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mDocType = getDocType()
        if (mDocType != DocTypeEnum.EPUB) {
            group_epub.visibility = View.GONE
        }

        if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            window.setLayout(screenWidth * 3 / 4, screenHeight)
        } else {
            window.setLayout(screenWidth * 95 / 100, screenHeight * 4 / 5)
        }
    }

    override fun initData() {
        val bookInfo = intent?.extras?.getSerializable(Constant.MASSIVE_BOOK_INFO_KEY_EXTRA)
                as MassiveBookDetailBean.MassiveBookDetailInfo

        handleBookInfo(bookInfo)
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            finish()
        }

        iv_book_chapter.setOnClickListener {
            mMassiveBookChapterPop.showAtBottom(it)
        }

        iv_previous_page.setOnClickListener {
            showPreviousPage()
        }

        iv_next_page.setOnClickListener {
            showNextPage()
        }

        iv_previous_chapter.setOnClickListener {
            showPreviousChapter()
        }

        iv_next_chapter.setOnClickListener {
            showNextChapter()
        }

        iv_font_setting.setOnClickListener {
            mFontPopupWindow.showAtBottom(it)
        }
    }

    override fun observerForever() = false

    override fun observerUI(state: State) {

    }

    override fun getReadBookBean() = mReadBookBean

    override fun setBookTitle(title: String?) {
        tv_title.text = title
    }

    override fun setBookChapters(chapter: MutableList<String>) {
        mMassiveBookChapterPop.setChaptersData(chapter)
    }

    abstract fun showPreviousChapter()
    abstract fun showNextChapter()
    abstract fun setChapterIndex(index: Int)
    abstract fun showPreviousPage()
    abstract fun showNextPage()
    abstract fun preBookView()

//endregion

//region public methods

    protected fun showBookCover(bitmap: Bitmap?) {
        iv_cover.setImageBitmap(bitmap)
        iv_cover.visibility = View.VISIBLE
    }

//endregion

//region private methods

    private fun handleBookInfo(bookInfo: MassiveBookDetailBean.MassiveBookDetailInfo) {
        mBookId = bookInfo.bookId
        mBookName = bookInfo.bookName.trim()
        //val bookNamePath = bookInfo.path.substring(bookInfo.path.lastIndexOf("/") + 1)
        //LogUtils.e("path1==${bookInfo.path.substring(bookInfo.path.lastIndexOf("/") + 1)}")
        //LogUtils.e("path2==${bookInfo.path.replace("/$bookNamePath", "")}")

        val path = "${FileUtils.getLocalPath()}${bookInfo.path}"
        LogUtils.e("path==$path")
        val file = File(path)
        if (file.exists() && bookInfo.fileMd5String == FileMD5Utils.getFileMD5(file)) {
            //LogUtils.e("md5==${bookInfo.fileMd5String}//${FileMD5Utils.getFileMD5(File("$dir/$name"))}")
            showDocumentView(path, bookInfo.bookName.trim())
        } else {
            if (isNetworkConnected()) {
                FileUtils.deleteFile(path)
                downloadMassiveBook(bookInfo)
            } else {
                showToast("图书文件不存在，请到资源列表下载！")
            }
        }
        /*if (file.exists() && file.listFiles().isNotEmpty()) {
            file.listFiles { dir, name ->
                if (name in bookInfo.path &&
                    bookInfo.fileMd5String == FileMD5Utils.getFileMD5(File("$dir/$name"))) {
                    //LogUtils.e("md5==${bookInfo.fileMd5String}//${FileMD5Utils.getFileMD5(File("$dir/$name"))}")
                    showDocumentView("$dir/$name",bookInfo.bookName.trim())
                } else {
                    FileUtils.deleteFile("$dir/$name")
                    downloadMassiveBook(bookInfo)
                }
                false
            }
        } else {
            downloadMassiveBook(bookInfo)
        }*/
    }

    private fun showDocumentView(path: String, bookName: String) {
        //val path = "/mnt/sdcard/1447838181531初中生作文分类王7年级.epub"
        mReadBookBean = ReadBookBean(path, bookName,0,0,0)

        mDocView = getDocumentView()
        mDocView.isFocusable = false
        preBookView()

        initContentView()

    }

    private fun initContentView() {
        /*val param = if (getDocType() == DocTypeEnum.EPUB) {
            if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            } else {
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            }
        } else {
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        }*/

        val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        ll_content.addView(mDocView, param)
        mDocView.isFocusable = false
    }

    private fun downloadMassiveBook(bookInfo: MassiveBookDetailBean.MassiveBookDetailInfo) {
        bookInfo.apply {
            if (path == null) {
                return@apply
            }
            if (!path.endsWith(".pdf") && !path.endsWith(".epub")) {
                return@apply
            }
            val fileName = path.substring(path.lastIndexOf("/") + 1)
            val file = FileUtils.createTempFile(fileName, path.replace("/$fileName", "")) //"$mBookId$mBookName",
            LogUtils.e("filepath==${file.path}")
            val url = if (path.startsWith("http") || path.startsWith("https")) {
                path
            } else {
                ApiConstant.HOST + path
            }
            DownloadUtils.download(url, file.path,
                object : DownloadUtils.OnDownloadListener {
                    override fun onDownloading(progress: Int) {
                        runOnUiThread {
                            with(mProgressDialog) {
                                if (!isShowing) {
                                    setTitle("提示")
                                    setMessage("正在下载...请耐心等待")
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
                            LogUtils.e("md5比较=${FileMD5Utils.getFileMD5(File(path))}//${bookInfo.fileMd5String}")

                            path?.let {
                                if (FileMD5Utils.getFileMD5(File(it)) == bookInfo.fileMd5String) {
                                    showDocumentView(it, bookName)
                                } else {
                                    showToast("下载失败，请重新下载！")
                                    FileUtils.deleteFile(it)
                                }
                            }
                        }
                    }
                })
        }
    }

//endregion
}