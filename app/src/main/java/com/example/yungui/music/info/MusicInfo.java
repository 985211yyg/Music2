/**
 * Copyright (lrc_arrow) www.longdw.com
 */
package com.example.yungui.music.info;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 音乐信息
 */
public class MusicInfo implements Parcelable {

    public static final String KEY_SONG_ID = "songID";//音乐id
    public static final String KEY_ALBUM_ID = "albumID";//专辑id
    public static final String KEY_ALBUM_NAME = "albumName";//专辑名字
    public static final String KEY_ALBUM_DATA = "albumData";//专辑数据
    public static final String KEY_DURATION = "duration";//时长
    public static final String KEY_MUSIC_NAME = "musicName";//音乐明细
    public static final String KEY_ARTIST = "artist";//艺术家
    public static final String KEY_ARTIST_ID = "artist_id";//艺术家id
    public static final String KEY_DATA = "data";//数据
    public static final String KEY_FOLDER = "folder";//文件夹
    public static final String KEY_SIZE = "size";//数量
    public static final String KEY_FAVORITE = "favorite";//喜欢
    public static final String KEY_LRC = "lrc";//歌词
    public static final String KEY_ISLOCAL = "isLocal";//是否是本地
    public static final String KEY_SORT = "sort";//分裂

    /**
     * 数据库中的_id
     */
    public long songId = -1;
    public int albumId = -1;
    public String albumName;
    public String albumData;
    public int duration;
    public String musicName;
    public String artist;
    public long artistId;
    public String data;
    public String folder;
    public String lrc;
    public boolean islocal;
    public String sort;
    public int size;

    /**
     * 0表示没有收藏 1表示收藏
     */
    public int favorite = 0;

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {

        @Override
        public MusicInfo createFromParcel(Parcel source) {
            MusicInfo music = new MusicInfo();
            Bundle bundle = new Bundle();
            bundle = source.readBundle();
            music.songId = bundle.getLong(KEY_SONG_ID);
            music.albumId = bundle.getInt(KEY_ALBUM_ID);
            music.albumName = bundle.getString(KEY_ALBUM_NAME);
            music.duration = bundle.getInt(KEY_DURATION);
            music.musicName = bundle.getString(KEY_MUSIC_NAME);
            music.artist = bundle.getString(KEY_ARTIST);
            music.artistId = bundle.getLong(KEY_ARTIST_ID);
            music.data = bundle.getString(KEY_DATA);
            music.folder = bundle.getString(KEY_FOLDER);
            music.albumData = bundle.getString(KEY_ALBUM_DATA);
            music.size = bundle.getInt(KEY_SIZE);
            music.lrc = bundle.getString(KEY_LRC);
            music.islocal = bundle.getBoolean(KEY_ISLOCAL);
            music.sort = bundle.getString(KEY_SORT);
            return music;
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SONG_ID, songId);
        bundle.putInt(KEY_ALBUM_ID, albumId);
        bundle.putString(KEY_ALBUM_NAME, albumName);
        bundle.putString(KEY_ALBUM_DATA, albumData);
        bundle.putInt(KEY_DURATION, duration);
        bundle.putString(KEY_MUSIC_NAME, musicName);
        bundle.putString(KEY_ARTIST, artist);
        bundle.putLong(KEY_ARTIST_ID, artistId);
        bundle.putString(KEY_DATA, data);
        bundle.putString(KEY_FOLDER, folder);
        bundle.putInt(KEY_SIZE, size);
        bundle.putString(KEY_LRC, lrc);
        bundle.putBoolean(KEY_ISLOCAL, islocal);
        bundle.putString(KEY_SORT, sort);
        dest.writeBundle(bundle);
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

}