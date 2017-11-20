/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.example.yungui.music.service;


import com.example.yungui.music.IMusicAidlInterface;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.util.Arrays;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * 音乐播放器。
 * 此类作为中间类，连接UI同时与服务结合，作为在沟通桥梁，
 * 实现了ServiceConnection回调，
 * 又持有UI的ServiceConnection，
 */
public class MusicPlayer {

    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;
    private static final long[] sEmptyList;
    //在baseActivity 中实例化了
    public static IMusicAidlInterface mService = null;
    private static ContentValues[] mContentValuesCache = null;

    static {
        mConnectionMap = new WeakHashMap<Context, ServiceBinder>();
        sEmptyList = new long[0];
    }

    /**
     * 绑定到服务
     *
     * @param context             上下文 activity，fragment
     * @param UIServiceConnection activity，fragment等传递过来的ServiceConnection回调接口
     * @return
     */
    public static final ServiceToken bindToService(final Context context,
                                                   final ServiceConnection UIServiceConnection) {

        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        final ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        //开启服务
        contextWrapper.startService(new Intent(contextWrapper, MediaService.class));
        //新建自定义的binder类，将UI总传递过来的ServiceConnection，传入类中
        final ServiceBinder binder = new ServiceBinder(UIServiceConnection,
                contextWrapper.getApplicationContext());
        //判断是否绑定成功
        if (contextWrapper.bindService(new Intent().setClass(contextWrapper, MediaService.class), binder, 0)) {
            mConnectionMap.put(contextWrapper, binder);
            System.out.println("bindToService>>>>>>>>>>"+ "绑定服务成功" );
            return new ServiceToken(contextWrapper);
        }
        return null;
    }

    /**
     * 从服务中解绑
     *
     * @param token
     */
    public static void unbindFromService(final ServiceToken token) {
        if (token == null) {
            return;
        }
        final ContextWrapper mContextWrapper = token.mWrappedContext;
        final ServiceBinder mBinder = mConnectionMap.remove(mContextWrapper);
        if (mBinder == null) {
            return;
        }
        //真正的解绑服务
        mContextWrapper.unbindService(mBinder);
        if (mConnectionMap.isEmpty()) {
            mService = null;
        }
    }

    /**
     * 判断后台服务是否连接成功
     *
     * @return
     */
    public static final boolean isPlaybackServiceConnected() {
        return mService != null;
    }

    /**
     * 播放下一首
     */
    public static void next() {
        try {
            if (mService != null) {
                //跨进程调用
                mService.next();
            }
        } catch (final RemoteException ignored) {
        }
    }

    /**
     * 初始化后台播放设置
     *
     * @param context
     */
    public static void initPlaybackServiceWithSettings(final Context context) {
        //设置是否在锁屏界面上显示音乐专辑
        setShowAlbumArtOnLockscreen(true);
    }

    /***
     * 锁屏上显示专辑图片
     * @param enabled
     */
    public static void setShowAlbumArtOnLockscreen(final boolean enabled) {
        try {
            if (mService != null) {
                //远程调用
                mService.setLockscreenAlbumArt(enabled);
            }
        } catch (final RemoteException ignored) {
        }
    }

    /**
     * 异步播放下一首，
     *
     * @param context
     */
    public static void asyncNext(final Context context) {
        final Intent previous = new Intent(context, MediaService.class);
        previous.setAction(MediaService.NEXT_ACTION);
        context.startService(previous);
    }

    /**
     * 播放前一首
     * @param context
     * @param force
     */
    public static void previous(final Context context, final boolean force) {
        final Intent previous = new Intent(context, MediaService.class);
        if (force) {
            previous.setAction(MediaService.PREVIOUS_FORCE_ACTION);
        } else {
            previous.setAction(MediaService.PREVIOUS_ACTION);
        }
        context.startService(previous);
    }

    /**
     * 暂停或者播放
     */
    public static void playOrPause() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                } else {
                    mService.play();
                }
            }
        } catch (final Exception ignored) {
        }
    }

    /**
     * 跟踪位置
     *
     * @return
     */
    public static boolean isTrackLocal() {
        try {
            if (mService != null) {
                return mService.isTrackLocal();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 切换循环播放
     */
    public static void cycleRepeat() {
        try {
            if (mService != null) {
                //随机播放模式
                if (mService.getShuffleMode() == MediaService.SHUFFLE_NORMAL) {
                    //停止随机
                    mService.setShuffleMode(MediaService.SHUFFLE_NONE);
                    //开启循环模式
                    mService.setRepeatMode(MediaService.REPEAT_CURRENT);
                    return;
                } else {
                    switch (mService.getRepeatMode()) {
                        //单曲循环
                        case MediaService.REPEAT_CURRENT:
                            mService.setRepeatMode(MediaService.REPEAT_ALL);
                            break;
                        //列表循环
                        case MediaService.REPEAT_ALL:
                            mService.setShuffleMode(MediaService.SHUFFLE_NORMAL);
//                        if (mService.getShuffleMode() != MediaService.SHUFFLE_NONE) {
//                            mService.setShuffleMode(MediaService.SHUFFLE_NONE);
//                        }
                            break;

                    }
                }

            }
        } catch (final RemoteException ignored) {
        }
    }

    /**
     * 随机循环
     */
    public static void cycleShuffle() {
        try {
            if (mService != null) {
                switch (mService.getShuffleMode()) {
                    case MediaService.SHUFFLE_NONE:
                        mService.setShuffleMode(MediaService.SHUFFLE_NORMAL);
                        if (mService.getRepeatMode() == MediaService.REPEAT_CURRENT) {
                            mService.setRepeatMode(MediaService.REPEAT_ALL);
                        }
                        break;
                    case MediaService.SHUFFLE_NORMAL:
                        mService.setShuffleMode(MediaService.SHUFFLE_NONE);
                        break;
//                    case MediaService.SHUFFLE_AUTO:
//                        mService.setShuffleMode(MediaService.SHUFFLE_NONE);
//                        break;
                    default:
                        break;
                }
            }
        } catch (final RemoteException ignored) {
        }
    }

    /***
     * 判断是否正在播放
     * @return
     */
    public static final boolean isPlaying() {
        if (mService != null) {
            try {
                return mService.isPlaying();
            } catch (final RemoteException ignored) {
            }
        }
        return false;
    }

    /**
     * 获取随机播放模式
     *
     * @return
     */
    public static final int getShuffleMode() {
        if (mService != null) {
            try {
                return mService.getShuffleMode();
            } catch (final RemoteException ignored) {
            }
        }
        return 0;
    }

    /**
     * 设置随机播放模式
     *
     * @param mode
     */
    public static void setShuffleMode(int mode) {
        try {
            if (mService != null) {
                mService.setShuffleMode(mode);
            }
        } catch (RemoteException ignored) {

        }
    }

    /**
     * 获取循环模式
     *
     * @return
     */
    public static final int getRepeatMode() {
        if (mService != null) {
            try {
                return mService.getRepeatMode();
            } catch (final RemoteException ignored) {
            }
        }
        return 0;
    }

    /**
     * 获取追踪的名字
     * @return
     */
    public static final String getTrackName() {
        if (mService != null) {
            try {
                return mService.getTrackName();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 获取艺术家名字
     *
     * @return
     */
    public static final String getArtistName() {
        if (mService != null) {
            try {
                return mService.getArtistName();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 获取征集专辑名字
     *
     * @return
     */
    public static final String getAlbumName() {
        if (mService != null) {
            try {
                return mService.getAlbumName();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 专辑路径
     *
     * @return
     */
    public static final String getAlbumPath() {
        if (mService != null) {
            try {
                return mService.getAlbumPath();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 所有专辑路径
     *
     * @return
     */
    public static final String[] getAlbumPathAll() {
        if (mService != null) {
            try {
                return mService.getAlbumPathtAll();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 当前专辑id
     *
     * @return
     */
    public static final long getCurrentAlbumId() {
        if (mService != null) {
            try {
                return mService.getAlbumId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**获取当前的声音id
     * @return
     */
    public static final long getCurrentAudioId() {
        if (mService != null) {
            try {
                return mService.getAudioId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**
     * 获取音乐追踪器
     *
     * @return
     */
    public static final MusicTrack getCurrentTrack() {
        if (mService != null) {
            try {
                return mService.getCurrentTrack();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 获取指定的追踪器
     *
     * @param index
     * @return
     */
    public static final MusicTrack getTrack(int index) {
        if (mService != null) {
            try {
                return mService.getTrack(index);
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 获取下一个 声音 的id
     *
     * @return
     */
    public static final long getNextAudioId() {
        if (mService != null) {
            try {
                return mService.getNextAudioId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**
     * 获取上一个 声音 的id
     *
     * @return
     */
    public static final long getPreviousAudioId() {
        if (mService != null) {
            try {
                return mService.getPreviousAudioId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**
     * 当前艺术家id
     *
     * @return
     */
    public static final long getCurrentArtistId() {
        if (mService != null) {
            try {
                return mService.getArtistId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**
     * 获取声音会话id
     * @return
     */
    public static final int getAudioSessionId() {
        if (mService != null) {
            try {
                return mService.getAudioSessionId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**
     * 获取歌曲列表
     *
     * @return
     */
    public static final long[] getQueue() {
        try {
            if (mService != null) {
                return mService.getQueue();
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return sEmptyList;
    }

    /**
     * 获取播放信息
     *
     * @return
     */
    public static final HashMap<Long, MusicInfo> getPlayinfos() {
        try {
            if (mService != null) {
                return (HashMap<Long, MusicInfo>) mService.getPlayinfos();
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return null;
    }

    public static final long getQueueItemAtPosition(int position) {
        try {
            if (mService != null) {
                return mService.getQueueItemAtPosition(position);
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return -1;
    }

    /**
     * 列表size
     *
     * @return
     */
    public static final int getQueueSize() {
        try {
            if (mService != null) {
                return mService.getQueueSize();
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return 0;
    }

    /**
     * 获取列队位置
     * @return
     */
    public static final int getQueuePosition() {
        try {
            if (mService != null) {
                return mService.getQueuePosition();
            }
        } catch (final RemoteException ignored) {
        }
        return 0;
    }

    /**
     * 设置列队位置
     * @param position
     */
    public static void setQueuePosition(final int position) {
        if (mService != null) {
            try {
                mService.setQueuePosition(position);
            } catch (final RemoteException ignored) {
            }
        }
    }

    /**
     * 获取列队历史大小
     * @return
     */
    public static final int getQueueHistorySize() {
        if (mService != null) {
            try {
                return mService.getQueueHistorySize();
            } catch (final RemoteException ignored) {
            }
        }
        return 0;
    }

    /**
     * 获取列队历史位置
     * @param position
     * @return
     */
    public static final int getQueueHistoryPosition(int position) {
        if (mService != null) {
            try {
                return mService.getQueueHistoryPosition(position);
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }

    /**
     * 获取列队历史集合
     * @return
     */
    public static final int[] getQueueHistoryList() {
        if (mService != null) {
            try {
                return mService.getQueueHistoryList();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    /**
     * 移除追踪器
     *
     * @param id
     * @return
     */
    public static final int removeTrack(final long id) {
        try {
            if (mService != null) {
                return mService.removeTrack(id);
            }
        } catch (final RemoteException ingored) {
        }
        return 0;
    }

    /**
     * 移除指定位置的追踪器
     *
     * @param id
     * @param position
     * @return
     */
    public static final boolean removeTrackAtPosition(final long id, final int position) {
        try {
            if (mService != null) {
                return mService.removeTrackAtPosition(id, position);
            }
        } catch (final RemoteException ingored) {
        }
        return false;
    }

    public static void moveQueueItem(final int from, final int to) {
        try {
            if (mService != null) {
                mService.moveQueueItem(from, to);
            } else {
            }
        } catch (final RemoteException ignored) {
        }
    }

    /**
     * 播放所有
     *
     * @param infos
     * @param list 歌曲id数组
     * @param position 点击的位置
     * @param forceShuffle  是否强行插入
     */
    public static synchronized void playAll(final HashMap<Long, MusicInfo> infos,
                                            final long[] list,
                                            int position,
                                            final boolean forceShuffle) {

        if (list == null || list.length == 0 || mService == null) {
            return;
        }
        try {
            //重置播放模式
            if (forceShuffle) {
                mService.setShuffleMode(MediaService.SHUFFLE_NORMAL);
            }
            //播放的id
            final long currentId = mService.getAudioId();
            long playId = list[position];
            Log.e("currentId", currentId + "");
            final int currentQueuePosition = getQueuePosition();
            if (position != -1) {
                final long[] playlist = getQueue();
                //比较长度是否一致
                if (Arrays.equals(list, playlist)) {
                    if (currentQueuePosition == position && currentId == list[position]) {
                        mService.play();
                        return;
                    } else {
                        mService.setQueuePosition(position);
                        return;
                    }

                }
            }
            if (position < 0) {
                position = 0;
            }
            mService.open(infos, list, forceShuffle ? -1 : position);
            mService.play();
            Log.e("time", System.currentTimeMillis() + "");
        } catch (final RemoteException ignored) {
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放下一首
     *
     * @param context
     * @param map
     * @param list
     */
    public static void playNext(Context context, final HashMap<Long, MusicInfo> map, final long[] list) {
        if (mService == null) {
            return;
        }
        try {
            int current = -1;
            long[] result = list;

            for (int i = 0; i < list.length; i++) {
                if (MusicPlayer.getCurrentAudioId() == list[i]) {
                    current = i;
                } else {
                    MusicPlayer.removeTrack(list[i]);
                }
            }

//            if( current != -1){
//                ArrayList lists = new ArrayList();
//                for(int i = 0; i<list.length;i++){
//                    if(i != current){
//                        lists.add(list[i]);
//                    }
//                }
//                result = new long[list.length - 1];
//                for(int i = 0;i<lists.size();i++){
//                     result[i] = (long) lists.get(i);
//                }
//            }

            mService.enqueue(list, map, MediaService.NEXT);

            Toast.makeText(context, R.string.next_play, Toast.LENGTH_SHORT).show();
        } catch (final RemoteException ignored) {
        }
    }

    public static String getPath() {
        if (mService == null) {
            return null;
        }
        try {
            return mService.getPath();

        } catch (Exception e) {

        }
        return null;
    }

    public static void stop() {
        try {
            mService.stop();
        } catch (Exception e) {

        }
    }

    public static final int getSongCountForAlbumInt(final Context context, final long id) {
        int songCount = 0;
        if (id == -1) {
            return songCount;
        }

        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id);
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                if (!cursor.isNull(0)) {
                    songCount = cursor.getInt(0);
                }
            }
            cursor.close();
            cursor = null;
        }

        return songCount;
    }

    public static final String getReleaseDateForAlbum(final Context context, final long id) {
        if (id == -1) {
            return null;
        }
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id);
        Cursor cursor = context.getContentResolver().query(uri, new String[]{
                MediaStore.Audio.AlbumColumns.FIRST_YEAR
        }, null, null, null);
        String releaseDate = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                releaseDate = cursor.getString(0);
            }
            cursor.close();
            cursor = null;
        }
        return releaseDate;
    }

    public static void seek(final long position) {
        if (mService != null) {
            try {
                mService.seek(position);
            } catch (final RemoteException ignored) {
            }
        }
    }

    public static void seekRelative(final long deltaInMs) {
        if (mService != null) {
            try {
                mService.seekRelative(deltaInMs);
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ignored) {

            }
        }
    }

    public static final long position() {
        if (mService != null) {
            try {
                return mService.position();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ex) {

            }
        }
        return 0;
    }

    public static final int secondPosition() {
        if (mService != null) {
            try {
                return mService.secondPosition();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ex) {

            }
        }
        return 0;
    }

    public static final long duration() {
        if (mService != null) {
            try {
                return mService.duration();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ignored) {

            }
        }
        return 0;
    }

    public static void clearQueue() {

        try {
            if (mService != null)
                mService.removeTracks(0, Integer.MAX_VALUE);
        } catch (final RemoteException ignored) {
        }
    }

    public static void addToQueue(final Context context, final long[] list, long sourceId) {
        if (mService == null) {
            return;
        }
        try {
            mService.enqueue(list, null, MediaService.LAST);
            //final String message = makeLabel(context, R.plurals.NNNtrackstoqueue, list.length);
            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (final RemoteException ignored) {
        }
    }

    /**
     * 加入播放列表
     *
     * @param context
     * @param ids
     * @param playlistid
     */
    public static void addToPlaylist(final Context context, final long[] ids, final long playlistid) {
        final int size = ids.length;
        final ContentResolver resolver = context.getContentResolver();
        final String[] projection = new String[]{
                "max(" + "play_order" + ")",
        };
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        Cursor cursor = null;
        int base = 0;

        try {
            cursor = resolver.query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                base = cursor.getInt(0) + 1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        int numinserted = 0;
        for (int offSet = 0; offSet < size; offSet += 1000) {
            makeInsertItems(ids, offSet, 1000, base);
            numinserted += resolver.bulkInsert(uri, mContentValuesCache);
        }

    }

    /**
     * 插入项目
     *
     * @param ids
     * @param offset
     * @param len
     * @param base
     */
    public static void makeInsertItems(final long[] ids, final int offset, int len, final int base) {
        if (offset + len > ids.length) {
            len = ids.length - offset;
        }

        if (mContentValuesCache == null || mContentValuesCache.length != len) {
            mContentValuesCache = new ContentValues[len];
        }
        for (int i = 0; i < len; i++) {
            if (mContentValuesCache[i] == null) {
                mContentValuesCache[i] = new ContentValues();
            }
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids[offset + i]);
        }
    }

    /**
     * 新建播放播单
     *
     * @param context
     * @param name
     * @return
     */
    public static final long createPlaylist(final Context context, final String name) {
        if (name != null && name.length() > 0) {
            final ContentResolver resolver = context.getContentResolver();
            final String[] projection = new String[]{
                    MediaStore.Audio.PlaylistsColumns.NAME
            };
            final String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";
            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    projection, selection, null, null);
            if (cursor.getCount() <= 0) {
                final ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                final Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        values);
                return Long.parseLong(uri.getLastPathSegment());
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            return -1;
        }
        return -1;
    }

    /**
     * 退出服务
     */
    public static void exitService() {
//        if (mService == null) {
//            return;
//        }
        try {
            mConnectionMap.clear();
            Log.e("exitmp", "Destroying service");
            mService.exit();
        } catch (Exception e) {
        }
    }

    /**
     * 计时器
     *
     * @param time
     */
    public static void timing(int time) {
        if (mService == null) {
            return;
        }
        try {
            mService.timing(time);
        } catch (Exception e) {

        }
    }

    /**
     * 自定义的binder是实现了ServiceConnection接口
     */
    public static final class ServiceBinder implements ServiceConnection {
        private final ServiceConnection mCallback;
        private final Context mContext;

        //构造方法，在传入ServiceConnection
        public ServiceBinder(final ServiceConnection callback, final Context context) {
            mCallback = callback;
            mContext = context;
        }

        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            //连接远程服务
            mService = IMusicAidlInterface.Stub.asInterface(service);
            if (mCallback != null) {
                mCallback.onServiceConnected(className, service);
            }
            initPlaybackServiceWithSettings(mContext);
        }

        @Override
        public void onServiceDisconnected(final ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            mService = null;
        }
    }

    //服务令牌 传递ContextWrapper
    public static final class ServiceToken {
        public ContextWrapper mWrappedContext;
        public ServiceToken(final ContextWrapper context) {
            mWrappedContext = context;
        }
    }


}
