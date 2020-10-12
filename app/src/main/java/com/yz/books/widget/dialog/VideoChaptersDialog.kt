package com.yz.books.widget.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.books.R
import com.yz.books.adapter.AudioChaptersAdapter
import com.yz.books.ext.setOnItemClick
import com.yz.books.ui.video.bean.VideoBookChaptersBean
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_audio_chapter.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class VideoChaptersDialog(context: Context,
                          private val portrait: Boolean) : BaseDialog(context) {

    private val mAudioChaptersAdapter by lazy(LazyThreadSafetyMode.NONE) {
        AudioChaptersAdapter<VideoBookChaptersBean.VideoBookChaptersInfo>(context, null)
    }

    private var mTotalPages = 1
    private var mPageNum = 1

    private var mLoadChaptersCallback: ((Int) -> Unit)? = null
    private var mHandleChaptersCallback: ((Int,VideoBookChaptersBean.VideoBookChaptersInfo) -> Unit)? = null

    override fun getLayoutId() = R.layout.dialog_audio_chapter

    override fun initView() {
        super.initView()
        if (portrait) {
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(Gravity.BOTTOM)
            window?.setWindowAnimations(R.style.BaseDialogAnim)
        } else {
            window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window?.setGravity(Gravity.END)
            window?.setWindowAnimations(R.style.RightDialogAnim)
        }

        tv_title.text = "视频列表"

        initRecyclerView()

        btn_previous.isEnabled = false
        btn_next.isEnabled = false
    }

    override fun initListener() {
        btn_back.setOnClickListener {
            dismiss()
        }

        btn_previous.setOnClickListener {
            btn_next.isEnabled = true

            if (mPageNum > 0) {
                mPageNum--
                mLoadChaptersCallback?.invoke(mPageNum)
            }

            if (mPageNum == 1) {
                btn_previous.isEnabled = false
            }
        }

        btn_next.setOnClickListener {
            btn_previous.isEnabled = true
            if (mPageNum < mTotalPages) {
                mPageNum++
                mLoadChaptersCallback?.invoke(mPageNum)
            }

            if (mPageNum == mTotalPages) {
                btn_next.isEnabled = false
            }
        }
    }

    private fun initRecyclerView() {
        with(recycler_view_chapters) {
            layoutManager = LinearLayoutManager(context)
            adapter = mAudioChaptersAdapter
        }

        mAudioChaptersAdapter.setOnItemClick { _, _, position ->
            val data = mAudioChaptersAdapter.data[position]
            if (data.isPlaying) {
                return@setOnItemClick
            }
            mHandleChaptersCallback?.invoke(position, data)
        }
    }

    fun refreshItemData(position: Int, localPath: String) {
        val dataList = mAudioChaptersAdapter.data
        if (localPath.isNotEmpty()) {
            dataList[position].downloaded = true
            dataList[position].localPath = localPath
        } else {
            dataList.forEach {
                it.isPlaying = false
            }
            dataList[position].isPlaying = true
        }
        mAudioChaptersAdapter.notifyDataSetChanged()
    }

    fun setAudioChapters(bookName: String?,
                         chaptersBean: VideoBookChaptersBean?) {
        if (chaptersBean == null) {
            mAudioChaptersAdapter.replaceData(mutableListOf())
            return
        }
        with(chaptersBean) {
            mAudioChaptersAdapter.mBookName = bookName ?: ""
            mAudioChaptersAdapter.replaceData(videoChapter)

            if (total == 0) {
                group_page_info.visibility = View.GONE
                return@with
            }
            val totalPages = if (total % 6 > 0) {
                total / 6 + 1
            } else {
                total / 6
            }
            mTotalPages = totalPages
            group_page_info.visibility = View.VISIBLE
            tv_page_info.text = "$mPageNum / $totalPages"

            if (mPageNum == totalPages) {
                btn_previous.isEnabled = false
                btn_next.isEnabled = false
            } else {
                btn_previous.isEnabled = false
                btn_next.isEnabled = true
            }
        }
    }

    fun getPreviousNextChapter(position: Int): VideoBookChaptersBean.VideoBookChaptersInfo? {
        if (mAudioChaptersAdapter.data.isEmpty()) {
            return null
        }
        //下一页判断
        if (position == mAudioChaptersAdapter.data.size) {
            return if (mPageNum == mTotalPages) {
                null
            } else {
                mPageNum++
                VideoBookChaptersBean.VideoBookChaptersInfo(-1,"","",
                    fileMd5String = "",
                    isPlaying = false,
                    downloaded = false,
                    localPath = ""
                )
            }
        }
        //上一页判断
        if (position < 0) {
            return if (mPageNum == mTotalPages) {
                null
            } else {
                mPageNum--
                VideoBookChaptersBean.VideoBookChaptersInfo(-2,"","",
                    fileMd5String = "",
                    isPlaying = false,
                    downloaded = false,
                    localPath = ""
                )
            }
        }

        return mAudioChaptersAdapter.data[position]
    }

    fun loadChapters(loadChaptersCallback: (Int) -> Unit) {
        mLoadChaptersCallback = loadChaptersCallback
    }

    fun handleChapters(handleChaptersCallback: (Int, VideoBookChaptersBean.VideoBookChaptersInfo) -> Unit) {
        mHandleChaptersCallback = handleChaptersCallback
    }

}