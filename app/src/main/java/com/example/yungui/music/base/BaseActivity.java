package com.example.yungui.music.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.yungui.music.Interface.MediaBrowserChangeListener;
import com.example.yungui.music.R;
import com.example.yungui.music.event.PlaybackServiceConnectedEvent;
import com.example.yungui.music.event.PlaybackStateChangedEvent;
import com.example.yungui.music.service.MediaBrowserAdapter;
import com.example.yungui.music.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/10/16.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();
    public static final int requestCode = 1;
    protected Toolbar mToolbar;
    protected boolean isPlaying;
    protected MediaBrowserAdapter mMediaBrowserAdapter;
    private MediaBrowserAdapter.MediaBrowserChangeForUIListener changeForUIListener;
    private List<MediaBrowserChangeListener> mediaBrowserChangeListeners = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查权限是否被允许
        if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //是否需要作出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //todo 解释

            } else {
                //不用解释直接请求权限
                ActivityCompat.requestPermissions(BaseActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);

            }
        }
        //检查是否有权限
        if (!PermissionUtils.checkForUsageStatsPermission(this)) {
            //请求权限
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
        setContentView(getLayoutID());
        mMediaBrowserAdapter = new MediaBrowserAdapter(this);
        changeForUIListener = new MyMediaBrowserChangeForUIListener();
        mMediaBrowserAdapter.addListener(changeForUIListener);
        initToolBar(setToolBar());
        initView(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowserAdapter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaBrowserAdapter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaBrowserAdapter.removeListener(changeForUIListener);
    }

    public MediaBrowserAdapter getMediaBrowserAdapter() {
        return mMediaBrowserAdapter;
    }

    //-===================对外听的监听====
    public void addMediaBrowserChangeListener(@NonNull MediaBrowserChangeListener listener) {
        if (listener != null) {
            mediaBrowserChangeListeners.add(listener);
        }
    }

    public void removeMediaBrowserChangeListener(@NonNull MediaBrowserChangeListener listener) {
        if (listener != null && mediaBrowserChangeListeners.contains(listener)) {
            mediaBrowserChangeListeners.remove(listener);
        }
    }

    //获取布局
    protected abstract int getLayoutID();

    //  初始化布局,在activity创建时调用，将参数传出去
    protected abstract void initView(Bundle savedInstanceState);

    //是否带有toolbar
    public abstract boolean setToolBar();

    //下面的方法对于继承的activity是必须实现的
    public abstract void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat);

    public abstract void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat);

    public abstract void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat);

    public abstract void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems);

    //对所有的监听器执行命令
    public void performAllFragmentListeners(@NonNull ListenerCommand command) {
        for (MediaBrowserChangeListener mediaBrowserChangeListener : mediaBrowserChangeListeners) {
            if (mediaBrowserChangeListener != null && command != null) {
                command.perform(mediaBrowserChangeListener);
            }
        }
    }

    public interface ListenerCommand {
        void perform(MediaBrowserChangeListener mediaBrowserChangeListener);
    }


    //===============================来自MediaBrowserAdapter的回调======================
    private class MyMediaBrowserChangeForUIListener extends MediaBrowserAdapter.MediaBrowserChangeForUIListener {
        @Override
        public void onConnected(@NonNull MediaControllerCompat mediaControllerCompat) {
            Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>onConnected: ");
            MediaControllerCompat.setMediaController(BaseActivity.this, mediaControllerCompat);
            BaseActivity.this.onPlayBackServiceConnected(mediaControllerCompat);
            performAllFragmentListeners(mediaBrowserChangeListener -> {
                mediaBrowserChangeListener.onPlayBackServiceConnected(mediaControllerCompat);
            });
        }

        @Override
        public void onMediaItemsLoaded(@NonNull List<MediaBrowserCompat.MediaItem> mediaItems) {
            Log.e(TAG, ">>>>>>>>>>onMediaItemsLoaded: ");
            BaseActivity.this.onMediaItemsLoaded(mediaItems);
            performAllFragmentListeners(mediaBrowserChangeListener -> {
                mediaBrowserChangeListener.onMediaItemsLoaded(mediaItems);
            });
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            Log.e(TAG, ">>>>>>>>onMetadataChanged: ");
            BaseActivity.this.onMetadataChanged(mediaMetadataCompat);
            performAllFragmentListeners(mediaBrowserChangeListener -> {
                mediaBrowserChangeListener.onMetadataChanged(mediaMetadataCompat);
            });


        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            Log.e(TAG, "onPlaybackStateChanged: ");
            isPlaying = playbackStateCompat != null &&
                    playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING;
            BaseActivity.this.onPlaybackStateChanged(playbackStateCompat);
            performAllFragmentListeners(mediaBrowserChangeListener -> {
                mediaBrowserChangeListener.onPlaybackStateChanged(playbackStateCompat);
            });
        }
    }

    private void initToolBar(boolean enable) {
        if (enable) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //权限请求成功
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                //权限请求失败  ，直接退出
                this.finish();
            }
        }
    }

    public void setDisplayHomeAsUpEnabled(boolean enabled) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        }
    }


}
