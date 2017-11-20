package com.example.yungui.music.Interface;

/**
 * Created by yungui on 2017/10/17.
 * 常量接口
 */

public interface IConstants {
    String MUSIC_COUNT_CHANGED = "com.example.yungui.music.musiccountchanged";
    String PLAYLIST_ITEM_MOVED = "com.example.yungui.music.itemmoved";
    String PLAYLIST_COUNT_CHANGED = "com.example.yungui.music.playlistcountchanged";
    String EMPTY_LIST = "com.example.yungui.music.emptyplaylist";
    String PACKAGE = "com.example.yungui.music";

    int MUSICOVERFLOW = 0;
    int ARTISTOVERFLOW = 1;
    int ALBUMOVERFLOW = 2;
    int FOLDEROVERFLOW = 3;

    //歌手和专辑列表点击都会进入MyMusic 此时要传递参数表明是从哪里进入的
    int START_FROM_ARTIST = 1;
    int START_FROM_ALBUM = 2;
    int START_FROM_LOCAL = 3;
    int START_FROM_FOLDER = 4;
    int START_FROM_FAVORITE = 5;

    int FAV_PLAYLIST = 10;


}
