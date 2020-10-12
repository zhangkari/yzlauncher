package com.yz.books.ui.video.player;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.yz.books.R;

import java.io.IOException;

/**
 * @author lilin
 * @time on 2020/4/17 下午9:09
 */
public class NewVideoPlayerActivity extends Activity {

    private MediaPlayer mMediaPlayer;

    private SurfaceView mVideoPlaySurfaceview;

    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_new_video_player);

        mVideoPlaySurfaceview = findViewById(R.id.video_play_surfaceview);

        initMediaPlayer();
        initSurfaceviewStateListener();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    private void initSurfaceviewStateListener() {
        mSurfaceHolder = mVideoPlaySurfaceview.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.setDisplay(holder);//给mMediaPlayer添加预览的SurfaceHolder
                setPlayVideo("/mnt/sdcard/yzbooks/12记叙文阅读/人物形象的塑造——肖像描写.mp4");//添加播放视频的路径
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //Log.e(TAG, "surfaceChanged触发: width=" + width + "height" + height);

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void setPlayVideo(String path) {
        try {
            mMediaPlayer.setDataSource(path);//
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);//缩放模式
            mMediaPlayer.setLooping(true);//设置循环播放
            mMediaPlayer.prepareAsync();//异步准备
//            mMediaPlayer.prepare();//同步准备,因为是同步在一些性能较差的设备上会导致UI卡顿
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { //准备完成回调
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //isInitFinish = true;
                    mMediaPlayer.start();
                    /*mHandler.sendEmptyMessage(GET_VIDEO_PLAY_TIME_KEY);
                    mTotalPlayTime = mMediaPlayer.getDuration();
                    if (mTotalPlayTime == -1){
                        Toast.makeText(VideoPlayActivity.this, "视频文件时间异常", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    mVedioSeek.setMax(mTotalPlayTime/1000);
                    mVedioTotalTimeTextView.setText(formatTime(mTotalPlayTime));*/
                }
            });
            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() { //尺寸变化回调
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    changeVideoSize();

                    //seekTo(mCurrentPlayTime);

                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("==","视频播放错误,错误原因what="+what+"extra="+extra);
                    return false;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改视频的大小,以用来适配屏幕
     */
    public void changeVideoSize() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = getResources().getDisplayMetrics().heightPixels;
        Log.e("==", "changeVideoSize: deviceHeight="+deviceHeight+"deviceWidth="+deviceWidth);
        float devicePercent = 0;
        //下面进行求屏幕比例,因为横竖屏会改变屏幕宽度值,所以为了保持更小的值除更大的值.
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
            devicePercent = (float) deviceWidth / (float) deviceHeight; //竖屏状态下宽度小与高度,求比
        }else { //横屏
            devicePercent = (float) deviceHeight / (float) deviceWidth; //横屏状态下高度小与宽度,求比

        }

        if (videoWidth > videoHeight){
            if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                videoWidth = deviceWidth;
                videoHeight = (int)(videoHeight/devicePercent);

            }else {
                videoWidth = deviceWidth;
                videoHeight = (int)(deviceWidth*devicePercent);
            }

        }else {
            if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏

                videoHeight = deviceHeight;
                float videoPercent = (float) videoWidth / (float) videoHeight;
                float differenceValue = Math.abs(videoPercent - devicePercent);
                if (differenceValue < 0.3){
                    videoWidth = deviceWidth;
                }else {
                    videoWidth = (int)(videoWidth/devicePercent);
                }


            }else { //横屏
                videoHeight = deviceHeight;
                videoWidth = (int)(deviceHeight*devicePercent);

            }

        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mVideoPlaySurfaceview.getLayoutParams();
        layoutParams.width = videoWidth;
        layoutParams.height = videoHeight;
        layoutParams.verticalBias = 0.5f;
        layoutParams.horizontalBias = 0.5f;
        mVideoPlaySurfaceview.setLayoutParams(layoutParams);

    }
}
