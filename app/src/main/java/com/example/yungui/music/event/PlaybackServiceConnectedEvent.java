package com.example.yungui.music.event;

import android.support.v4.media.session.MediaControllerCompat;

/**
 * Created by 22892 on 2018/1/17.
 */

public class PlaybackServiceConnectedEvent {
    private MediaControllerCompat mediaControllerCompat;

    public PlaybackServiceConnectedEvent(MediaControllerCompat mediaControllerCompat) {
        this.mediaControllerCompat = mediaControllerCompat;
    }

    public MediaControllerCompat getMediaControllerCompat() {
        return mediaControllerCompat;
    }

    public void setMediaControllerCompat(MediaControllerCompat mediaControllerCompat) {
        this.mediaControllerCompat = mediaControllerCompat;
    }
}
