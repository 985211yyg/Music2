package com.example.yungui.music.service;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.yungui.music.dataUtils.MusicLibrary;
import com.example.yungui.music.widget.MediaNotificationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * MediaBrowserServiceCompat 持有MediaSession和Player
 * Created by 22892 on 2018/1/9.
 */

public class MediaPlaybackService extends MediaBrowserServiceCompat {
    public static final String TAG = MediaPlaybackService.class.getSimpleName();
    private String Root = MediaPlaybackService.class.getCanonicalName();

    private MediaSessionCompat mMediaSession;
    private MediaPlayerAdapter playerBack;
    private MusicLibrary musicLibrary;
    private MediaSessionCallback mMediaSessionCallback;
    private MediaNotificationManager mMediaNotificationManager;
    private boolean mServiceInStartedState;//用于标记通知服务

    public MediaPlaybackService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: MusicService 创建 MediaSession 和通知栏");
        musicLibrary = new MusicLibrary(this);
        //创建MediaSession
        mMediaSession = new MediaSessionCompat(this, TAG);
        mMediaSessionCallback = new MediaSessionCallback();
        mMediaSession.setCallback(mMediaSessionCallback);
        //设置mediaSession,使其能够接收到来自MediaButton和MediaController的调用
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        //设置Token以得到与该MediaSession对应的MediaBrowser
        //从而获取到MediaController，在使用MediaController调用MediaSession的方法
        setSessionToken(mMediaSession.getSessionToken());
        //通知管理器
        mMediaNotificationManager = new MediaNotificationManager(this);
        playerBack = new MediaPlayerAdapter(this, new MediaPlayerListener());


    }

    //任务被移除，停止服务
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    //清理
    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        if (playerBack.isPlaying()) {
            playerBack.stop();
        }
        mMediaSession.release();
        Log.e(TAG, "onDestroy: MediaPlayBackService stopped, and MediaSession released");

    }

    // 控制对服务的访问，验证权限
    // 没有权限，返回null，则连接被拒绝
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 @Nullable Bundle rootHints) {

        return new BrowserRoot(Root, null);
    }

    // !!!!装载内容 回调MediaBrowserCompat.SubscriptionCallback 讲数据传出去，MediaController调用MediaSession onAddQueueItem增加数据
    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        List<MediaBrowserCompat.MediaItem> mediaItems = musicLibrary.provideMediaItem();
        result.sendResult(mediaItems);
    }

    /**
     * MediaSession接收来自MediaController和系统的调用
     */
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        //播放列表
        private final List<MediaSessionCompat.QueueItem> mPlayLists = new ArrayList<>();
        private int mQueueIndex = -1;
        //预备的数据
        private MediaMetadataCompat mPreparedMediaMetadata;


        // 在最后插入列队, MediaBrowserCompat.SubscriptionCallback 的回调方法onChildLoaded()中通过controller加入
        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            //计算hashcode作为ID
            mPlayLists.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            mPlayLists.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mPlayLists.isEmpty()) ? -1 : mQueueIndex;
        }

        //播放，并且开启服务开启通知栏
        @Override
        public void onPlay() {
            if (mPlayLists == null && mPlayLists.size() == 0) {
                return;
            }
            if (mPreparedMediaMetadata == null) {
                Log.e(TAG, "onPlay:加载数据中");
                //装载元数据
                onPrepare();
            }
            //播放音乐
            playerBack.playFromMedia(mPreparedMediaMetadata);
        }


        //准备播放,获取元数据，激活MediaSession
        @Override
        public void onPrepare() {
            if (mQueueIndex < 0 && mPlayLists.isEmpty()) {
                Log.e(TAG, "onPrepare: 没有数据");
                //没有数据
                return;
            }
            //从描述信息想中获取ID
            String mediaID = mPlayLists.get(mQueueIndex).getDescription().getMediaId();
            mPreparedMediaMetadata = musicLibrary.queryMetadata(mediaID);
            Log.e(TAG, "onPrepare: 查询到的MediaMetadata" + mPreparedMediaMetadata.getDescription().toString());
            mMediaSession.setMetadata(mPreparedMediaMetadata);
            if (!mMediaSession.isActive()) {
                mMediaSession.setActive(true);
            }
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            return false;
        }

        //调到指定位置的item
        @Override
        public void onSkipToQueueItem(long id) {
            mQueueIndex = (int) id;
            mPreparedMediaMetadata = null;
            onPlay();
        }

        @Override
        public void onPause() {
            playerBack.pause();
        }

        //播放下一首
        @Override
        public void onSkipToNext() {
            mQueueIndex = (++mQueueIndex % mPlayLists.size());//取余数  余数永远是1，等为0，就是自加1
            //重置mediaData
            mPreparedMediaMetadata = null;
            onPlay();
        }

        //博凡前一首
        @Override
        public void onSkipToPrevious() {
            mQueueIndex = mQueueIndex > 0 ? mQueueIndex - 1 : mPlayLists.size() - 1;
            mPreparedMediaMetadata = null;
            onPlay();

        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {

        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {

        }

        @Override
        public void onStop() {
            playerBack.stop();
            //mediaSession暂停活动
            mMediaSession.setActive(false);
        }


        @Override
        public void onSeekTo(long pos) {
            playerBack.seekTo(pos);
        }


        @Override
        public void onSetRepeatMode(int repeatMode) {

        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {

        }

        @Override
        public void onCustomAction(String action, Bundle extras) {

        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            super.onPrepareFromMediaId(mediaId, extras);
        }

        @Override
        public void onPrepareFromSearch(String query, Bundle extras) {
            super.onPrepareFromSearch(query, extras);
        }

        @Override
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            super.onPrepareFromUri(uri, extras);
        }
    }

    /**
     * ============MediaPlay的反馈=========================
     * 1、播放完成
     * 2、播放状态改变
     * 3、缓冲进度
     */
    public class MediaPlayerListener extends PlaybackInfoListener {
        private final ServicesManager servicesManager;

        public MediaPlayerListener() {
            servicesManager = new ServicesManager();
        }

        //播放完成
        @Override
        public void OnPlayCompleted() {
            Log.e(TAG, "OnPlayCompleted: 播放完成！");
            mMediaSession.getController().getTransportControls().skipToNext();
        }

        //播放状态改变你回调
        @Override
        public void OnPlaybackInfoChanged(PlaybackStateCompat stateCompat) {
            // 会擦混播放状态给 MediaSession. 需要包含播放暂停等状态，以及播放的位置
            mMediaSession.setPlaybackState(stateCompat);
            switch (stateCompat.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    servicesManager.moveServiceToStartedState(stateCompat);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    servicesManager.updateNotificationForPause(stateCompat);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    servicesManager.moveServiceOutOfStartedState(stateCompat);
                    break;
                    //处于缓冲状态，需要传出春冲进度
                case PlaybackStateCompat.STATE_BUFFERING:
                    servicesManager.moveServiceToStartedState(stateCompat);
                    break;

            }

        }

        @Override
        public void OnBufferingUpdate(int percent) {
            PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
            stateBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, 1, SystemClock.elapsedRealtime());
            stateBuilder.setBufferedPosition(percent);
        }
    }

    /**
     * 管理服务的类 负责管理通知栏
     */
    class ServicesManager {

        private void moveServiceToStartedState(PlaybackStateCompat stateCompat) {
            Log.d(TAG, ">>>>>>>>>moveServiceToStartedState: ");
            Notification notification = mMediaNotificationManager
                    .getNotification(playerBack.getCurrentMedia(),
                            stateCompat, mMediaSession);
            //开启服务
            if (!mServiceInStartedState) {
                ContextCompat.startForegroundService(MediaPlaybackService.this, new Intent(MediaPlaybackService.this, MediaPlaybackService.class));
                mServiceInStartedState = true;
            }
            //将通知前置到通知栏
            startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
        }

        //暂停时的通知栏
        private void updateNotificationForPause(PlaybackStateCompat stateCompat) {
            Log.d(TAG, ">>>>>>>>updateNotificationForPause: ");
            //停止前台通知栏，但是不移除通知栏
            stopForeground(false);
            //根据状态创建对应的通知栏
            Notification notification = mMediaNotificationManager.getNotification(playerBack.getCurrentMedia(),
                    stateCompat, mMediaSession);
            mMediaNotificationManager.getNotificationManager()
                    .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
        }

        private void moveServiceOutOfStartedState(PlaybackStateCompat stateCompat) {
            Log.e(TAG, ">>>>>>>>moveServiceOutOfStartedState: ");
            //停止通知并移除通知栏
            stopForeground(true);
            //通知服务
            stopSelf();
            mServiceInStartedState = false;
        }


    }
}
