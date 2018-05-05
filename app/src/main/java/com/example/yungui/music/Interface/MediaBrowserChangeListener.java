package com.example.yungui.music.Interface;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

/**
 * Created by 22892 on 2018/1/16.
 */

public interface MediaBrowserChangeListener {

    void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat);

    void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat);

    void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat);

    void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems);
}
