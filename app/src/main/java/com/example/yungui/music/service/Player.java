package com.example.yungui.music.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;

/**
 * 处理音乐播放，以及响应硬件的加入
 * Created by 22892 on 2018/1/9.
 */

public  abstract class Player {
    //默认音量
    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    //消息提示或者有通知是的音量
    private static final float MEDIA_VOLUME_DUCK = 0.2f;
    //audio变成噪音比如耳机拔出
    private static final IntentFilter AUDIO_NOISY_INTENT_FILTER =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private final BroadcastReceiver audioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                //如果正在播放停止播放
                if (isPlaying()) {
                    pause();
                }
            }
        }
    };

    private AudioManager audioManager;
    private AudioFocusHelper audioFocusHelper;
    private Context applicationContext;
    private boolean isAudioNoisyReceiverRegistered;
    //是否有音频焦点
    private boolean isAudioFocus;

    public Player(@NonNull Context context) {
        applicationContext = context.getApplicationContext();
        audioManager = (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);
        audioFocusHelper = new AudioFocusHelper();
    }

    /**
     * 播放
     */
    public final void play() {
        //是否有焦点
        if (audioFocusHelper.requestAudioFocus()) {
            //注册接收器
            registerAudioNoisyReceiver();
            //播放
            onPlay();
        }

    }

    /**
     * 取消焦点，接触广播接收
     */
    public final void pause() {
        if (isAudioFocus) {
            audioFocusHelper.abandonAudioFocus();
        }
        unregisterAudioNoisyReceiver();
        onPause();
    }

    /**
     * 释放焦点，解除广播接收，释放资源
     */
    public final void stop() {
        audioFocusHelper.abandonAudioFocus();
        unregisterAudioNoisyReceiver();
        //调用
        onStop();
    }



    public abstract void onPlay();


    public abstract void playFromMedia(MediaMetadataCompat mediaMetadataCompat);

    public abstract MediaMetadataCompat getCurrentMedia();

    public abstract boolean isPlaying();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void seekTo(long position);

    /**
     * 音量调节
     * @param mediaVolumeDefault
     */
    public abstract void setVolume(float mediaVolumeDefault);

    //注册广播接收
    private void registerAudioNoisyReceiver() {
        if (!isAudioNoisyReceiverRegistered) {
            applicationContext.registerReceiver(audioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER);
            isAudioNoisyReceiverRegistered = true;
        }

    }

    //解除广播接受
    private void unregisterAudioNoisyReceiver() {
        if (isAudioNoisyReceiverRegistered) {
            applicationContext.unregisterReceiver(audioNoisyReceiver);
            isAudioNoisyReceiverRegistered = false;
        }

    }

    /**
     * 管理音频焦点的类
     */
    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {

        //请求获取焦点
        private boolean requestAudioFocus() {
            final int result = audioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        //取消焦点
        private void abandonAudioFocus() {
            audioManager.abandonAudioFocus(this);
        }
        /**
         * 处理焦点事件
         *
         * @param focusChange
         */
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                //获得焦点
                case AudioManager.AUDIOFOCUS_GAIN:
                    //有焦点 ，没有播放
                    if (isAudioFocus && !isPlaying()) {
                        play();
                    } else if (isPlaying()) {
                        //有焦点在播放，设置音量
                        setVolume(MEDIA_VOLUME_DEFAULT);
                    }
                    //重置焦点
                    isAudioFocus = false;
                    break;
                //短暂失去焦点，比如说到信息发出提示音，需要较小音量
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    setVolume(MEDIA_VOLUME_DUCK);
                    break;
                //暂时失去焦点
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //停止播放，重置焦点
                    if (isPlaying()) {
                        isAudioFocus = true;
                        pause();
                    }

                    break;
                //永久失去焦点，取消对音频焦点的监听,停止播放
                case AudioManager.AUDIOFOCUS_LOSS:
                    audioFocusHelper.abandonAudioFocus();
                    isAudioFocus = false;
                    stop();
                    break;
            }

        }

    }


}
