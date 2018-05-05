package com.example.yungui.music.dataUtils;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.example.yungui.music.db.MusicInfoDataBase;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.utils.AppExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 从数据库中获取歌曲信息，组装成 MediaMetaData
 * Created by 22892 on 2018/1/9.
 */

public class MusicLibrary {
    public static final String TAG = MusicLibrary.class.getSimpleName();

    private HashMap<String, MediaMetadataCompat> musics = new HashMap<>();
    private List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

    public MusicLibrary(Context context) {
        initData(context);
    }

    private void initData(Context context) {
        AppExecutors.getInstance().localIO().execute(() -> {
            MusicInfoDataBase.getInstance(context)
                    .musicInfoDao()
                    .getAll()
                    .observeForever(new Observer<List<MusicInfo>>() {
                        @Override
                        public void onChanged(@Nullable List<MusicInfo> musicInfos) {
                            for (MusicInfo musicInfo : musicInfos) {
                                if (musicInfo.musicName == "" ||
                                        musicInfo.albumName == "" ||
                                        musicInfo.artist == "") {
                                    continue;
                                }
                                MediaMetadataCompat mediaMetadataCompat = createMediaMetadataCompat(
                                        String.valueOf(musicInfo.songId),
                                        musicInfo.musicName,
                                        musicInfo.artist,
                                        musicInfo.albumName,
                                        musicInfo.mimeType,
                                        musicInfo.duration,
                                        musicInfo.data
                                );
                                musics.put(String.valueOf(musicInfo.songId), mediaMetadataCompat);
                                MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat
                                        .MediaItem(mediaMetadataCompat.getDescription(),
                                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
                                mediaItems.add(mediaItem);
                            }
                            Log.e(TAG, "mediaItems: " + mediaItems.size());
                            Log.e(TAG, "mediaItems: " + musics.size());
                        }
                    });

        });

    }

    /**
     * 装载MediaItems
     *
     * @return
     */
    public List<MediaBrowserCompat.MediaItem> provideMediaItem() {
        if (mediaItems != null && mediaItems.size() > 0) {
            Log.e(TAG, "provideMediaItem: 有数据");
            return mediaItems;
        } else {
            Log.e(TAG, "provideMediaItem: 没数据");
            return mediaItems;
        }
    }

    /**
     * 返回对应ID的MediaMetadataCompat
     */
    public MediaMetadataCompat queryMetadata(@NonNull String mediaId) {
        if (musics != null && musics.size() > 0) {
            Log.e(TAG, "QueryMetadata: 有数据");
            return musics.get(mediaId);
        } else {
            Log.e(TAG, "QueryMetadata: 没有数据");
            return null;
        }

    }


    ///装载
    private static MediaMetadataCompat createMediaMetadataCompat(String mediaId,
                                                                 String title,
                                                                 String artist,
                                                                 String album,
                                                                 String genre,
                                                                 String duration,
                                                                 String mediaUri) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.valueOf(duration))
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaUri)
                .build();
    }


}
