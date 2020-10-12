package com.yz.books.ui.audio.player

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.widget.ImageView
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
import com.yz.books.ui.audio.AudioBooksModel
import com.yz.books.ui.audio.AudioBooksViewModel
import com.yz.books.ui.audio.bean.AudioBookChaptersBean
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.utils.*
import com.yz.books.widget.dialog.AudioChaptersDialog
import com.yz.books.widget.dialog.LoadingDialog
import com.yz.books.widget.pop.VoicePopupWindow
import kotlinx.android.synthetic.main.activity_audio_player.*
import java.io.File
import java.lang.ref.WeakReference

/**
 * 音频播放界面
 *
 * @author lilin
 * @time on 2020-01-03 16:59
 */
class AudioPlayerActivity : BaseMVVMActivity<AudioBooksViewModel>(), Player.EventListener {

//region var/val

    private val mVoicePopupWindow by lazy(LazyThreadSafetyMode.NONE) {
        VoicePopupWindow(this) {
            mPlayer?.volume = it / 100f
            //LogUtils.e("volume==${mPlayer?.volume}")
        }
    }

    private val mPlayerHandler by lazy(LazyThreadSafetyMode.NONE) {
        PlayHandler(this)
    }

    private val mConcatenatingMediaSource by lazy(LazyThreadSafetyMode.NONE) {
        ConcatenatingMediaSource()
    }

    private val mAudioChaptersDialog by lazy(LazyThreadSafetyMode.NONE) {
        AudioChaptersDialog(this,
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    private val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    /**
     * 正在播放
     */
    private var mIsPlaying = false
    /**
     * 重新播放
     */
    private var mRepeatPlay = false

    private val mDefaultDataSourceFactory: DefaultDataSourceFactory
        get() = DefaultDataSourceFactory(this, "audio/mpeg")

    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var mSeekBar: SeekBar
    private lateinit var mTimeProgress: TextView

    private var mAudioId: Int? = null
    private var mBookName: String? = null
    private var mPageNum = 1

    /**
     * 当前播放的章节位置
     */
    private var mClickPosition = 0

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        mProgressDialog.dismissDialog()
        mVoicePopupWindow.dismiss()
        mAudioChaptersDialog.dismissDialog()
        mPlayer?.removeListener(this)
        mPlayer?.release()
        mPlayerHandler.removeCallbacksAndMessages(null)
    }

    override fun providerVMClass() = AudioBooksViewModel()

    override fun getLayoutId() = R.layout.activity_audio_player

    override fun initView() {
        mSeekBar = seek_bar
        mTimeProgress = tv_time_progress
    }

    override fun initData() {
        val audioBookInfo = intent?.extras?.getSerializable(Constant.AUDIO_BOOK_DETAIL_KEY_EXTRA)
                as AudioBookDetailBean.AudioBookDetailInfo?
        if (audioBookInfo != null) {
            setAudioBookInfo(audioBookInfo)
            getAudioBookChapters()
        }

        initExoPlayer()
        mVoicePopupWindow.initVoiceValue(mPlayer?.volume ?: 0f)
    }

    private fun setAudioBookInfo(audioBookInfo: AudioBookDetailBean.AudioBookDetailInfo) {
        with(audioBookInfo) {
            mAudioId = audioId
            mBookName = audioName
            ImageLoaderUtils.withBookCover(coverImg.addFileHostUrl(), iv_cover, ImageView.ScaleType.FIT_XY)
            tv_book_name.text = audioName
            tv_book_author.text = "$author 著"
        }
    }

    override fun initListener() {
        super.initListener()
        mPlayer?.addListener(this)

        mConcatenatingMediaSource.addEventListener(mPlayerHandler, object : MediaSourceEventListener {
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
            if (mIsPlaying) {
                iv_player_play.setImageResource(R.drawable.ic_player_start)
                mPlayer?.playWhenReady = false
                mPlayerHandler.removeCallbacksAndMessages(null)
            } else {
                iv_player_play.setImageResource(R.drawable.ic_player_pause)
                if (mRepeatPlay) {
                    mRepeatPlay = false
                    mPlayer?.seekTo(0)
                }
                mPlayer?.playWhenReady = true
                mPlayerHandler.sendEmptyMessage(0)
            }
        }

        iv_player_voice.setOnClickListener {
            mVoicePopupWindow.showAtBottom(it)
        }

        iv_player_table.setOnClickListener {
            mAudioChaptersDialog.show()
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
            is AudioBooksModel.AudioBookChaptersState -> {
                handleAudioBookChaptersData(state.pageNum, state.audioBookChaptersBean)
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        LogUtils.e("onPlayerStateChanged=$playWhenReady//$playbackState")
        mRepeatPlay = false
        if (playbackState == Player.STATE_ENDED) {
            mRepeatPlay = true
            iv_player_play.setImageResource(R.drawable.ic_player_start)
            getPreNextData(true)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        LogUtils.e("onIsPlayingChanged=$isPlaying")
        mIsPlaying = isPlaying
        if (isPlaying) {
            iv_player_play.setImageResource(R.drawable.ic_player_pause)
        } else {
            iv_player_play.setImageResource(R.drawable.ic_player_start)
        }

        val duration = mPlayer?.duration ?: 0
        val totalSeconds = duration / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val time = String.format("%02d:%02d", minutes, seconds)

        val duration1 = mPlayer?.currentPosition ?: 0
        val totalSeconds1 = duration1 / 1000
        val seconds1 = totalSeconds1 % 60
        val minutes1 = (totalSeconds1 / 60) % 60
        val time1 = String.format("%02d:%02d", minutes1, seconds1)
        LogUtils.e("audio==${time}//${time1}//${mPlayer?.volume}")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        LogUtils.e("onLoadingChanged=$isLoading")
        if (isLoading) {
            showLoading()
        } else {
            dismissLoading()
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        showToast(error.message)
        mIsPlaying = false
        mRepeatPlay = true
        iv_player_play.setImageResource(R.drawable.ic_player_start)
    }

//endregion

//region public methods

//endregion

//region private methods

    private fun getAudioBookChapters() {
        mAudioId?.let {
            mViewModel?.getAudioBookChapters(it, mPageNum)
        }
    }

    private fun initExoPlayer() {
        mPlayer = SimpleExoPlayer
            .Builder(this)
            .setTrackSelector(DefaultTrackSelector(this))
            .build()
        /*//val defaultDataSourceFactory = DefaultDataSourceFactory(this, "audio/mpeg") //  userAgent -> audio/mpeg  不能为空
        //val concatenatingMediaSource = ConcatenatingMediaSource() //创建一个媒体连接源
        var mediaSource = ProgressiveMediaSource.Factory(mDefaultDataSourceFactory).
            createMediaSource(Uri.parse("file:///android_asset/hyl.mp3"))
        mConcatenatingMediaSource.addMediaSource(mediaSource)
        mediaSource = ProgressiveMediaSource.Factory(mDefaultDataSourceFactory).
            createMediaSource(Uri.parse("file:///android_asset/xlt.mp3"))
        mConcatenatingMediaSource.addMediaSource(mediaSource)
        //val loopMediaSource = LoopingMediaSource(mConcatenatingMediaSource)

        mPlayer?.playWhenReady = true
        //mPlayer?.prepare(loopMediaSource) //实现多个音频循环播放
        mPlayer?.prepare(mConcatenatingMediaSource) //实现多个音频播放*/
    }

    /**
     * 播放audio
     * @param url
     */
    private fun playAudio(url: String) {
        mConcatenatingMediaSource.clear()

        val mediaSource = ProgressiveMediaSource.Factory(mDefaultDataSourceFactory).
            createMediaSource(Uri.parse(url))
        mConcatenatingMediaSource.addMediaSource(mediaSource)
        mPlayer?.playWhenReady = true
        mPlayer?.prepare(mConcatenatingMediaSource)

        mAudioChaptersDialog.refreshItemData(mClickPosition,"")
    }

    private fun handleAudioBookChaptersData(pageNum: Int,
                                            audioBookChaptersBean: AudioBookChaptersBean?) {
        mClickPosition = 0

        if (audioBookChaptersBean != null) {
            for ((index, value) in audioBookChaptersBean.audioChapters.withIndex()) {
                val path = "${FileUtils.getLocalPath()}${value.chapterUrl}"
                if (File(path).exists()) {
                    audioBookChaptersBean.audioChapters[index].downloaded = true
                    audioBookChaptersBean.audioChapters[index].localPath = path

                    /*val fileName = value.chapterUrl.substring(value.chapterUrl.lastIndexOf("/") + 1)
                    FileUtils.copyFolder(FileUtils.getLocalPath()+value.chapterUrl.replace(fileName, ""),
                    FileUtils.getLocalPath()+"1test2"+value.chapterUrl.replace(fileName, ""))*/
                }
            }
            /*val path = "${FileUtils.getLocalPath()}$mAudioId$mBookName"
            val file = File(path)
            file.listFiles { dir, name ->
                LogUtils.e("fileList==$dir/$name")
                for ((index, value) in audioBookChaptersBean.audioChapters.withIndex()) {
                    if (name in value.chapterUrl) {
                        audioBookChaptersBean.audioChapters[index].downloaded = true
                        audioBookChaptersBean.audioChapters[index].localPath = "$dir/$name"
                    }
                }
                false
            }*/
        }

        mAudioChaptersDialog.apply {
            show()
            setAudioChapters(mBookName, audioBookChaptersBean)
            loadChapters {
                mPageNum = it
                getAudioBookChapters()
            }
            handleChapters { position, data ->
                mClickPosition = position
                handleAudioData(data)
            }
        }

        audioBookChaptersBean?.let {
            if (it.audioChapters.isNotEmpty() && mClickPosition < it.audioChapters.size) {
                val data = it.audioChapters[mClickPosition]
                handleAudioData(data, true)
            }
        }
    }

    /**
     * 下载或者播放音频
     * @param data
     * @param first 是否首次
     */
    private fun handleAudioData(data: AudioBookChaptersBean.AudioBookChapterInfo,
                                first: Boolean? = null) {
        if (data.downloaded && data.fileMd5String == FileMD5Utils.getFileMD5(File(data.localPath))) {
            playAudio(data.localPath)
            if (first == true) {
                mAudioChaptersDialog.dismissDialog()
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
        val data = mAudioChaptersDialog.getPreviousNextChapter(index)
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
                getAudioBookChapters()
            }
            -2 -> {
                mPageNum++
                getAudioBookChapters()
            }
            else -> {
                mClickPosition = index
                handleAudioData(data)
            }
        }
    }

    private fun downloadAudio(chapterInfo: AudioBookChaptersBean.AudioBookChapterInfo) {
        chapterInfo.apply {
            if (!chapterUrl.endsWith(".mp3") || !isNetworkConnected()) {
                return@apply
            }
            val fileName = chapterUrl.substring(chapterUrl.lastIndexOf("/") + 1)
            val file = FileUtils.createTempFile(fileName, chapterUrl.replace("/$fileName", ""))
            LogUtils.e("filepath==${file.path}")
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
                                if (FileMD5Utils.getFileMD5(File(it)) == chapterInfo.fileMd5String) {
                                    mAudioChaptersDialog.refreshItemData(mClickPosition, it)
                                    playAudio(it)
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

    private class PlayHandler(activity: AudioPlayerActivity) : Handler() {
        val activityWeakf = WeakReference<AudioPlayerActivity>(activity)
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

//endregion
}