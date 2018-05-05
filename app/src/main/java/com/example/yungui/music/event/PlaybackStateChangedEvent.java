package com.example.yungui.music.event;

import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Created by 22892 on 2018/1/17.
 */

public class PlaybackStateChangedEvent {
    private PlaybackStateCompat stateCompat;

    public PlaybackStateChangedEvent(PlaybackStateCompat stateCompat) {
        this.stateCompat = stateCompat;
    }

    public PlaybackStateCompat getStateCompat() {
        return stateCompat;
    }

    public void setStateCompat(PlaybackStateCompat stateCompat) {
        this.stateCompat = stateCompat;
    }
}
