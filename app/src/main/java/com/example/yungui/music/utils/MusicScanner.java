package com.example.yungui.music.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.yungui.music.db.MusicInfoDataBase;
import com.example.yungui.music.info.MusicInfo;

import java.io.File;
import java.io.FileFilter;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DATE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_GENRE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

/**
 * Created by 22892 on 2018/1/8.
 */

public class MusicScanner {
    public static final String TAG = MusicScanner.class.getSimpleName();
    private MediaScannerConnection mediaScannerConnection;
    private ScannerClient scannerClient;
    private static MusicScanner instance;
    private File file;
    private String mimeType;
    private Context mContext;
    private MusicInfoDataBase musicInfoDataBase;
    //解析歌曲信息
    private MediaMetadataRetriever mediaMetadataRetriever;
    private OnScannerListener onScannerListener;
    private int songCount = 0;
    private int currentCount;

    public static MusicScanner newInstance(Context context) {
        Log.e(TAG, "newInstance: ");
        if (instance == null) {
            instance = new MusicScanner(context);
        }
        return instance;
    }


    private MusicScanner(Context context) {
        Log.e(TAG, "MusicScanner: ");
        mContext = context;
        if (scannerClient == null) {
            scannerClient = new ScannerClient();
        }
        if (musicInfoDataBase == null) {

            musicInfoDataBase = InjectorUtils.provideMusicInfoDataBase(context);
        }
        if (mediaScannerConnection == null) {

            mediaScannerConnection = new MediaScannerConnection(context, scannerClient);
        }
    }

    class ScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

        @Override
        public void onMediaScannerConnected() {
            Log.e(TAG, "onMediaScannerConnected: ");
            //连接上服务时候回调
            scannerFile(file, mimeType);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            Log.e(TAG, "onScanCompleted: " + path + "===" + uri);
            //回调
            onScannerListener.scanning(path, uri);
            if (path.endsWith(".flac") || path.endsWith(".mp3")) {
                AppExecutors.getInstance().localIO().execute(() -> {
                    currentCount = songCount;
                    songCount += 1;
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    MusicInfo musicInfo = new MusicInfo();
                    try {

                        mediaMetadataRetriever.setDataSource(path);
                    } catch (Exception e) {
                        Log.e(TAG, "mediaMetadataRetriever.setDataSource: " + e);
                    }
                    musicInfo.songId = songCount;
                    musicInfo.data = path == null ? null : path;
                    musicInfo.musicName = mediaMetadataRetriever.extractMetadata(METADATA_KEY_TITLE) == null ? "11" : mediaMetadataRetriever.extractMetadata(METADATA_KEY_TITLE);
                    musicInfo.albumName = mediaMetadataRetriever.extractMetadata(METADATA_KEY_ALBUM) == null ? "11" : mediaMetadataRetriever.extractMetadata(METADATA_KEY_ALBUM);
                    musicInfo.artist = mediaMetadataRetriever.extractMetadata(METADATA_KEY_ARTIST) == null ? "11" : mediaMetadataRetriever.extractMetadata(METADATA_KEY_ARTIST);
                    musicInfo.duration = mediaMetadataRetriever.extractMetadata(METADATA_KEY_DURATION) == null ? "0" : mediaMetadataRetriever.extractMetadata(METADATA_KEY_DURATION);
                    musicInfo.folder = new File(path).getParent();
                    musicInfo.mimeType = mediaMetadataRetriever.extractMetadata(METADATA_KEY_MIMETYPE);
                    musicInfoDataBase.musicInfoDao().insert(musicInfo);
                    Log.e(TAG, "获取的歌曲信息：" + musicInfo.toString());
                });
                mediaMetadataRetriever.release();
            }
            mediaScannerConnection.disconnect();
        }

        private void scannerFile(File file, String mimeType) {
            Log.e(TAG, "scannerFile: ");
            //是文件直接扫描
            if (file.isFile()) {
                mediaScannerConnection.scanFile(file.getAbsolutePath(), null);
                return;
            }
            //是文件夹
            File[] files = file.listFiles();
            //文件夹为空,退出,第一次进来是不会是不会为空的
            if (files == null) {
                Log.e(TAG, "文件夹为空退出");
                return;
            }
            //循环
            for (File file1 : files) {
                if (file1.toString().contains("图片") || file1.toString().contains("Camera") || file1.toString().contains("picture")) {
                    continue;
                }//递归
                scannerFile(file1, null);
            }
        }

    }

    public void scanning(@NonNull File file, String mimeType, OnScannerListener listener) {
        onScannerListener = listener;
        this.file = file;
        this.mimeType = mimeType;
        //连接mediaScannerService
        mediaScannerConnection.connect();

    }

    public void disconnect() {
        mediaScannerConnection.disconnect();
        file = null;
        mimeType = null;
    }

    public interface OnScannerListener {
        void scanning(String path, Uri uri);

        void completed();
    }

}

