package com.yz.books.ui.video.player

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.yz.books.R
import com.yz.books.api.ApiConstant
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
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
import kotlinx.android.synthetic.main.activity_new_video_player.*
import kotlinx.android.synthetic.main.view_playback_controller.*
import java.io.File
import java.lang.ref.WeakReference
import kotlin.math.abs


/**
 * 视频播放
 *
 * @author lilin
 * @time on 2020-01-17 08:39
 */
class VideoPlayerActivity : BaseMVVMActivity<VideoBooksViewModel>() {

//region var/val

    private val mVoicePopupWindow by lazy(LazyThreadSafetyMode.NONE) {
        VoicePopupWindow(this) {
            //mPlayer?.volume = it / 100f
            //LogUtils.e("volume==${mPlayer?.volume}")
            mMediaPlayer?.setVolume(it / 100f, it / 100f)
        }
    }

    private val mVideoChaptersDialog by lazy(LazyThreadSafetyMode.NONE) {
        VideoChaptersDialog(
            this,
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        )
    }

    private val mPlayerHandler by lazy(LazyThreadSafetyMode.NONE) {
        PlayHandler(this)
    }

    private val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    //private var mPlayer: SimpleExoPlayer? = null
    private var mMediaPlayer: MediaPlayer? = null
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
        startPlay()
    }

    override fun onPause() {
        super.onPause()
        pausePlay()
    }

    override fun onStop() {
        super.onStop()
        stopPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgressDialog.dismissDialog()
        mVoicePopupWindow.dismiss()
        mVideoChaptersDialog.dismissDialog()

        mPlayerHandler.removeCallbacksAndMessages(null)

        stopPlay()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    override fun providerVMClass() = VideoBooksViewModel()

    override fun getLayoutId() = R.layout.activity_new_video_player

    override fun initView() {
        mSeekBar = seek_bar
        mTimeProgress = tv_time_progress
        initExoPlayer()

        mPlayerHandler.sendEmptyMessageDelayed(1, 15000)

        //mVoicePopupWindow.initVoiceValue(mPlayer?.volume ?: 0f)

        iv_player_voice.visibility = View.GONE
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
        /*mPlayer?.addListener(this)

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
        })*/

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekTo((seekBar?.progress ?: 0) * 1000)
            }
        })

        /*player_view.setControllerVisibilityListener {
            btn_back.visibility = it
            tv_video_name.visibility = it
        }*/

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
            /*if (mPlayer?.isPlaying == true) {
                iv_player_play.setImageResource(R.drawable.ic_player_start)
                mPlayer?.playWhenReady = false
                mPlayerHandler.removeCallbacksAndMessages(null)
            } else {
                iv_player_play.setImageResource(R.drawable.ic_player_pause)
                mPlayer?.playWhenReady = true
                mPlayerHandler.sendEmptyMessage(0)
            }*/
            if (mMediaPlayer?.isPlaying == true) {
                pausePlay()
                iv_player_play.setImageResource(R.drawable.ic_player_start)
                mPlayerHandler.removeCallbacksAndMessages(null)
            } else {
                startPlay()
                iv_player_play.setImageResource(R.drawable.ic_player_pause)
                mPlayerHandler.sendEmptyMessage(0)
            }
        }

        iv_player_voice.setOnClickListener {
            mVoicePopupWindow.showAtBottom(it)
        }

        iv_player_table.setOnClickListener {
            mVideoChaptersDialog.show()
        }

        cl_root.setOnClickListener {
            group_controller.visibility = View.VISIBLE
            focusIndicator.layout.visibility = View.VISIBLE
            mPlayerHandler.sendEmptyMessageDelayed(1, 5000)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                mPlayerHandler.removeMessages(1)
                group_controller.visibility = View.VISIBLE
                focusIndicator.layout.visibility = View.VISIBLE
                mPlayerHandler.sendEmptyMessageDelayed(1, 5000)
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (mVoicePopupWindow.isShowing) {
            mVoicePopupWindow.dismiss();
            return
        }
        super.onBackPressed()
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
            is VideoBooksModel.VideoBookChaptersState -> {
                handleVideoBookChaptersData(state.pageNum, state.videoBookChaptersBean)
            }
        }
    }

//endregion

//region private methods

    private fun startPlay() {
        if (mMediaPlayer?.isPlaying == false) {
            mMediaPlayer?.start()
        }
    }

    private fun stopPlay() {
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.stop()
        }
    }

    private fun pausePlay() {
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.pause()
        }
    }

    private fun seekTo(time: Int) {
        mMediaPlayer?.seekTo(time)
    }

    /*private fun releasePlayer() {
        mPlayer?.release()
        mPlayer = null
    }*/

    private fun initExoPlayer() {
        mMediaPlayer = MediaPlayer()
        initSurfaceviewStateListener()
        /*mPlayer = SimpleExoPlayer
            .Builder(this)
            .setTrackSelector(DefaultTrackSelector(this))
            .build()

        mVoicePopupWindow.initVoiceValue(mPlayer?.volume ?: 0f)

        player_view.player = mPlayer*/
    }

    private fun initSurfaceviewStateListener() {
        val surfaceHolder = video_play_surfaceview.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                mMediaPlayer?.setDisplay(holder)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }

            override fun surfaceCreated(holder: SurfaceHolder?) {

            }

        })
    }

    private fun setTimeProgress(player: MediaPlayer?, tvTimeProgress: TextView) {
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

    private class PlayHandler(activity: VideoPlayerActivity) : Handler() {
        val activityWeakf = WeakReference<VideoPlayerActivity>(activity)
        val playerActivity = activityWeakf.get()

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (playerActivity == null) {
                return
            }
            if (msg?.what == 0) {
                playerActivity.setTimeProgress(
                    playerActivity.mMediaPlayer,
                    playerActivity.mTimeProgress
                )
                playerActivity.mSeekBar.max = (playerActivity.mMediaPlayer?.duration ?: 0) / 1000
                playerActivity.mSeekBar.progress =
                    (playerActivity.mMediaPlayer?.currentPosition ?: 0) / 1000
                LogUtils.e("progress==${playerActivity.mSeekBar.max}//${playerActivity.mSeekBar.progress}")

                sendEmptyMessageDelayed(0, 1000)
            } else {
                if (!playerActivity.mVoicePopupWindow.isShowing) {
                    playerActivity.group_controller.visibility = View.GONE
                    playerActivity.focusIndicator.layout.visibility = View.GONE
                }
            }
        }
    }

    private fun getVideoBookChapters() {
        mVideoId?.let {
            mViewModel?.getVideoBookChapters(it, mPageNum)
        }
    }

    private fun handleVideoBookChaptersData(
        pageNum: Int,
        videoBookChaptersBean: VideoBookChaptersBean?
    ) {
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
            if (it.videoChapter.isNotEmpty() && mClickPosition < it.videoChapter.size) {
                val data = it.videoChapter[mClickPosition]
                handleVideoData(data, true)
            }
        }
    }

    /**
     * 播放video
     * @param url
     */
    private fun playVideo(url: String, videoName: String) {
        mVideoChaptersDialog.refreshItemData(mClickPosition, "")

        tv_video_name.text = videoName

        if (mMediaPlayer == null) return

        try {
            mMediaPlayer!!.apply {
                reset()
                setDataSource(url)
                setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT) //缩放模式
                //isLooping = true //设置循环播放
                prepareAsync() //异步准备

                setOnPreparedListener {
                    mSeekBar.max = duration
                    mPlayerHandler.sendEmptyMessage(0)
                    iv_player_play.setImageResource(R.drawable.ic_player_pause)

                    start()
                }

                setOnVideoSizeChangedListener { _, _, _ ->
                    changeVideoSize()
                }

                setOnErrorListener { _, _, _ ->
                    //showToast("视频播放错误")
                    iv_player_play.setImageResource(R.drawable.ic_player_start)
                    false
                }

                setOnCompletionListener {
                    iv_player_play.setImageResource(R.drawable.ic_player_start)
                    getPreNextData(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*tv_video_name.text = videoName
        //"file:///android_asset/test.mp4"
        mConcatenatingMediaSource.clear()

        val mediaSource = ProgressiveMediaSource.Factory(mDefaultDataSourceFactory).
            createMediaSource(Uri.parse(url))
        mConcatenatingMediaSource.addMediaSource(mediaSource)
        mPlayer?.playWhenReady = true
        mPlayer?.prepare(mConcatenatingMediaSource)

        mVideoChaptersDialog.refreshItemData(mClickPosition,"")

        iv_player_play.setImageResource(R.drawable.ic_player_pause)*/
    }

    /**
     * 下载或者播放视频
     * @param data
     */
    private fun handleVideoData(
        data: VideoBookChaptersBean.VideoBookChaptersInfo,
        first: Boolean? = null
    ) {
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
                chapterInfo.chapterUrl.startsWith("https")
            ) {
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

    /**
     * 修改视频的大小,以用来适配屏幕
     */
    private fun changeVideoSize() {
        var videoWidth = mMediaPlayer!!.videoWidth
        var videoHeight = mMediaPlayer!!.videoHeight
        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels
        Log.e(
            "==",
            "changeVideoSize: deviceHeight=" + deviceHeight + "deviceWidth=" + deviceWidth
        )
        var devicePercent = 0f
        //下面进行求屏幕比例,因为横竖屏会改变屏幕宽度值,所以为了保持更小的值除更大的值.
        devicePercent =
            if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
                deviceWidth.toFloat() / deviceHeight.toFloat() //竖屏状态下宽度小与高度,求比
            } else { //横屏
                deviceHeight.toFloat() / deviceWidth.toFloat() //横屏状态下高度小与宽度,求比
            }
        if (videoWidth > videoHeight) {
            if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                videoWidth = deviceWidth
                videoHeight = (videoHeight / devicePercent).toInt()
            } else {
                videoWidth = deviceWidth
                videoHeight = (deviceWidth * devicePercent).toInt()
            }
        } else {
            if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
                videoHeight = deviceHeight
                val videoPercent =
                    videoWidth.toFloat() / videoHeight.toFloat()
                val differenceValue = abs(videoPercent - devicePercent)
                videoWidth = if (differenceValue < 0.3) {
                    deviceWidth
                } else {
                    (videoWidth / devicePercent).toInt()
                }
            } else { //横屏
                videoHeight = deviceHeight
                videoWidth = (deviceHeight * devicePercent).toInt()
            }
        }
        val layoutParams =
            video_play_surfaceview.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = videoWidth
        layoutParams.height = videoHeight
        layoutParams.verticalBias = 0.5f
        layoutParams.horizontalBias = 0.5f
        video_play_surfaceview.layoutParams = layoutParams
    }

//endregion
}