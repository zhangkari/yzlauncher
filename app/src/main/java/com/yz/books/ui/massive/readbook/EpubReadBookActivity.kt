package com.yz.books.ui.massive.readbook

import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import com.yz.books.R
import com.yz.books.utils.LogUtils
import com.yz.books.utils.sp.PreferencesUtils
import com.yz.books.utils.sp.SpConstant
import me.jessyan.autosize.internal.CancelAdapt
import rmkj.lib.read.RMReadController
import rmkj.lib.read.epub.entity.RMEPUBResourceProvider
import rmkj.lib.read.epub.entity.RMEPUBZipObject
import rmkj.lib.read.itf.IRMObejctInterface
import rmkj.lib.read.itf.OnRMEPUBLoaderListener
import rmkj.lib.read.itf.OnRMPageChangeListener
import rmkj.lib.read.itf.OnRMSpineChangedListener
import rmkj.lib.read.util.RMUtilFileStream
import rmkj.lib.read.view.RMEPUBView
import java.io.File
import java.util.regex.Pattern


/**
 * epub类型
 *
 * @author lilin
 * @time on 2020-01-13 08:33
 */
class EpubReadBookActivity : ReadBookActivity(),
    OnRMPageChangeListener, OnRMSpineChangedListener, OnRMEPUBLoaderListener {
//region var/val

    private val mPattern = Pattern
        .compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>")

    private lateinit var mEpubView: RMEPUBView
    private lateinit var mReadController: RMReadController

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        if (::mEpubView.isInitialized) {
            mEpubView.onDestory()
        }
        if (::mReadController.isInitialized) {
            mReadController.onDestroy()
        }
    }

    //override fun providerVMClass() = ReadBookViewModel()

    override fun getDocType() = DocTypeEnum.EPUB

    override fun getDocumentView(): RMEPUBView {
        mEpubView = object : RMEPUBView(this) {
            override fun doLoadSpine(
                spineIndex: Int,
                epub: IRMObejctInterface?,
                provider: RMEPUBResourceProvider?
            ) {
                //super.doLoadSpine(spineIndex, epub, provider)

                val epubZipObject: RMEPUBZipObject? = epub as RMEPUBZipObject
                setBookTitle(epubZipObject?.bookName)
                if (epubZipObject != null) {
                    val chapters = mutableListOf<String>()
                    val chapterList = epubZipObject.ncxManager.nav.navPoints
                    for (i in 0 until chapterList.size) {
                       val chapter = chapterList[i]
                        //LogUtils.e("chapter==${chapter.level}//${chapter.text}")
                        chapters.add(chapter.text)
                    }
                    setBookChapters(chapters)
                }
                if (epubZipObject != null) {
                    val property = epubZipObject.opfManager.spine.list[spineIndex].property
                    if (property != null) {
                        jsInterface.onJSLoadComplete(0, 1, -1, -1)
                        val spineFile = epubZipObject.getSpineFile(spineIndex)
                        val sourceHtml = RMUtilFileStream.getTextFromFile(
                            provider?.getSpineContent(spineFile), "utf-8")
                        val m = mPattern.matcher(sourceHtml)
                        if (m.find()) {
                            val f = File(
                                (File(spineFile).parent + File.separator)
                                    .replace(
                                        File.separator
                                                + File.separator,
                                        File.separator
                                    ), m.group(1)
                            )
                            LogUtils.e("file==${f.path}")
                            showBookCover(
                                BitmapFactory.decodeStream(
                                provider?.getSpineContent(f.canonicalPath.substring(1))))
                        } else {
                            super.doLoadSpine(spineIndex, epub, provider)
                        }
                    } else {
                        super.doLoadSpine(spineIndex, epub, provider)
                    }
                } else {
                    super.doLoadSpine(spineIndex, epub, provider)
                }
            }
        }

        return mEpubView
    }

    override fun onPageChanged(page: Int, totalPage: Int) {
        LogUtils.e("onPageChanged=$page//$totalPage")
    }

    override fun onSpineChanged(currentSpine: Int) {
        LogUtils.e("onSpineChanged=$currentSpine")
    }

    override fun onLoadBookComplete() {
        LogUtils.e("onLoadBookComplete=")
        if (!::mEpubView.isInitialized) {
            return
        }
        mEpubView.apply {
            setBackgroundColor(ContextCompat.getColor(this@EpubReadBookActivity, R.color.cF5F4D5))
            setWebViewFontColor("#421E00", false)
            setWebViewLineSpace(210, false)
            setWebViewFontSize(70, false)
        }
        mEpubView.setNoteImagePath("file:///android_asset/note_tag.png")
        getReadBookBean()?.apply {
            mEpubView.showSpine(readingChapter, readingCurrentPage, readingTotalPage)
        }
    }

    override fun showPreviousChapter() {
        if (!::mEpubView.isInitialized) {
            return
        }
        mEpubView.showPrevSpine()
    }

    override fun showNextChapter() {
        if (!::mEpubView.isInitialized) {
            return
        }
        mEpubView.showNextSpine()
    }

    override fun setChapterIndex(index: Int) {
        if (!::mEpubView.isInitialized) {
            return
        }
        mEpubView.showSpine(index)
    }

    override fun showPreviousPage() {
        if (!::mEpubView.isInitialized) {
            return
        }
        //mEpubView.showPage(mEpubView.currentPage - 1)
        mEpubView.showNextPage()
    }

    override fun showNextPage() {
        if (!::mEpubView.isInitialized) {
            return
        }
        //mEpubView.showPage(mEpubView.currentPage + 1)
        mEpubView.showPrevPage()
    }

    override fun setFontSize(size: Int) {
        if (!::mEpubView.isInitialized) {
            return
        }
        mEpubView.setWebViewFontSize(size, true)
    }

    override fun preBookView() {
        if (!::mEpubView.isInitialized) {
            return
        }
        mReadController = RMReadController(mEpubView, getReadBookBean()?.bookPath,
            PreferencesUtils.getSpValue(SpConstant.USER_ID, "admin"))
        mReadController.apply {
            //setOnJSClick(this@EpubReadBookActivity)
            setOnPageChaneListener(this@EpubReadBookActivity)
            setOnSpineChangedListener(this@EpubReadBookActivity)
            loadEpubRzp(getReadBookBean()?.bookPath, this@EpubReadBookActivity)
        }
    }

//endregion

//region public methods6

//endregion

//region private methods

//endregion
}