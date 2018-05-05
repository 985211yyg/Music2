/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.yungui.music.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

/**
 * 可以使用MediaSession进行追踪，查找的seekbar
 */

public class MediaSeekBar extends AppCompatSeekBar {
    private static final String TAG = "MediaSeekBar";
    private MediaControllerCompat mMediaController;
    private ControllerCallback mControllerCallback;
    private boolean isMediaMetadataChange;

    private boolean mIsTrackTouch = false;
    //状态回调
    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        //进度改变的回调
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        //开始拖动seekBar
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsTrackTouch = true;
        }

        //用户已经停止拖动seekBar此时的进度将用于播放
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaController.getTransportControls().seekTo(getProgress());
            mIsTrackTouch = false;
        }
    };
    //进度动画
    private ValueAnimator mProgressAnimator;

    /**
     * 构造方法，并且设置啊监听器
     *
     * @param context
     */
    public MediaSeekBar(Context context) {
        super(context);
        //设置回调监听
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    public MediaSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    public MediaSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    //设置监听的方法
    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        // Prohibit adding seek listeners to this subclass.
    }

    /**
     * 对外公开，传入mediaController
     *
     * @param mediaController
     */
    public void setMediaController(final MediaControllerCompat mediaController) {
        if (mediaController != null) {
            mControllerCallback = new ControllerCallback();
            mediaController.registerCallback(mControllerCallback);
            if (mediaController.getPlaybackState() != null && mediaController.getMetadata() != null) {
                //初始化时设置进度
                mControllerCallback.onMetadataChanged(mediaController.getMetadata());
                mControllerCallback.onPlaybackStateChanged(mediaController.getPlaybackState());
            }
        } else if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
        }
        mMediaController = mediaController;
    }

    /**
     * 对外公开 断开链接
     */
    public void disconnectController() {
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
            mMediaController = null;
        }
    }

    /**
     * MediaControllerCompat的回调方法
     */
    private class ControllerCallback extends MediaControllerCompat.Callback
            implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        //播放状态改变
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            // If there's an ongoing animation, stop it now.
            if (mProgressAnimator != null) {
                mProgressAnimator.cancel();
                mProgressAnimator = null;
            }
            //主要针对直接进入详情页面点击按钮进行播放时没有设置总进度的情况
            if (!isMediaMetadataChange && mMediaController.getMetadata() != null) {
                setMax((int) mMediaController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            }
            //获取进度
            final int progress = state != null ? (int) state.getPosition() : 0;
            //设置进度
            setProgress(progress);
            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                final int timeToEnd = (int) ((getMax() - progress) / state.getPlaybackSpeed());
                mProgressAnimator = ValueAnimator.ofInt(progress, getMax()).setDuration(timeToEnd);
                mProgressAnimator.setInterpolator(new LinearInterpolator());
                mProgressAnimator.addUpdateListener(this);
                mProgressAnimator.start();
            }
        }

        /**
         * 播放数据改变
         *
         * @param metadata
         */
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            isMediaMetadataChange = true;
            int max = metadata != null ? (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) : 0;
            Log.e(TAG, "onMetadataChanged: Max" + max);
            setProgress(0);
            setMax(max);
        }

        //动画回调
        @Override
        public void onAnimationUpdate(final ValueAnimator valueAnimator) {
            // If the user is changing the slider, cancel the animation.
            if (mIsTrackTouch) {
                valueAnimator.cancel();
                return;
            }
            final int animatedIntValue = (int) valueAnimator.getAnimatedValue();
            setProgress(animatedIntValue);
        }
    }
}
