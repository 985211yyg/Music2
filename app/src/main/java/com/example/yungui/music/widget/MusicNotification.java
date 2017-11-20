package com.example.yungui.music.widget;

import android.widget.RemoteViews;

/**
 * Created by yungui on 2017/9/28.
 */

public class MusicNotification extends RemoteViews {
    
    public MusicNotification(String packageName, int layoutId) {
        super(packageName, layoutId);
    }
}
