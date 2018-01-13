package com.example.yungui.music.service;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 持有MediaBrowser  MediaController
 * Created by 22892 on 2018/1/10.
 */

public class MediaBrowserAdapter {
    public static final String TAG = MediaBrowserAdapter.class.getSimpleName();

    private final MediaControllerCallback mediaControllerCallback = new MediaControllerCallback();
    //供UI订阅
    private final MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
    //连接回调，传给MediaBrowserService
    private final MediaBrowserConnectionCallback mediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();

    @NonNull
    private MediaControllerCompat mediaController;
    private MediaBrowserCompat mediaBrowser;

    private final List<MediaBrowserChangeForUIListener> mediaBrowserChangeForUiListeners = new ArrayList<>();

    private final InternalState internalState;
    private Context mContext;


    public MediaBrowserAdapter(Context context) {
        Log.d(TAG, "创建MediaBrowserAdapter 实例 ");
        mContext = context;
        internalState = new InternalState();
    }


    //与activity或者fragment的onStart方法一致
    //在对应的生命周期中，做相应操作
    //初始化MediaBrowser,并链接到MediaBrowserService
    public void onStart() {
        Log.d(TAG, "onStart: creating MediaController, and connecting to MediaBrowser");
        if (mediaBrowser == null) {
            //实例化，并设置监听回调
            mediaBrowser = new MediaBrowserCompat(
                    mContext,
                    new ComponentName(mContext, MediaPlaybackService.class),
                    mediaBrowserConnectionCallback,
                    null);
            //链接到服务，连接过程在回调中体现
            mediaBrowser.connect();

        }


    }

    /**
     * ======MediaBrowser连接MediaBrowserService 是的回调，传给MediaBrowserService做参数===
     * 连接成功后获取service中对应MediaSession的MediaControl,并给controller注册回调，用于监听
     * MediaSession中的数据及状态变化
     */
    public class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        //连接服务成功
        //此时获取服务中的控制MediaSession的MediaController
        @Override
        public void onConnected() {
            Log.e(TAG, "onConnected: 已经链接上服务");
            try {
                //根据服务中设置的MediaSession的Token获取对应的MediaController
                mediaController = new MediaControllerCompat(mContext,
                        mediaBrowser.getSessionToken());
                //==========观察服务，当加载数据，或者出错是调用
                mediaBrowser.subscribe(mediaBrowser.getRoot(), mediaBrowserSubscriptionCallback);
                //注册回调
                mediaController.registerCallback(mediaControllerCallback);
                //注册监听之后，调用回调方法将现有MediaSession状态同步到UI。
                mediaControllerCallback.onMetadataChanged(
                        mediaController.getMetadata());
                mediaControllerCallback.onPlaybackStateChanged(
                        mediaController.getPlaybackState());

                //通知所有的监听者,将得到的MediaController传给监听者,
                performAllListeners(new ListenerCommand() {
                    @Override
                    public void perform(@NonNull MediaBrowserChangeForUIListener changeListener) {
                        Log.e(TAG, "performAllListeners: 将得到的MediaController传给监听者 ");
                        changeListener.onConnected(mediaController);
                    }
                });

            } catch (RemoteException e) {
                Log.d(TAG, String.format("onConnected: Problem: %s", e.toString()));
                throw new RuntimeException(e);
            }

        }

        //链接暂停
        @Override
        public void onConnectionSuspended() {
            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>onConnectionSuspended: 服务暂停");

        }

        //失败
        @Override
        public void onConnectionFailed() {
            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>onConnectionFailed:连接服务失败 ");

        }
    }


    /**
     * 接收来自MediaService中MediaSession的信息，用于更新ui，比如歌曲的切换
     * ，pause  play stop
     */
    public class MediaControllerCallback extends MediaControllerCompat.Callback {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            internalState.setPlaybackState(state);
            //通知所有监听者
            performAllListeners(new ListenerCommand() {
                @Override
                public void perform(@NonNull MediaBrowserChangeForUIListener changeListener) {
                    Log.e(TAG, "performAllListeners: onPlaybackStateChanged");
                    changeListener.onPlaybackStateChanged(state);
                }
            });
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            //不同则更新
            if (isMediaMetadataSame(metadata, internalState.mediaMetadata)) {
                Log.e(TAG, "onMetadataChanged: Filtering out needless onMetadataChanged() update");
                return;

            } else {
                internalState.setMediaMetadata(metadata);
                //通知所有的监听者元数据发生改变
                performAllListeners(new ListenerCommand() {
                    @Override
                    public void perform(@NonNull MediaBrowserChangeForUIListener changeListener) {
                        Log.e(TAG, "performAllListeners: onMetadataChanged");
                        changeListener.onMetadataChanged(metadata);
                    }
                });
            }

        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {


        }

        @Override
        public void onSessionDestroyed() {
            //重置状态持有者，刷新通知
            resetState();
            onPlaybackStateChanged(null);
            Log.e(TAG, "onSessionDestroyed: MusicService is dead!!!");
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {

        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {

        }

        @Override
        public void onExtrasChanged(Bundle extras) {

        }

        @Override
        public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {

        }


        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onShuffleModeChanged(int shuffleMode) {
        }

        @Override
        public void binderDied() {
        }


        //通过比较两个元数据的MediaID
        private boolean isMediaMetadataSame(MediaMetadataCompat newMetadata, MediaMetadataCompat currentMetadata) {
            if (currentMetadata == null || newMetadata == null) {
                return false;
            }
            String newMetadataID = newMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            String currentMetadataID = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            return newMetadataID.equals(currentMetadataID);
        }

    }

    /**
     * 当MediaBrowserService加载新数据时接收来自MediaBrowser的回调，
     */
    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        //当数据更新是回调，可以将数据返回给UI进行展示
        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> mediaItems) {
            //确保MediaController不是null,如果为null程序将退出
            assert mediaController != null;
            //都添加到播放列表中
            for (MediaBrowserCompat.MediaItem mediaItem : mediaItems) {
                if (mediaItem.getDescription().toString().contains("11")) {
                    continue;
                } else {
                    mediaController.addQueueItem(mediaItem.getDescription());
                }
            }
            //数据已经准备完毕，让player做好准备，这可以减少收到播放命令时开始播放的时间。此操作不是必须的
            mediaController.getTransportControls().prepare();

        }


        @Override
        public void onError(@NonNull String parentId) {

        }


    }


    //与activity或者fragment的onStop方法一致
    //断开相应的来链接，注销对应的回调
    //重置状态
    public void onStop() {
        Log.d(TAG, "onStop: Releasing MediaController, Disconnecting from MediaBrowser");
        if (mediaController != null) {
            mediaController.unregisterCallback(mediaControllerCallback);
            mediaController = null;
        }
        if (mediaBrowser != null && mediaBrowser.isConnected()) {
            //断开链接
            mediaBrowser.disconnect();
            mediaBrowser = null;
        }
        //重置状态
        resetState();

    }

    //重置状态，
    private void resetState() {
        internalState.reset();
        performAllListeners(new ListenerCommand() {
            @Override
            public void perform(@NonNull MediaBrowserChangeForUIListener changeForUIListener) {
                //置空播放状态
                changeForUIListener.onPlaybackStateChanged(null);
                changeForUIListener.onMetadataChanged(null);
            }
        });
        Log.d(TAG, "resetState: ");

    }

    //获取MediaController的传输控制
    public MediaControllerCompat.TransportControls getTransportControls() {
        if (mediaController == null) {
            Log.e(TAG, "getTransportControls: MediaContorller is null");
            throw new IllegalStateException();
        }
        return mediaController.getTransportControls();
    }

    //对所有的监听器执行命令
    public void performAllListeners(@NonNull ListenerCommand command) {
        Log.e(TAG, "performAllListeners: 开始执行命令");
        for (MediaBrowserChangeForUIListener browserChangeForUIListener : mediaBrowserChangeForUiListeners) {
            if (browserChangeForUIListener != null && command != null) {
                try {
                    Log.e(TAG, "performAllListeners: command");
                    command.perform(browserChangeForUIListener);
                    Log.e(TAG, "performAllListeners: 成功");
                } catch (Exception e) {
                    removeListener(browserChangeForUIListener);
                    Log.e(TAG, "performAllListeners: 出错");

                }
            }
        }
    }

    //执行命令
    public interface ListenerCommand {
        void perform(@NonNull MediaBrowserChangeForUIListener changeListener);
    }


    //获取MediaBrowser的中
    public void addListener(MediaBrowserChangeForUIListener changeForUIListener) {
        if (changeForUIListener != null) {
            mediaBrowserChangeForUiListeners.add(changeForUIListener);
        }
    }

    //移除监听
    public void removeListener(MediaBrowserChangeForUIListener changeForUIListener) {
        if (changeForUIListener != null) {
            if (mediaBrowserChangeForUiListeners.contains(changeForUIListener)) {
                mediaBrowserChangeForUiListeners.remove(changeForUIListener);
            }
        }
    }

    /**
     * ==========MediaBrowser状态改变的回调方法，提供给UI使用=================
     */
    public static abstract class MediaBrowserChangeForUIListener {

        //连接回调
        public abstract void onConnected(@NonNull MediaControllerCompat mediaControllerCompat);

        //数据改变是的回调
        public abstract void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat);

        //播放状态改变的回调
        public abstract void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat);

    }


    /**
     * 包含状态的类
     */
    public class InternalState {
        //元数据 歌曲信息等
        private MediaMetadataCompat mediaMetadata;
        //播放状态
        private PlaybackStateCompat playbackState;

        public void reset() {
            mediaMetadata = null;
            playbackState = null;
        }

        public MediaMetadataCompat getMediaMetadata() {
            return mediaMetadata;
        }

        public void setMediaMetadata(MediaMetadataCompat mediaMetadata) {
            this.mediaMetadata = mediaMetadata;
        }

        public PlaybackStateCompat getPlaybackState() {
            return playbackState;
        }

        public void setPlaybackState(PlaybackStateCompat playbackState) {
            this.playbackState = playbackState;
        }
    }
}
