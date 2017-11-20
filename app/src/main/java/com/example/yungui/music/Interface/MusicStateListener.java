package com.example.yungui.music.Interface;

/**
 * Created by yungui on 2017/10/17.
 */

public interface MusicStateListener {
    //更新歌曲信息
    void updateTrackInfo();

    //更新时间
    void updateTime();

    //更新主题
    void updateTheme();

    //重载适配器，更新所有歌曲信息
    void updateAdapter();


}
