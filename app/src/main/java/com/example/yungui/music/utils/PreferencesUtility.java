/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.example.yungui.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public final class PreferencesUtility {

    public static final String ARTIST_SORT_ORDER = "artist_sort_order";//
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";
    public static final String ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";

    public static final String LRC_LAST_POSITION = "lrc_last_position";
    public static final String LRC_FRONT_SIZE = "lrc_front_size";
    public static final String LRC_FRONT_COLOR = "lrc_front_color";

    private static final String NOW_PLAYING_SELECTOR = "now_paying_selector";
    private static final String TOGGLE_ANIMATIONS = "toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations";
    private static final String TOGGLE_ARTIST_GRID = "toggle_artist_grid";
    private static final String TOGGLE_ALBUM_GRID = "toggle_album_grid";
    private static final String TOGGLE_HEADPHONE_PAUSE = "toggle_headphone_pause";
    private static final String THEME_PREFERNCE = "theme_preference";
    private static final String START_PAGE_INDEX = "start_page_index";
    private static final String START_PAGE_PREFERENCE_LASTOPENED = "start_page_preference_latopened";
    private static final String NOW_PLAYNG_THEME_VALUE = "now_playing_theme_value";
    private static final String FAVRIATE_MUSIC_PLAYLIST = "favirate_music_playlist";
    private static final String DOWNMUSIC_BIT = "down_music_bit";
    private static final String CURRENT_DATE = "currentdate";

    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;

    //构造方法
    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public int getLrcFrontSize() {
        return mPreferences.getInt(LRC_FRONT_SIZE, 14);
    }

    public void setLrcFrontSize(int lrcFrontSize) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LRC_FRONT_SIZE, lrcFrontSize);
        editor.commit();
    }

    public  int getLrcFrontColor() {
        return mPreferences.getInt(LRC_FRONT_COLOR, 1);
    }

    public  void setLrcFrontColor(int  lrcFrontColor) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LRC_FRONT_COLOR, lrcFrontColor);
        editor.commit();
    }

    /**
     * 退出时间
     * @return
     */
    public long lastExit(){
        return mPreferences.getLong("last_err_exit", 0);
    }
    public void setExitTime(){
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong("last_err_exit", System.currentTimeMillis());
        editor.commit();
    }

    public boolean isCurrentDayFirst(String str){
        return mPreferences.getString(CURRENT_DATE, "").equals(str);
    }
    public void setCurrentDate(String str){
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(CURRENT_DATE, str);
        editor.apply();
    }

    /**
     * 保存播放链接
     * @param id
     * @param link
     */
    public void setPlayLink(long id, String link) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(id + "", link);
        editor.apply();
    }
    public String getPlayLink(long id) {
        return mPreferences.getString(id + "", null);
    }

    /**
     * 记录item位置
     * @param str
     */
    public void setItemPosition(String str) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("item_relative_position", str);
        editor.apply();
    }
    public String getItemPosition() {
        return mPreferences.getString("item_relative_position", "推荐歌单 最新专辑 主播电台");
    }

    /**
     * 下载音乐的字节数
     * @param bit
     */
    public void setDownMusicBit(int bit) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(DOWNMUSIC_BIT, bit);
        editor.apply();
    }
    public int getDownMusicBit() {
        return mPreferences.getInt(DOWNMUSIC_BIT, 192);
    }

    /**
     * 是否是最爱的播放列表
     * @return
     */
    public boolean getFavriateMusicPlaylist() {
        return mPreferences.getBoolean(FAVRIATE_MUSIC_PLAYLIST, false);
    }
    public void setFavriateMusicPlaylist(boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(FAVRIATE_MUSIC_PLAYLIST, b);
        editor.apply();
    }

    public void setOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public boolean getAnimations() {
        return mPreferences.getBoolean(TOGGLE_ANIMATIONS, true);
    }

    public boolean getSystemAnimations() {
        return mPreferences.getBoolean(TOGGLE_SYSTEM_ANIMATIONS, true);
    }

    public boolean isArtistsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ARTIST_GRID, true);
    }

    public void setArtistsInGrid(final boolean b) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(TOGGLE_ARTIST_GRID, b);
                editor.apply();
                return null;
            }
        }.execute();

    }

    public boolean isAlbumsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ALBUM_GRID, true);
    }

    public void setAlbumsInGrid(final boolean b) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(TOGGLE_ALBUM_GRID, b);
                editor.apply();
                return null;
            }
        }.execute();

    }

    public boolean pauseEnabledOnDetach() {
        return mPreferences.getBoolean(TOGGLE_HEADPHONE_PAUSE, true);
    }

    public String getTheme() {
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }


    /**
     * 保存开始页面的index
     * @return
     */
    public int getStartPageIndex() {
        return mPreferences.getInt(START_PAGE_INDEX, 0);
    }
    public void setStartPageIndex(final int index) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(START_PAGE_INDEX, index);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setLastOpenedAsStartPagePreference(boolean preference) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(START_PAGE_PREFERENCE_LASTOPENED, preference);
        editor.apply();
    }

    public boolean lastOpenedIsStartPagePreference() {
        return mPreferences.getBoolean(START_PAGE_PREFERENCE_LASTOPENED, true);
    }

    private void setSortOrder(final String key, final String value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(key, value);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(ARTIST_SORT_ORDER, value);
    }

    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }

    public static String FOLDER_SONG_SORT_ORDER = "folder_sort";

    public void setFolerSortOrder(final String value) {
        setSortOrder(FOLDER_SONG_SORT_ORDER, value);
    }

    public final String getFoloerSortOrder() {
        return mPreferences.getString(FOLDER_SONG_SORT_ORDER, SortOrder.FolderSortOrder.FOLDER_A_Z);
    }

    public void setArtistSongSortOrder(final String value) {
        setSortOrder(ARTIST_SONG_SORT_ORDER, value);
    }

    public final String getArtistAlbumSortOrder() {
        return mPreferences.getString(ARTIST_ALBUM_SORT_ORDER,
                SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z);
    }

    public void setArtistAlbumSortOrder(final String value) {
        setSortOrder(ARTIST_ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(SONG_SORT_ORDER, value);
    }

    public final boolean didNowplayingThemeChanged() {
        return mPreferences.getBoolean(NOW_PLAYNG_THEME_VALUE, false);
    }

    public void setNowPlayingThemeChanged(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(NOW_PLAYNG_THEME_VALUE, value);
        editor.apply();
    }

    public void setFilterSize(int size) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("filtersize", size);
        editor.apply();
    }

    public void setFilterTime(int time) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("filtertime", time);
        editor.apply();
    }

    public int getFilterSize() {
        return mPreferences.getInt("filtersize", 1024 * 1024);
    }

    public int getFilterTime() {
        return mPreferences.getInt("filtertime", 60 * 1000);
    }

}