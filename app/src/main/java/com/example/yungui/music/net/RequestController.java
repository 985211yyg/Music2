package com.example.yungui.music.net;

import com.example.yungui.music.net.retrofit_interface.MusicApi;

/**
 * Created by yungui on 2017/12/6.
 */

public class RequestController {
    public static final Object monitor = new Object();
    public static MusicApi musicApi;

    public static MusicApi getMusicApi() {
        if (musicApi == null) {
            synchronized (monitor) {
                musicApi = RetrofitManager.getInstance().Create(MusicApi.class);
            }
        }
        return musicApi;
    }
}
