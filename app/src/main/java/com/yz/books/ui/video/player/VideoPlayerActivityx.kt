package com.yz.books.ui.video.player

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.yz.books.R
import com.yz.books.api.ApiConstant
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ext.dismissDialog
import com.yz.books.ext.isNetworkConnected
import com.yz.books.ext.showToast
import com.yz.books.ui.video.VideoBooksModel
import com.yz.books.ui.video.VideoBooksViewModel
import com.yz.books.ui.video.bean.VideoBookChaptersBean
import com.yz.books.utils.DownloadUtils
import com.yz.books.utils.FileMD5Utils
import com.yz.books.utils.FileUtils
import com.yz.books.utils.LogUtils
import com.yz.books.widget.dialog.LoadingDialog
import com.yz.books.widget.dialog.VideoChaptersDialog
import com.yz.books.widget.pop.VoicePopupWindow
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.view_playback_controller.*
import java.io.File
import java.lang.ref.WeakReference


/**
 * 视频播放
 *
 * @author lilin
 * @time on 2020-01-17 08:39
 */
class VideoPlayerActivityx : BaseMVVMActivity<VideoBooksViewModel>(), Player.EventListener {

//region var/val

    private val mDefaultDataSourceFactory: DefaultDataSourceFactory
        get() = DefaultDataSourceFactory(this, "audio/mpeg")

    private val mConcatenatingMediaSource by lazy(LazyThreadSafetyMode.NONE) {
        ConcatenatingMediaSource()
    }

    private val mVoicePopupWindow by lazy(LazyThreadSafetyMode.NONE) {
        VoicePopupWindow(this) {
            mPlayer?.volume = it / 100f
            //LogUtils.e("volume==${mPlayer?.volume}")
        }
    }

    private val mVideoChaptersDialog by lazy(LazyThreadSafetyMode.NONE) {
        VideoChaptersDialog(this,
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    private val mPlayerHandler by lazy(LazyThreadSafetyMode.NONE) {
        PlayHandler(this)
    }

    private val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var mSeekBar: SeekBar
    private lateinit var mTimeProgress: TextView

    private var mVideoId: Int? = null
    private var mBookName: String? = null
    private var mPageNum = 1

    /**
     * 当前播放的章节位置
     */
    private var mClickPosition = 0

//endregion

//region implement methods

//endregion

//region public methods

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || player_view == null) {
            initExoPlayer()
            player_view?.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            player_view?.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            player_view?.onPause()
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgressDialog.dismissDialog()
        mVoicePopupWindow.dismiss()
        mVideoChaptersDialog.dismissDialog()
        mPlayer?.removeListener(this)
        releasePlayer()
        mPlayerHandler.removeCallbacksAndMessages(null)
    }

    override fun providerVMClass() = VideoBooksViewModel()

    override fun getLayoutId() = R.layout.activity_video_player

    override fun initView() {
        mSeekBar = seek_bar
        mTimeProgress = tv_time_progress
        initExoPlayer()

        mVoicePopupWindow.initVoiceValue(mPlayer?.volume ?: 0f)
    }

    override fun initData() {
        val videoInfo = intent?.extras?.getString(Constant.VIDEO_INFO_KEY_EXTRA) ?: ","
        val (videoId, videoName) = videoInfo.split(",")
        mVideoId = videoId.toIntOrNull()
        mBookName = videoName
        getVideoBookChapters()
        //playVideo()
    }

    override fun initListener() {
        super.initListener()
        mPlayer?.addListener(this)

        mConcatenatingMediaSource.addEventListener(mPlayerHandler, object :
            MediaSourceEventListener {
            override fun onLoadStarted(
                windowIndex: Int,
                mediaPeriodId: MediaSource.MediaPeriodId?,
                loadEventInfo: MediaSourceEventListener.LoadEventInfo?,
                mediaLoadData: MediaSourceEventListener.MediaLoadData?
            ) {
                mSeekBar.max = mPlayer?.duration?.toInt() ?: 0
                mPlayerHandler.sendEmptyMessage(0)
            }
        })

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mPlayer?.seekTo(seekBar?.progress?.toLong() ?: 0L)
            }
        })

        player_view.setControllerVisibilityListener {
            btn_back.visibility = it
            tv_video_name.visibility = it
        }

        btn_back.setOnClickListener {
            finish()
        }

        iv_player_previous.setOnClickListener {
            //mPlayer?.previous()
            getPreNextData(false)
        }

        iv_player_next.setOnClickListener {
            //mPlayer?.next()
            getPreNextData(true)
        }

        iv_player_play.setOnClickListener {
            if (mPlayer?.isPlaying == true) {
                iv_player_play.setImageResource(R.drawable.ic_player_start)
                mPlayer?.playWhenReady = false
                mPlayerHandler.removeCallbacksAndMessages(null)
            } else {
                iv_player_play.setImageResource(R.drawable.ic_player_pause)
                mPlayer?.playWhenReady = true
                mPlayerHandler.sendEmptyMessage(0)
            }
        }

        iv_player_voice.setOnClickListener {
            mVoicePopupWindow.showAtBottom(it)
        }

        iv_player_table.setOnClickListener {
            mVideoChaptersDialog.show()
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
            is VideoBooksModel.VideoBookChaptersState -> {
                handleVideoBookChaptersData(state.pageNum, state.videoBookChaptersBean)
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        LogUtils.e("onPlayerStateChanged==$playbackState")
        if (playbackState == Player.STATE_ENDED) {
            iv_player_play.setImageResource(R.drawable.ic_player_start)
            getPreNextData(true)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            iv_player_play.setImageResource(R.drawable.ic_player_pause)
        } else {
            iv_player_play.setImageResource(R.drawable.ic_player_start)
        }
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        if (isLoading) {
            showLoading()
        } else {
            dismissLoading()
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        showToast(error.message)
        iv_player_play.setImageResource(R.drawable.ic_player_start)
    }

//endregion

//region private methods

    private fun releasePlayer() {
        mPlayer?.release()
        mPlayer = null
    }

    private fun initExoPlayer() {
        mPlayer = SimpleExoPlayer
            .Builder(this)
            .setTrackSelector(DefaultTrackSelector(this))
            .build()

        mVoicePopupWindow.initVoiceValue(mPlayer?.volume ?: 0f)

        player_view.player = mPlayer

    }

    private fun setTimeProgress(player: ExoPlayer?, tvTimeProgress: TextView) {
        val duration = player?.duration ?: 0
        val totalSeconds = duration / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val totalTime = String.format("%02d:%02d", minutes, seconds)

        val duration1 = player?.currentPosition ?: 0
        val totalSeconds1 = duration1 / 1000
        val seconds1 = totalSeconds1 % 60
        val minutes1 = (totalSeconds1 / 60) % 60
        var currentTime = String.format("%02d:%02d", minutes1, seconds1)
        //LogUtils.e("audio==${time}//${time1}//${mPlayer?.playbackParameters?.pitch}")
        if (totalSeconds1 > totalSeconds) {
            currentTime = totalTime
        }

        tvTimeProgress.text = "$currentTime/$totalTime"
    }

    private class PlayHandler(activity: VideoPlayerActivityx) : Handler() {
        val activityWeakf = WeakReference<VideoPlayerActivityx>(activity)
        val playerActivity = activityWeakf.get()

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (playerActivity == null) {
                return
            }
            if (msg?.what == 0) {
                playerActivity.setTimeProgress(playerActivity.mPlayer, playerActivity.mTimeProgress)
                playerActivity.mSeekBar.max = playerActivity.mPlayer?.duration?.toInt() ?: 0
                playerActivity.mSeekBar.progress = playerActivity.mPlayer?.currentPosition?.toInt() ?: 0

                sendEmptyMessageDelayed(0, 500)
            }
        }
    }

    private fun getVideoBookChapters() {
        mVideoId?.let {
            mViewModel?.getVideoBookChapters(it, mPageNum)
        }
    }

    private fun handleVideoBookChaptersData(pageNum: Int,
                                            videoBookChaptersBean: VideoBookChaptersBean?) {
        mClickPosition = 0

        if (videoBookChaptersBean != null) {
            for ((index, value) in videoBookChaptersBean.videoChapter.withIndex()) {
                val path = "${FileUtils.getLocalPath()}${value.chapterUrl}"
                if (File(path).exists()) {
                    videoBookChaptersBean.videoChapter[index].downloaded = true
                    videoBookChaptersBean.videoChapter[index].localPath = path
                }
            }
            /*val path = "${FileUtils.getLocalPath()}$mVideoId$mBookName"
            val file = File(path)
            file.listFiles { dir, name ->
                LogUtils.e("fileList==$dir/$name")
                for ((index, value) in videoBookChaptersBean.videoChapter.withIndex()) {
                    if (name in value.chapterUrl) {
                        videoBookChaptersBean.videoChapter[index].downloaded = true
                        videoBookChaptersBean.videoChapter[index].localPath = "$dir/$name"
                    }
                }
                false
            }*/
        }

        mVideoChaptersDialog.apply {
            show()
            setAudioChapters(mBookName, videoBookChaptersBean)
            loadChapters {
                mPageNum = it
                getVideoBookChapters()
            }
            handleChapters { position, data ->
                mClickPosition = position
                handleVideoData(data)
            }
        }

        videoBookChaptersBean?.let {
            val data = it.videoChapter[mClickPosition]
            handleVideoData(data, true)
        }
    }

    /**
     * 播放video
     * @param url
     */
    private fun playVideo(url: String, videoName: String) {
        tv_video_name.text = videoName
        //"file:///android_asset/test.mp4"
        mConcatenatingMediaSource.clear()

        val mediaSource = ProgressiveMediaSource.Factory(mDefaultDataSourceFactory).
            createMediaSource(Uri.parse(url))
        mConcatenatingMediaSource.addMediaSource(mediaSource)
        mPlayer?.playWhenReady = true
        mPlayer?.prepare(mConcatenatingMediaSource)

        mVideoChaptersDialog.refreshItemData(mClickPosition,"")

        iv_player_play.setImageResource(R.drawable.ic_player_pause)
    }

    /**
     * 下载或者播放视频
     * @param data
     */
    private fun handleVideoData(data: VideoBookChaptersBean.VideoBookChaptersInfo,
                                first: Boolean? = null) {
        if (data.downloaded && data.fileMd5String == FileMD5Utils.getFileMD5(File(data.localPath))) {
            playVideo(data.localPath, data.chapterName)
            if (first == true) {
                mVideoChaptersDialog.dismissDialog()
            }
        } else {
            FileUtils.deleteFile(data.localPath)
            downloadAudio(data)
        }
    }

    /**
     * 获取上下一章数据
     * @param next
     */
    private fun getPreNextData(next: Boolean) {
        val index = if (next) {
            mClickPosition + 1
        } else {
            mClickPosition - 1
        }
        LogUtils.e("mClickPosition==$mClickPosition//$index")
        val data = mVideoChaptersDialog.getPreviousNextChapter(index)
        if (data == null) {
            if (next) {
                showToast("已是最后一章了")
            } else {
                showToast("已是第一章了")
            }
            return
        }

        when (data.chapterId) {
            -1 -> {
                mPageNum++
                getVideoBookChapters()
            }
            -2 -> {
                mPageNum++
                getVideoBookChapters()
            }
            else -> {
                mClickPosition = index
                handleVideoData(data)
            }
        }
    }

    private fun downloadAudio(chapterInfo: VideoBookChaptersBean.VideoBookChaptersInfo) {
        chapterInfo.apply {
            if (!chapterUrl.endsWith(".mp4") || !isNetworkConnected()) {
                return@apply
            }
            val fileName = chapterUrl.substring(chapterUrl.lastIndexOf("/") + 1)
            val file = FileUtils.createTempFile(fileName, chapterUrl.replace("/$fileName", ""))
            //createAudioFile("$mVideoId$mBookName", fileName)
            LogUtils.e("filepath==${file.path}//${file.exists()}")
            val url = if (chapterInfo.chapterUrl.startsWith("http") ||
                chapterInfo.chapterUrl.startsWith("https")) {
                chapterInfo.chapterUrl
            } else {
                ApiConstant.HOST + chapterInfo.chapterUrl
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
                            path?.let {
                                LogUtils.e("md5==${FileMD5Utils.getFileMD5(File(it))}")
                                if (FileMD5Utils.getFileMD5(File(it)) == chapterInfo.fileMd5String) {
                                    mVideoChaptersDialog.refreshItemData(mClickPosition, it)
                                    playVideo(it, chapterInfo.chapterName)
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