package com.example.yungui.music.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.IOException;

/**
 * 真正播放音乐的类
 * Created by 22892 on 2018/1/9.
 */

public class MediaPlayerAdapter extends Player {
    public static final String TAG = MediaPlayerAdapter.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private PlaybackInfoListener playbackInfoListener;
    private MediaMetadataCompat currentMedia;
    private String currentPath;
    private Context context;
    private int state;
    //当前媒体是否播放完的标志
    private boolean currentMediaPlayedToCompletion;

    //解决MediaPlayer不播放时拖动进度条的bug
    private long mSeekWhileNotPlaying = -1;


    public MediaPlayerAdapter(@NonNull Context context, PlaybackInfoListener playbackInfoListener) {
        super(context);
        this.playbackInfoListener = playbackInfoListener;
        this.context = context.getApplicationContext();
    }

    /**
     * 当一个mediaPlayer释放后，不能再重用，所以需要创建一个新的MediaPlayer,
     * 在onStart()和onStop()中分别重建和释放MediaPlayer
     */
    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new android.media.MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mp) {
                    //播放完成
                    playbackInfoListener.OnPlayCompleted();
                    setNewState(PlaybackStateCompat.STATE_PAUSED);
                }
            });
        }

    }


    @Override
    public void playFromMedia(MediaMetadataCompat mediaMetadataCompat) {
        Log.e(TAG, "playFromMedia: 播放" + mediaMetadataCompat.getDescription());
        currentMedia = mediaMetadataCompat;
        final String path = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);
        playFile(path);
    }

    @Override
    public void onPlay() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            //更新状态
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }

    }


    @Override
    public MediaMetadataCompat getCurrentMedia() {
        return currentMedia;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public void onPause() {
        if (isPlaying()) {
            mMediaPlayer.pause();
        }
        setNewState(PlaybackStateCompat.STATE_PAUSED);

    }

    @Override
    public void onStop() {
        setNewState(PlaybackStateCompat.STATE_PAUSED);
        release();
    }

    @Override
    public void seekTo(long position) {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mSeekWhileNotPlaying = position;
            }
        }
        mMediaPlayer.seekTo((int) position);
        setNewState(state);


    }

    @Override
    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }

    }

    //更新状态
    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        Log.d(TAG, "setNewState: " + newPlayerState);
        state = newPlayerState;
        //停止状态 播放完一首歌
        if (state == PlaybackStateCompat.STATE_STOPPED) {
            currentMediaPlayedToCompletion = true;
        }
        //播放的位置
        final long reportPosition;
        if (mSeekWhileNotPlaying >= 0) {
            reportPosition = mSeekWhileNotPlaying;

            if (state == PlaybackStateCompat.STATE_PLAYING) {
                mSeekWhileNotPlaying = -1;
            }
        } else {
            reportPosition = mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
        }

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(state,
                reportPosition,
                1.0f,
                SystemClock.elapsedRealtime());
        //通知监听器状态改变
        playbackInfoListener.OnPlaybackInfoChanged(stateBuilder.build());

    }

    private void playFile(String path) {
        boolean mediaChanged = (currentPath == null || !path.equals(currentPath));
        //如果当前播放完成
        if (currentMediaPlayedToCompletion) {
            mediaChanged = true;
            currentMediaPlayedToCompletion = false;
        }
        //数据没有发生改变
        if (!mediaChanged) {
            //没有在播放
            if (!isPlaying()) {
                //播放
                play();
            }
            //退出
            return;
        } else {
            //发生改变
            release();
        }
        currentPath = path;
        //重新初始化播放器
        initializeMediaPlayer();
        try {
            //设置资源
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file" + path);
        }
        try {
            //准备资源
            mMediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file" + path);
        }
        //播放,在获取焦点注册监听之后会调用onPlay()方法
        play();
    }

    /**
     * 重置播放器
     */
    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 返回对应状态下，接下来能进行操作的状态
     *
     * @return
     */
    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        //默认返回
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (state) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }
}
