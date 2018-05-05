package com.example.yungui.music.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yungui.music.Interface.IConstants;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.utils.AppExecutors;
import com.example.yungui.music.utils.FileUtils;
import com.example.yungui.music.utils.MusicScanner;
import com.example.yungui.music.utils.MusicUtils;

import java.io.File;
import java.util.List;

public class PopupItemActivity extends BaseActivity {

    public static final String TAG = "tag";
    public static final String TITLE = "title";

    public static final String UPDATE = "升级音质";
    public static final String SCANNING = "扫描歌曲";
    public static final String DOWNLOAD_LRC = "一键下载词图";
    public static final String RECOVER = "本地歌曲恢复助手";

    public static final int READ_QUEST_CODE = 0x123;
    public static final int TAG_UPDATE = 0;
    public static final int TAG_SCANNING = 1;
    public static final int TAG_DOWNLOAD_LRC = 2;
    public static final int TAG_RECOVER = 3;
    private int tag;
    private TextView show;
    private CheckBox chTime, chSize;
    private Button scanning, custom_scanning, scanning_setting, close;

    private MusicScanner musicScanner;

    private int songCount = 0;
    private boolean cancelable;


    @Override
    protected int getLayoutID() {
        tag = getIntent().getIntExtra(TAG, 0);
        switch (tag) {
            case TAG_UPDATE:
                return R.layout.popup_item_scaning;

            case TAG_SCANNING:
                return R.layout.popup_item_scaning;

            case TAG_DOWNLOAD_LRC:
                return R.layout.popup_item_scaning;
            case TAG_RECOVER:
                return R.layout.popup_item_scaning;
        }
        return 0;
    }

    @Override
    public boolean setToolBar() {
        return false;
    }

    @Override
    public void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat) {

    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {

    }

    @Override
    public void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        musicScanner = MusicScanner.newInstance(PopupItemActivity.this);
        switch (tag) {
            case TAG_UPDATE:

                break;
            case TAG_SCANNING:
                show = findViewById(R.id.show);
                chSize = findViewById(R.id.chSize);
                chTime = findViewById(R.id.chTime);
                custom_scanning = findViewById(R.id.custom_scanning);
                scanning = findViewById(R.id.scanning);
                scanning_setting = findViewById(R.id.scanning_setting);
                scanning.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.setTextSize(16);
                        show.setText("开始搜索...");
                        if (cancelable) {
                            musicScanner.disconnect();
                            scanning.setText("开始扫描");
                            cancelable = false;
                        }
                        scanning.setText("取消");
                        musicScanner.scanning(new File(FileUtils.getExtSDCardPath(PopupItemActivity.this)[1]),
                                "audio/*", new MusicScanner.OnScannerListener() {
                            @Override
                            public void scanning(String path, Uri uri) {
                                show.post(() -> {
                                    show.setText(path);
                                });
                            }

                            @Override
                            public void completed() {
                                scanning.setText("完成");
                                scanning.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scanning.setText("开始扫描");
                                    }
                                }, 3000);
                            }
                        });
                        cancelable = true;
                    }
                });
                custom_scanning.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppExecutors.getInstance().localIO().execute(() -> {
                            List<MusicInfo> musicInfos = MusicUtils.queryMusic(PopupItemActivity.this, IConstants.START_FROM_LOCAL);
                            Log.e(TAG, "扫描之后的歌曲: " + musicInfos.size());
                        });
                    }
                });

                break;
            case TAG_DOWNLOAD_LRC:

                break;
            case TAG_RECOVER:

                break;

        }

    }

    //关闭窗口
    public void close(View view) {
        Toast.makeText(this, "点击了关闭按钮", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicScanner.disconnect();
    }

    public class MyMediaScannerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //开始扫描歌曲
                case Intent.ACTION_MEDIA_SCANNER_STARTED:
                    Log.e(TAG, "onReceive: " + "开始扫描歌曲");
                    break;
                //扫描歌曲结束
                case Intent.ACTION_MEDIA_SCANNER_FINISHED:
                    Log.e(TAG, "onReceive: " + "扫描歌曲结束");
                    break;

                case Intent.ACTION_MEDIA_SCANNER_SCAN_FILE:
                    Log.e(TAG, "onReceive: " + "扫描文件");
                    break;
            }

        }
    }

}
