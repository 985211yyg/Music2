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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.yungui.music.MainActivity;
import com.example.yungui.music.MainApplication;
import com.example.yungui.music.R;
import com.example.yungui.music.service.MediaPlaybackService;
import com.example.yungui.music.utils.PermissionUtils;


/**
 * Keeps track of a notification and updates it automatically for a given MediaSession. This is
 * required so that the music service don't get killed during playback.
 */
public class MediaNotificationManager {

    public static final int NOTIFICATION_ID = 412;
    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final String CHANNEL_ID = "com.example.android.musicplayer.channel";
    private static final int REQUEST_CODE = 501;

    private final NotificationCompat.Action mPlayAction;
    private final NotificationCompat.Action mPauseAction;
    private final NotificationCompat.Action mNextAction;
    private final NotificationCompat.Action mPrevAction;
    private final NotificationManager mNotificationManager;

    private MediaPlaybackService service;
    private MediaControllerCompat mediaControllerCompat;

    public MediaNotificationManager(MediaPlaybackService mediaPlaybackService) {
        service = mediaPlaybackService;
        mNotificationManager =
                (NotificationManager) service.getSystemService(service.NOTIFICATION_SERVICE);
        //播放按钮
        mPlayAction =
                new NotificationCompat.Action(
                        R.mipmap.ic_play_arrow_white_24dp,
                        "播放",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                service,
                                PlaybackStateCompat.ACTION_PLAY));
        //暂停按钮
        mPauseAction =
                new NotificationCompat.Action(
                        R.mipmap.ic_pause_white_24dp,
                        "暂停",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                service,
                                PlaybackStateCompat.ACTION_PAUSE));
        //下一首
        mNextAction =
                new NotificationCompat.Action(
                        R.mipmap.ic_skip_next_white_24dp,
                        "下一首",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                service,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        //上一首
        mPrevAction =
                new NotificationCompat.Action(
                        R.mipmap.ic_skip_previous_white_24dp,
                        "上一首",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                service,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
//        mNotificationManager.cancelAll();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
    }

    public NotificationManager getNotificationManager() {

        return mNotificationManager;
    }

    public Notification getNotification(MediaMetadataCompat metadata,
                                        @NonNull PlaybackStateCompat state,
                                        MediaSessionCompat mediaSessionCompat) {

        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        mediaControllerCompat = mediaSessionCompat.getController();
        MediaDescriptionCompat description = metadata.getDescription();
        NotificationCompat.Builder builder =
                buildNotification(state,
                        mediaSessionCompat.getSessionToken(),
                        isPlaying,
                        description);
        return builder.build();
    }

    private NotificationCompat.Builder buildNotification(PlaybackStateCompat state,
                                                         MediaSessionCompat.Token token,
                                                         boolean isPlaying,
                                                         MediaDescriptionCompat description) {

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher()) {
            createChannel();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, CHANNEL_ID);
        builder.setStyle(
                new MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0, 1, 2)
                        //右上角的取消按钮
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(
                                        service,
                                        PlaybackStateCompat.ACTION_STOP)))
                .setColor(service.getResources().getColor(R.color.themeColor))
                .setSmallIcon(R.mipmap.ic_stat_image_audiotrack)
                // 点击时进入应用
                .setContentIntent(createContentIntent())
                //歌名
                .setContentTitle(description.getTitle())
                // Subtitle - Usually Artist name.
                .setContentText(description.getSubtitle())
                .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.mipmap.timg))
                //当播放暂停时可以被取消
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service, PlaybackStateCompat.ACTION_STOP))
                // 锁屏界面显示
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // 当有下一首或者有下一首时显示对应图标，没有则不显示
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            builder.addAction(mPrevAction);
        }

        builder.addAction(isPlaying ? mPauseAction : mPlayAction);

        // If skip to prev action is enabled.
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            builder.addAction(mNextAction);
        }

        return builder;
    }

    // 主要针对8.0的
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "MediaSession";
            // The user-visible description of the channel.
            String description = "MediaSession and MediaPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
            Log.d(TAG, "createChannel: New channel created");
        } else {
            Log.d(TAG, "createChannel: Existing channel reused");
        }
    }

    //判断是否是8.0及以上版本
    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(service, MainActivity.class);
        //启动位于栈顶的activity，如果不是制定的activity则新建一个
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                service, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }


}