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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.yungui.music.IMusicAidlInterface;
import com.example.yungui.music.MainApplication;
import com.example.yungui.music.R;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.model.SongPlayCount;
import com.example.yungui.music.permission.PermissionHelper;
import com.example.yungui.music.provider.MusicPlaybackState;
import com.example.yungui.music.provider.RecentStore;
import com.example.yungui.music.receiver.MediaButtonIntentReceiver;
import com.example.yungui.music.utils.CommonUtils;
import com.example.yungui.music.utils.ImageUtils;
import com.example.yungui.music.utils.L;
import com.example.yungui.music.utils.MediaPlayerProxy;
import com.example.yungui.music.utils.PreferencesUtility;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

/**
 * 正真的后台服务类，在独立的进程中，通过代理类控制AIDL来操作服务类
 */

@SuppressLint("NewApi")
public class MediaService extends Service {
    public static final String PLAYSTATE_CHANGED = "com.example.yungui.music.playstatechanged";
    public static final String POSITION_CHANGED = "com.example.yungui.music.positionchanged";
    public static final String META_CHANGED = "com.example.yungui.music.metachanged";
    public static final String PLAYLIST_ITEM_MOVED = "com.example.yungui.music.mmoved";
    public static final String QUEUE_CHANGED = "com.example.yungui.music.queuechanged";
    public static final String PLAYLIST_CHANGED = "com.example.yungui.music.playlistchanged";
    public static final String REPEATMODE_CHANGED = "com.example.yungui.music.repeatmodechanged";
    public static final String SHUFFLEMODE_CHANGED = "com.example.yungui.music.shufflemodechanged";
    public static final String TRACK_ERROR = "com.example.yungui.music.trackerror";
    public static final String TIMBER_PACKAGE_NAME = "com.example.yungui.music";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";
    public static final String SERVICECMD = "com.example.yungui.music.musicservicecommand";
    public static final String TOGGLEPAUSE_ACTION = "com.example.yungui.music.togglepause";
    public static final String PAUSE_ACTION = "com.example.yungui.music.pause";
    public static final String PREVIOUS_ACTION = "com.example.yungui.music.previous";
    public static final String STOP_ACTION = "com.example.yungui.music.stop";
    public static final String PREVIOUS_FORCE_ACTION = "com.example.yungui.music.previous.force";
    public static final String NEXT_ACTION = "com.example.yungui.music.next";
    public static final String MUSIC_CHANGED = "com.example.yungui.music.change_music";
    public static final String REPEAT_ACTION = "com.example.yungui.music.repeat";
    public static final String SHUFFLE_ACTION = "com.example.yungui.music.shuffle";
    public static final String FROM_MEDIA_BUTTON = "frommediabutton";
    public static final String REFRESH = "com.example.yungui.music.refresh";
    public static final String LRC_UPDATED = "com.example.yungui.music.updatelrc";
    public static final String UPDATE_LOCKSCREEN = "com.example.yungui.music.updatelockscreen";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDNOTIF = "buttonId";
    public static final String TRACK_PREPARED = "com.example.yungui.music.prepared";
    public static final String TRY_GET_TRACKINFO = "com.example.yungui.music.gettrackinfo";
    public static final String BUFFER_UP = "com.example.yungui.music.bufferup";
    public static final String LOCK_SCREEN = "com.example.yungui.music.lock";
    public static final String SEND_PROGRESS = "com.example.yungui.music.progress";
    public static final String MUSIC_LODING = "com.example.yungui.music.loading";
    private static final String SHUTDOWN = "com.example.yungui.music.shutdown";
    public static final String SETQUEUE = "com.example.yungui.music.setqueue";
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    public static final int REPEAT_NONE = 2;
    public static final int REPEAT_CURRENT = 1;//单曲循环
    public static final int REPEAT_ALL = 2;//
    public static final int MAX_HISTORY_SIZE = 1000;
    private static final String TAG = "MusicPlaybackService";
    private static final boolean D = true;
    private static final int LRC_DOWNLOADED = -10;
    private static final int IDCOLIDX = 0;
    private static final int TRACK_ENDED = 1;
    private static final int TRACK_WENT_TO_NEXT = 2;
    private static final int RELEASE_WAKELOCK = 3;
    private static final int SERVER_DIED = 4;
    private static final int FOCUSCHANGE = 5;
    private static final int FADEDOWN = 6;
    private static final int FADEUP = 7;
    private static final int IDLE_DELAY = 5 * 60 * 1000;//五分钟关闭
    private static final long REWIND_INSTEAD_PREVIOUS_THRESHOLD = 3000;
    private static final String[] PROJECTION = new String[]{
            "audio._id AS _id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    private static final String[] ALBUM_PROJECTION = new String[]{
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.LAST_YEAR
    };
    private static final Shuffler mShuffler = new Shuffler();
    private static final int NOTIFY_MODE_NONE = 0;
    private static final int NOTIFY_MODE_FOREGROUND = 1;
    private static final int NOTIFY_MODE_BACKGROUND = 2;
    private static final String[] PROJECTION_MATRIX = new String[]{
            "_id", MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    private static LinkedList<Integer> mHistory = new LinkedList<>();
    //连接后返回的IBinder对象
    private final IBinder mBinder = new ServiceStub(this);
    //service内部使用的播放器
    private MultiPlayer mPlayer;
    private String mFileToPlay;
    private WakeLock mWakeLock;
    private AlarmManager mAlarmManager;
    private PendingIntent mShutdownIntent;
    private boolean mShutdownScheduled;
    private NotificationManager mNotificationManager;
    private Cursor mCursor;
    private Cursor mAlbumCursor;
    private AudioManager mAudioManager;
    private SharedPreferences mPreferences;
    private boolean mServiceInUse = false;
    private boolean mIsSupposedToBePlaying = false;
    private long mLastPlayedTime;
    private int mNotifyMode = NOTIFY_MODE_NONE;
    private long mNotificationPostTime = 0;
    private boolean mQueueIsSaveable = true;
    private boolean mPausedByTransientLossOfFocus = false;

    private MediaSession mSession;

    private ComponentName mMediaButtonReceiverComponent;
    //SD卡标识
    private int mCardId;
    //播放位置
    private int mPlayPos = -1;
    //下一个播放位置
    private int mNextPlayPos = -1;
    //打开失败的计数
    private int mOpenFailedCounter = 0;
    //媒体安装数
    private int mMediaMountedCount = 0;

    private int mShuffleMode = SHUFFLE_NONE;

    private int mRepeatMode = REPEAT_ALL;

    private int mServiceStartId = -1;
    //音轨
    private ArrayList<MusicTrack> mPlaylist = new ArrayList<MusicTrack>(100);
    //音乐信息
    private HashMap<Long, MusicInfo> mPlaylistInfo = new HashMap<>();

    private long[] mAutoShuffleList = null;

    private MusicPlayerHandler mPlayerHandler;

    private HandlerThread mHandlerThread;
    private BroadcastReceiver mUnmountReceiver = null;
    private MusicPlaybackState mPlaybackStateStore;
    private boolean mShowAlbumArtOnLockscreen;
    private SongPlayCount mSongPlayCount;
    private RecentStore mRecentStore;
    private int mNotificationId = 1000;

    private ContentObserver mMediaStoreObserver;
    private static Handler mUrlHandler;
    private static Handler mLrcHandler;
    private MediaPlayerProxy mProxy;
    public static final String LRC_PATH = "/remusic/lrc/";
    private long mLastSeekPos = 0;
    private RequestPlayUrl mRequestUrl;
    private RequestLrc mRequestLrc;
    private boolean mIsSending = false;
    private boolean mIsLocked;
    private Bitmap mNoBit;
    private Notification mNotification;
    //获取歌词的线程
    private Thread mLrcThread = new Thread(new Runnable() {
        @Override
        public void run() {
            //创建一套完整handler机制
            Looper.prepare();
            mLrcHandler = new Handler();
            Looper.loop();
        }
    });
    //获取链接的线程
    private Thread mGetUrlThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            mUrlHandler = new Handler();
            Looper.loop();
        }
    });
    //AudioManager的监听焦点监听器
    private final OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(final int focusChange) {
            mPlayerHandler
                    .obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };


    /**
     * 广播接受器
     */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String command = intent.getStringExtra(CMDNAME);

            Log.d(TAG, "onreceive" + intent.toURI());
            //处理接收到的广播
            handleCommandIntent(intent);

        }
    };

    /**
     * 绑定成功，并返回IBinder对象
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(final Intent intent) {
        if (D) Log.d(TAG, "Service bound, intent = " + intent);
        //取消关闭
        cancelShutdown();
        //服务正在使用中
        mServiceInUse = true;
        return mBinder;
    }

    /**
     * 解绑
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(final Intent intent) {
        if (D) Log.d(TAG, "Service unbound");
        //服务没有在使用
        mServiceInUse = false;
        //存储列队
        saveQueue(true);
        if (mIsSupposedToBePlaying || mPausedByTransientLossOfFocus) {
            return true;

        } else if (mPlaylist.size() > 0 || mPlayerHandler.hasMessages(TRACK_ENDED)) {
            //延迟关闭
            scheduleDelayedShutdown();
            return true;
        }
        //停止服务
        stopSelf(mServiceStartId);
        return true;
    }

    /*
    重新绑定
     */
    @Override
    public void onRebind(final Intent intent) {
        cancelShutdown();
        mServiceInUse = true;
    }

    //服务初始化
    @Override
    public void onCreate() {
        if (D) Log.d(TAG, "Creating service");
        super.onCreate();
        //开启播放链接获取线程
        mGetUrlThread.start();
        //开启歌词获取线程
        mLrcThread.start();
        //实例化代理
        mProxy = new MediaPlayerProxy(this);
        mProxy.init();
        mProxy.start();
        //获取通知服务
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 获取显示播放状态的实例
        mPlaybackStateStore = MusicPlaybackState.getInstance(this);
        //播放次数
        mSongPlayCount = SongPlayCount.getInstance(this);
        //最近播放工具类
        mRecentStore = RecentStore.getInstance(this);

        mHandlerThread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        //实例化音乐播放处理类用来处理外部发送过来的事件
        mPlayerHandler = new MusicPlayerHandler(this, mHandlerThread.getLooper());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaButtonReceiverComponent = new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName());
        // 将组建注册为媒体按钮的唯一接收器
        mAudioManager.registerMediaButtonEventReceiver(mMediaButtonReceiverComponent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //简历媒体会话
            setUpMediaSession();
        }
        //获取一个共享存储
        mPreferences = getSharedPreferences("Service", 0);
        //获取SD卡标识ID
        mCardId = getCardId();
        //注册外置存储监听器
        registerExternalStorageListener();
        //实例化多播放器
        mPlayer = new MultiPlayer(this);
        mPlayer.setHandler(mPlayerHandler);

        // 广播接收器所能接受的广播
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICECMD);//服务连接诶
        filter.addAction(TOGGLEPAUSE_ACTION);
        filter.addAction(PAUSE_ACTION);//暂停
        filter.addAction(STOP_ACTION);//停止
        filter.addAction(NEXT_ACTION);//下一首
        filter.addAction(PREVIOUS_ACTION);//上一首
        filter.addAction(PREVIOUS_FORCE_ACTION);//强制上一首
        filter.addAction(REPEAT_ACTION);//重复
        filter.addAction(SHUFFLE_ACTION);//随机
        filter.addAction(TRY_GET_TRACKINFO);//尝试获取追踪信息
        filter.addAction(Intent.ACTION_SCREEN_OFF);//息屏
        filter.addAction(LOCK_SCREEN);//锁屏
        filter.addAction(SEND_PROGRESS);//发送进度
        filter.addAction(SETQUEUE);//设置播放列队
        // //注册广播接收器
        registerReceiver(mIntentReceiver, filter);
        //实例化媒体观察者
        mMediaStoreObserver = new MediaStoreObserver(mPlayerHandler);
        //对内置存储和外置存储（不包括SD卡）的都监听器媒体的变化
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, mMediaStoreObserver);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mMediaStoreObserver);

        //锁屏开关
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.setReferenceCounted(false);

        final Intent shutdownIntent = new Intent(this, MediaService.class);
        shutdownIntent.setAction(SHUTDOWN);

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mShutdownIntent = PendingIntent.getService(this, 0, shutdownIntent, 0);

        scheduleDelayedShutdown();
        //------------------  获取歌曲信息 的等等---------------------------------
        //权限检查完毕之后重新获取列表
        reloadQueueAfterPermissionCheck();
        //通知更新
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);
    }

    /**
     * 建立媒体会话
     */
    private void setUpMediaSession() {
        mSession = new MediaSession(this, "remusic");
        mSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPause() {
                pause();
                mPausedByTransientLossOfFocus = false;
            }

            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onSeekTo(long pos) {
                seek(pos);
            }

            @Override
            public void onSkipToNext() {
                gotoNext(true);
            }

            @Override
            public void onSkipToPrevious() {
                prev(false);
            }

            @Override
            public void onStop() {
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
                releaseServiceUiAndStop();
            }
        });
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
    }
    /*
    退出播放服务
     */
    public void exit() {

    }

    @Override
    public void onDestroy() {
        if (D) {
            Log.d(TAG, "Destroying service");
        }
        super.onDestroy();
        // Remove any sound effects
        final Intent audioEffectsIntent = new Intent(
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(audioEffectsIntent);

        cancelNotification();

        mAlarmManager.cancel(mShutdownIntent);

        mPlayerHandler.removeCallbacksAndMessages(null);

        if (CommonUtils.isJellyBeanMR2()) {
            mHandlerThread.quitSafely();
        } else {
            mHandlerThread.quit();
        }

        mPlayer.release();
        mPlayer = null;

        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSession.release();
        }

        getContentResolver().unregisterContentObserver(mMediaStoreObserver);

        closeCursor();

        unregisterReceiver(mIntentReceiver);
        if (mUnmountReceiver != null) {
            unregisterReceiver(mUnmountReceiver);
            mUnmountReceiver = null;
        }

        mWakeLock.release();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (D) {
            Log.d(TAG, "Got new intent " + intent + ", startId = " + startId);
        }
        mServiceStartId = startId;
        if (intent != null) {
            final String action = intent.getAction();

            if (SHUTDOWN.equals(action)) {
                mShutdownScheduled = false;
                releaseServiceUiAndStop();
                return START_NOT_STICKY;
            }
            handleCommandIntent(intent);
        }

        scheduleDelayedShutdown();

        if (intent != null && intent.getBooleanExtra(FROM_MEDIA_BUTTON, false)) {
            MediaButtonIntentReceiver.completeWakefulIntent(intent);
        }
        return START_STICKY;
    }

    //释放接收器并且停止
    private void releaseServiceUiAndStop() {
        if (isPlaying()
                || mPausedByTransientLossOfFocus
                || mPlayerHandler.hasMessages(TRACK_ENDED)) {
            return;
        }

        if (D) {
            Log.d(TAG, "Nothing is playing anymore, releasing notification");
        }
        cancelNotification();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSession.setActive(false);
        }

        if (!mServiceInUse) {
            saveQueue(true);
            stopSelf(mServiceStartId);
        }
    }

    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = SERVICECMD.equals(action) ? intent.getStringExtra(CMDNAME) : null;

        if (D) {
            Log.d(TAG, "handleCommandIntent: action = " + action + ", command = " + command);
        }

        if (CMDNEXT.equals(command) || NEXT_ACTION.equals(action)) {
            gotoNext(true);
        } else if (CMDPREVIOUS.equals(command)
                || PREVIOUS_ACTION.equals(action)
                || PREVIOUS_FORCE_ACTION.equals(action)) {

            prev(PREVIOUS_FORCE_ACTION.equals(action));

        } else if (CMDTOGGLEPAUSE.equals(command)
                || TOGGLEPAUSE_ACTION.equals(action)) {
            if (isPlaying()) {
                pause();
                mPausedByTransientLossOfFocus = false;
            } else {
                play();
            }
        } else if (CMDPAUSE.equals(command)
                || PAUSE_ACTION.equals(action)) {
            pause();
            mPausedByTransientLossOfFocus = false;

        } else if (CMDPLAY.equals(command)) {
            play();
        } else if (CMDSTOP.equals(command)
                || STOP_ACTION.equals(action)) {

            pause();
            mPausedByTransientLossOfFocus = false;
            seek(0);
            releaseServiceUiAndStop();

        } else if (REPEAT_ACTION.equals(action)) {
            cycleRepeat();
        } else if (SHUFFLE_ACTION.equals(action)) {
            cycleShuffle();
        } else if (TRY_GET_TRACKINFO.equals(action)) {
            getLrc(mPlaylist.get(mPlayPos).mId);
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {

        } else if (LOCK_SCREEN.equals(action)) {
            mIsLocked = intent.getBooleanExtra("islock", true);
            L.D(D, TAG, "isloced = " + mIsLocked);
        } else if (SEND_PROGRESS.equals(action)) {
            if (isPlaying() && !mIsSending) {
                mPlayerHandler.post(sendDuration);
                mIsSending = true;
            } else if (!isPlaying()) {
                mPlayerHandler.removeCallbacks(sendDuration);
                mIsSending = false;
            }

        } else if (SETQUEUE.equals(action)) {
            Log.e("playab", "action");
            setQueuePosition(intent.getIntExtra("position", 0));
        }
    }

    private Runnable sendDuration = new Runnable() {
        @Override
        public void run() {
            notifyChange(SEND_PROGRESS);
            mPlayerHandler.postDelayed(sendDuration, 1000);
        }
    };

    /**
     * 更xin通知栏
     */
    private void updateNotification() {
        final int newNotifyMode;
        if (isPlaying()) {
            newNotifyMode = NOTIFY_MODE_FOREGROUND;
        } else if (recentlyPlayed()) {
            newNotifyMode = NOTIFY_MODE_BACKGROUND;
        } else {
            newNotifyMode = NOTIFY_MODE_NONE;
        }

        // int mNotificationId = hashCode();

        if (mNotifyMode != newNotifyMode) {
            if (mNotifyMode == NOTIFY_MODE_FOREGROUND) {
                if (CommonUtils.isLollipop()) {
                    stopForeground(newNotifyMode == NOTIFY_MODE_NONE);
                } else {
                    stopForeground(newNotifyMode == NOTIFY_MODE_NONE || newNotifyMode == NOTIFY_MODE_BACKGROUND);
                }
            } else if (newNotifyMode == NOTIFY_MODE_NONE) {
                mNotificationManager.cancel(mNotificationId);
                mNotificationPostTime = 0;
            }
        }

        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            startForeground(mNotificationId, getNotification());

        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            mNotificationManager.notify(mNotificationId, getNotification());
        }

        mNotifyMode = newNotifyMode;
    }

    /**
     * 取消状态栏
     */
    private void cancelNotification() {
        stopForeground(true);
        //mNotificationManager.cancel(hashCode());
        mNotificationManager.cancel(mNotificationId);
        mNotificationPostTime = 0;
        mNotifyMode = NOTIFY_MODE_NONE;
    }

    /**
     * 获取SD卡的id
     *
     * @return
     */
    private int getCardId() {
        //6.0以上需要获得权限
        if (CommonUtils.isMarshmallow()) {
            if (PermissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                return getmCardId();
            } else {
                return 0;
            }
        } else {
            return getmCardId();
        }
    }

    /**
     * 获取SD卡的id
     *
     * @return
     */
    private int getmCardId() {
        final ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(Uri.parse("content://media/external/fs_id"), null, null,
                null, null);
        int mCardId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            mCardId = cursor.getInt(0);
            cursor.close();
        }
        return mCardId;
    }

    /**
     * 关闭外部存储文件
     * @param storagePath
     */
    public void closeExternalStorageFiles(final String storagePath) {
        stop(true);
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);
    }

    /**
     * 注外部文件监听器
     */
    public void registerExternalStorageListener() {
        if (mUnmountReceiver == null) {
            mUnmountReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    final String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                        saveQueue(true);
                        mQueueIsSaveable = false;
                        closeExternalStorageFiles(intent.getData().getPath());
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        mMediaMountedCount++;
                        mCardId = getCardId();
                        reloadQueueAfterPermissionCheck();
                        mQueueIsSaveable = true;
                        notifyChange(QUEUE_CHANGED);
                        notifyChange(META_CHANGED);
                    }
                }
            };
            final IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addDataScheme("file");
            registerReceiver(mUnmountReceiver, filter);
        }
    }

    /**
     * 计时关闭
     */
    private void scheduleDelayedShutdown() {
        if (D) {
            Log.v(TAG, "Scheduling shutdown in " + IDLE_DELAY + " ms");
        }
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + IDLE_DELAY, mShutdownIntent);
        mShutdownScheduled = true;
    }

    /**
     * 取消关闭
     */
    private void cancelShutdown() {
        if (D) {
            Log.d(TAG, "Cancelling delayed shutdown, scheduled = " + mShutdownScheduled);
        }
        if (mShutdownScheduled) {
            mAlarmManager.cancel(mShutdownIntent);
            mShutdownScheduled = false;
        }
    }

    /**
     * 停止播放
     *
     * @param goToIdle 是否闲置
     */
    private void stop(final boolean goToIdle) {
        if (D) {
            Log.d(TAG, "Stopping playback, goToIdle = " + goToIdle);
        }
        if (mPlayer.isInitialized()) {
            mPlayer.stop();
        }
        mFileToPlay = null;
        closeCursor();
        //如果闲置
        if (goToIdle) {
            setIsSupposedToBePlaying(false, false);
        }
// else {
//            if (CommonUtils.isLollipop())
//                stopForeground(false);
//            else stopForeground(true);
//        }
    }

    /**
     * 删除内部跟踪
     *
     * @param first
     * @param last
     * @return
     */
    private int removeTracksInternal(int first, int last) {
        synchronized (this) {
            if (last < first) {
                return 0;
            } else if (first < 0) {
                first = 0;
            } else if (last >= mPlaylist.size()) {
                last = mPlaylist.size() - 1;
            }

            boolean gotonext = false;
            if (first <= mPlayPos && mPlayPos <= last) {
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= last - first + 1;
            }
            final int numToRemove = last - first + 1;

            if (first == 0 && last == mPlaylist.size() - 1) {
                mPlayPos = -1;
                mNextPlayPos = -1;
                mPlaylist.clear();
                mHistory.clear();
            } else {
                for (int i = 0; i < numToRemove; i++) {
                    mPlaylistInfo.remove(mPlaylist.get(first).mId);
                    mPlaylist.remove(first);

                }

                ListIterator<Integer> positionIterator = mHistory.listIterator();
                while (positionIterator.hasNext()) {
                    int pos = positionIterator.next();
                    if (pos >= first && pos <= last) {
                        positionIterator.remove();
                    } else if (pos > last) {
                        positionIterator.set(pos - numToRemove);
                    }
                }
            }
            if (gotonext) {
                if (mPlaylist.size() == 0) {
                    stop(true);
                    mPlayPos = -1;
                    closeCursor();
                } else {
                    if (mShuffleMode != SHUFFLE_NONE) {
                        mPlayPos = getNextPosition(true);
                    } else if (mPlayPos >= mPlaylist.size()) {
                        mPlayPos = 0;
                    }
                    final boolean wasPlaying = isPlaying();
                    stop(false);
                    openCurrentAndNext();
                    if (wasPlaying) {
                        play();
                    }
                }
                notifyChange(META_CHANGED);
            }
            return last - first + 1;
        }
    }

    /**
     * 加入到播放列表
     *
     * @param list     歌曲id数组
     * @param position
     */
    private void addToPlayList(final long[] list, int position) {
        final int addLen = list.length;
        if (position < 0) {
            mPlaylist.clear();
            position = 0;
        }

        mPlaylist.ensureCapacity(mPlaylist.size() + addLen);
        if (position > mPlaylist.size()) {
            position = mPlaylist.size();
        }

        final ArrayList<MusicTrack> arrayList = new ArrayList<MusicTrack>(addLen);
        for (int i = 0; i < list.length; i++) {
            arrayList.add(new MusicTrack(list[i], i));
        }

        mPlaylist.addAll(position, arrayList);

        if (mPlaylist.size() == 0) {
            closeCursor();
            notifyChange(META_CHANGED);
        }
    }

    /**
     * 根据追踪id更新指针
     * @param trackId
     */
    private void updateCursor(final long trackId) {
        MusicInfo info = mPlaylistInfo.get(trackId);
        if (mPlaylistInfo.get(trackId) != null) {
            MatrixCursor cursor = new MatrixCursor(PROJECTION);
            cursor.addRow(new Object[]{info.songId, info.artist, info.albumName, info.musicName
                    , info.data, info.albumData, info.albumId, info.artistId});
            cursor.moveToFirst();
            mCursor = cursor;
            cursor.close();
        }
    }

    /**
     * 根据条件跟你像你指针
     * @param selection
     * @param selectionArgs
     */
    private void updateCursor(final String selection, final String[] selectionArgs) {
        synchronized (this) {
            closeCursor();
            mCursor = openCursorAndGoToFirst(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION, selection, selectionArgs);
        }
    }

    /**
     * 根据统一资源标识符来更新指针
     * @param uri
     */
    private void updateCursor(final Uri uri) {
        synchronized (this) {
            closeCursor();
            mCursor = openCursorAndGoToFirst(uri, PROJECTION, null, null);
        }
    }

    /**
     * 打开指针并且移动到第一个
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @return
     */
    private Cursor openCursorAndGoToFirst(Uri uri, String[] projection,
                                          String selection, String[] selectionArgs) {
        Cursor c = getContentResolver().query(uri, projection,
                selection, selectionArgs, null);
        if (c == null) {
            return null;
        }
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        return c;
    }

    /**
     * 关闭指针
     */
    private synchronized void closeCursor() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        if (mAlbumCursor != null) {
            mAlbumCursor.close();
            mAlbumCursor = null;
        }
    }

    /**
     * 获取播放链接的线程  1.首先从SharedPreference中获取，不成功--》2.从网络中获取
     */
    class RequestPlayUrl implements Runnable {
        private long id;
        private boolean play;
        private boolean stop;

        public RequestPlayUrl(long id, boolean play) {
            this.id = id;
            this.play = play;
        }

        public void stop() {
            stop = true;
        }

        @Override
        public void run() {
            try {
                //根据id从 SharedPreferences中获取连接
                String url = PreferencesUtility.getInstance(MediaService.this).getPlayLink(id);
                if (url == null) {
//                    //没有存储连接的话，根据id从网络获取
//                    MusicFileDownInfo song = Down.getUrl(MediaService.this, id + "");
//                    if (song != null && song.getShow_link() != null) {
//                        url = song.getShow_link();
//                        //存储播放链接
//                        PreferencesUtility.getInstance(MediaService.this).setPlayLink(id, url);
//                    }
                }
                if (url != null) {
                    L.E(D, TAG, "current url = " + url);
                } else {
                    gotoNext(true);
                }

                if (!stop) {
                    startProxy();
                    // String urlEn = HttpUtil.urlEncode(url);
                    String urlEn = url;
                    urlEn = mProxy.getProxyURL(urlEn);
                    mPlayer.setDataSource(urlEn);
                }


                if (play && !stop) {
                    play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //获取歌词
    class RequestLrc implements Runnable {

        private MusicInfo musicInfo;
        private boolean stop;

        RequestLrc(MusicInfo info) {
            this.musicInfo = info;
        }

        public void stop() {
            stop = true;
        }

        @Override
        public void run() {
//            L.E(D, TAG, "start to getlrc");
//            String url = null;
//            if (musicInfo != null && musicInfo.lrc != null) {
//                url = musicInfo.lrc;
//            }
//            try {
//                JsonObject jsonObject = HttpUtil.getResposeJsonObject(BMA.Search.searchLrcPic(musicInfo.musicName, musicInfo.artist));
//                JsonArray array = jsonObject.get("songinfo").getAsJsonArray();
//                int len = array.size();
//                url = null;
//                for (int i = 0; i < len; i++) {
//                    url = array.get(i).getAsJsonObject().get("lrclink").getAsString();
//                    if (url != null) {
//                        L.D(D, TAG, "lrclink = " + url);
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (!stop) {
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + LRC_PATH + musicInfo.songId);
//                String lrc = null;
//                try {
//                    lrc = HttpUtil.getResposeString(url);
//                    if (lrc != null && !lrc.isEmpty()) {
//                        if (!file.exists())
//                            file.createNewFile();
//                        writeToFile(file, lrc);
//                        mPlayerHandler.sendEmptyMessage(LRC_DOWNLOADED);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }


        }

    }


    private void getLrc(long id) {
//        MusicInfo info = mPlaylistInfo.get(id);
//
//        if (info == null) {
//            L.D(D, TAG, "get lrc err ,musicinfo is null");
//        }
//        String lrc = Environment.getExternalStorageDirectory().getAbsolutePath() + LRC_PATH;
//        File file = new File(lrc);
//        L.D(D, TAG, "file exists = " + file.exists());
//        if (!file.exists()) {
//            //不存在就建立此目录
//            boolean r = file.mkdirs();
//            L.D(D, TAG, "file created = " + r);
//
//        }
//        file = new File(lrc + id);
//        if (!file.exists()) {
//            if (mRequestLrc != null) {
//                mRequestLrc.stop();
//                mLrcHandler.removeCallbacks(mRequestLrc);
//            }
//            mRequestLrc = new RequestLrc(mPlaylistInfo.get(id));
//            mLrcHandler.postDelayed(mRequestLrc, 70);
//        }
    }

    private synchronized void writeToFile(File file, String lrc) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(lrc.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startProxy() {
        if (mProxy == null) {
            mProxy = new MediaPlayerProxy(this);
            mProxy.init();
            mProxy.start();
        }
    }

    private void openCurrentAndNextPlay(boolean play) {
        openCurrentAndMaybeNext(play, true);
    }

    private void openCurrentAndNext() {
        openCurrentAndMaybeNext(false, true);
    }


    private void openCurrentAndMaybeNext(final boolean play, final boolean openNext) {
        synchronized (this) {
            if (D) Log.d(TAG, "open current");
            closeCursor();
            stop(false);
            boolean shutdown = false;

            if (mPlaylist.size() == 0 || mPlaylistInfo.size() == 0 && mPlayPos >= mPlaylist.size()) {
                clearPlayInfos();
                return;
            }
            final long id = mPlaylist.get(mPlayPos).mId;
            updateCursor(id);
            getLrc(id);
            if (mPlaylistInfo.get(id) == null) {
                return;
            }
            if (!mPlaylistInfo.get(id).islocal) {
                if (mRequestUrl != null) {
                    mRequestUrl.stop();
                    mUrlHandler.removeCallbacks(mRequestUrl);
                }
                mRequestUrl = new RequestPlayUrl(id, play);
                mUrlHandler.postDelayed(mRequestUrl, 70);

            } else {
                while (true) {
                    if (mCursor != null
                            && openFile(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/"
                            + mCursor.getLong(IDCOLIDX))) {
                        break;
                    }

                    closeCursor();
                    if (mOpenFailedCounter++ < 10 && mPlaylist.size() > 1) {
                        final int pos = getNextPosition(false);
                        if (pos < 0) {
                            shutdown = true;
                            break;
                        }
                        mPlayPos = pos;
                        stop(false);
                        mPlayPos = pos;
                        updateCursor(mPlaylist.get(mPlayPos).mId);
                    } else {
                        mOpenFailedCounter = 0;
                        Log.w(TAG, "Failed to open file for playback");
                        shutdown = true;
                        break;
                    }
                }
            }

            if (shutdown) {
                scheduleDelayedShutdown();
                if (mIsSupposedToBePlaying) {
                    mIsSupposedToBePlaying = false;
                    notifyChange(PLAYSTATE_CHANGED);
                }
            } else if (openNext) {
                setNextTrack();
            }
        }
    }

    private void sendErrorMessage(final String trackName) {
        final Intent i = new Intent(TRACK_ERROR);
        i.putExtra(TrackErrorExtra.TRACK_NAME, trackName);
        sendBroadcast(i);
    }

    private int getNextPosition(final boolean force) {
        if (mPlaylist == null || mPlaylist.isEmpty()) {
            return -1;
        }
        if (!force && mRepeatMode == REPEAT_CURRENT) {
            if (mPlayPos < 0) {
                return 0;
            }
            return mPlayPos;
        } else if (mShuffleMode == SHUFFLE_NORMAL) {
            final int numTracks = mPlaylist.size();


            final int[] trackNumPlays = new int[numTracks];
            for (int i = 0; i < numTracks; i++) {
                trackNumPlays[i] = 0;
            }


            final int numHistory = mHistory.size();
            for (int i = 0; i < numHistory; i++) {
                final int idx = mHistory.get(i).intValue();
                if (idx >= 0 && idx < numTracks) {
                    trackNumPlays[idx]++;
                }
            }

            if (mPlayPos >= 0 && mPlayPos < numTracks) {
                trackNumPlays[mPlayPos]++;
            }

            int minNumPlays = Integer.MAX_VALUE;
            int numTracksWithMinNumPlays = 0;
            for (int i = 0; i < trackNumPlays.length; i++) {
                if (trackNumPlays[i] < minNumPlays) {
                    minNumPlays = trackNumPlays[i];
                    numTracksWithMinNumPlays = 1;
                } else if (trackNumPlays[i] == minNumPlays) {
                    numTracksWithMinNumPlays++;
                }
            }


            if (minNumPlays > 0 && numTracksWithMinNumPlays == numTracks
                    && mRepeatMode != REPEAT_ALL && !force) {
                return -1;
            }


            int skip = mShuffler.nextInt(numTracksWithMinNumPlays);
            for (int i = 0; i < trackNumPlays.length; i++) {
                if (trackNumPlays[i] == minNumPlays) {
                    if (skip == 0) {
                        return i;
                    } else {
                        skip--;
                    }
                }
            }

            if (D)
                Log.e(TAG, "Getting the next position resulted did not get a result when it should have");
            return -1;
        } else if (mShuffleMode == SHUFFLE_AUTO) {
            doAutoShuffleUpdate();
            return mPlayPos + 1;
        } else {
            if (mPlayPos >= mPlaylist.size() - 1) {
                if (mRepeatMode == REPEAT_NONE && !force) {
                    return -1;
                } else if (mRepeatMode == REPEAT_ALL || force) {
                    return 0;
                }
                return -1;
            } else {
                return mPlayPos + 1;
            }
        }
    }

    private void setNextTrack() {
        setNextTrack(getNextPosition(false));
    }

    private void setNextTrack(int position) {
        mNextPlayPos = position;
        if (D) Log.d(TAG, "setNextTrack: next play position = " + mNextPlayPos);
        if (mNextPlayPos >= 0 && mPlaylist != null && mNextPlayPos < mPlaylist.size()) {
            final long id = mPlaylist.get(mNextPlayPos).mId;
            if (mPlaylistInfo.get(id) != null) {
                if (mPlaylistInfo.get(id).islocal) {
                    mPlayer.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id);
                } else {
                    mPlayer.setNextDataSource(null);
                }

            }
        } else {
            mPlayer.setNextDataSource(null);
        }
    }

    private boolean makeAutoShuffleList() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Media._ID
                    }, MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);
            if (cursor == null || cursor.getCount() == 0) {
                return false;
            }
            final int len = cursor.getCount();
            final long[] list = new long[len];
            for (int i = 0; i < len; i++) {
                cursor.moveToNext();
                list[i] = cursor.getLong(0);
            }
            mAutoShuffleList = list;
            return true;
        } catch (final RuntimeException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return false;
    }

    private void doAutoShuffleUpdate() {
        boolean notify = false;
        if (mPlayPos > 10) {
            removeTracks(0, mPlayPos - 9);
            notify = true;
        }
        final int toAdd = 7 - (mPlaylist.size() - (mPlayPos < 0 ? -1 : mPlayPos));
        for (int i = 0; i < toAdd; i++) {
            int lookback = mHistory.size();
            int idx = -1;
            while (true) {
                idx = mShuffler.nextInt(mAutoShuffleList.length);
                if (!wasRecentlyUsed(idx, lookback)) {
                    break;
                }
                lookback /= 2;
            }
            mHistory.add(idx);
            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.remove(0);
            }
            mPlaylist.add(new MusicTrack(mAutoShuffleList[idx], -1));
            notify = true;
        }
        if (notify) {
            notifyChange(QUEUE_CHANGED);
        }
    }

    private boolean wasRecentlyUsed(final int idx, int lookbacksize) {
        if (lookbacksize == 0) {
            return false;
        }
        final int histsize = mHistory.size();
        if (histsize < lookbacksize) {
            lookbacksize = histsize;
        }
        final int maxidx = histsize - 1;
        for (int i = 0; i < lookbacksize; i++) {
            final long entry = mHistory.get(maxidx - i);
            if (entry == idx) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送缓冲进度的广播
     *
     * @param progress
     */
    private void sendUpdateBuffer(int progress) {
        Intent intent = new Intent(BUFFER_UP);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }

    /**
     * 发送状态改变的广播
     *
     * @param what
     */
    private void notifyChange(final String what) {
        if (D) Log.d(TAG, "notifyChange: what = " + what);
        //更新播放进度
        if (SEND_PROGRESS.equals(what)) {
            final Intent intent = new Intent(what);
            intent.putExtra("position", position());
            intent.putExtra("duration", duration());
            //发送广播
            sendStickyBroadcast(intent);
            return;
        }

        // 更新锁屏播放控制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            updateMediaSession(what);

        if (what.equals(POSITION_CHANGED)) {
            return;
        }

        final Intent intent = new Intent(what);
        intent.putExtra("id", getAudioId());
        intent.putExtra("artist", getArtistName());
        intent.putExtra("album", getAlbumName());
        intent.putExtra("track", getTrackName());
        intent.putExtra("playing", isPlaying());
        intent.putExtra("albumuri", getAlbumPath());
        intent.putExtra("islocal", isTrackLocal());

        sendStickyBroadcast(intent);
        final Intent musicIntent = new Intent(intent);
        musicIntent.setAction(what.replace(TIMBER_PACKAGE_NAME, MUSIC_PACKAGE_NAME));
        sendStickyBroadcast(musicIntent);
//        if (what.equals(TRACK_PREPARED)) {
//            return;
//        }

        if (what.equals(META_CHANGED)) {

            mRecentStore.addSongId(getAudioId());
            mSongPlayCount.bumpSongCount(getAudioId());

        } else if (what.equals(QUEUE_CHANGED)) {
            Intent intent1 = new Intent("com.example.yungui.music.emptyplaylist");
            intent.putExtra("showorhide", "show");
            sendBroadcast(intent1);
            saveQueue(true);
            if (isPlaying()) {

                if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size()
                        && getShuffleMode() != SHUFFLE_NONE) {
                    setNextTrack(mNextPlayPos);
                } else {
                    setNextTrack();
                }
            }
        } else {
            saveQueue(false);
        }

        if (what.equals(PLAYSTATE_CHANGED)) {
            updateNotification();
        }

    }

    /**
     * 更新媒体会话
     *
     * @param what
     */
    private void updateMediaSession(final String what) {
        int playState = mIsSupposedToBePlaying
                ? PlaybackState.STATE_PLAYING
                : PlaybackState.STATE_PAUSED;
        //播放状态的变化
        if (what.equals(PLAYSTATE_CHANGED) || what.equals(POSITION_CHANGED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSession.setPlaybackState(new PlaybackState.Builder()
                        .setState(playState, position(), 1.0f)
                        .setActions(PlaybackState.ACTION_PLAY
                                | PlaybackState.ACTION_PAUSE
                                | PlaybackState.ACTION_PLAY_PAUSE
                                | PlaybackState.ACTION_SKIP_TO_NEXT
                                | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                        .build());
            }
            //数据的变化
        } else if (what.equals(META_CHANGED) || what.equals(QUEUE_CHANGED)) {
            //Bitmap albumArt = ImageLoader.getInstance().loadImageSync(CommonUtils.getAlbumArtUri(getAlbumId()).toString());
            Bitmap albumArt = null;
            if (albumArt != null) {

                Bitmap.Config config = albumArt.getConfig();
                if (config == null) {
                    config = Bitmap.Config.ARGB_8888;
                }
                albumArt = albumArt.copy(config, false);
            }

            //本版大于5.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //设置元数据
                mSession.setMetadata(new MediaMetadata.Builder()
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, getArtistName())
                        .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, getAlbumArtistName())
                        .putString(MediaMetadata.METADATA_KEY_ALBUM, getAlbumName())
                        .putString(MediaMetadata.METADATA_KEY_TITLE, getTrackName())
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, duration())
                        .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, getQueuePosition() + 1)
                        .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, getQueue().length)
                        .putString(MediaMetadata.METADATA_KEY_GENRE, getGenreName())
                        .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART,
                                mShowAlbumArtOnLockscreen ? albumArt : null)
                        .build());
                //设置播放状态
                mSession.setPlaybackState(new PlaybackState.Builder()
                        .setState(playState, position(), 1.0f)
                        .setActions(PlaybackState.ACTION_PLAY
                                | PlaybackState.ACTION_PAUSE
                                | PlaybackState.ACTION_PLAY_PAUSE
                                | PlaybackState.ACTION_SKIP_TO_NEXT
                                | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                        .build());
            }
        }
    }

    //通知
    private Notification getNotification() {
        RemoteViews remoteViews;
        final int PAUSE_FLAG = 0x1;
        final int NEXT_FLAG = 0x2;
        final int STOP_FLAG = 0x3;
        final String albumName = getAlbumName();
        final String artistName = getArtistName();
        final boolean isPlaying = isPlaying();

        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
        String text = TextUtils.isEmpty(albumName) ? artistName : artistName + " - " + albumName;
        remoteViews.setTextViewText(R.id.title, getTrackName());
        remoteViews.setTextViewText(R.id.text, text);

        //此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        Intent pauseIntent = new Intent(TOGGLEPAUSE_ACTION);
        pauseIntent.putExtra("FLAG", PAUSE_FLAG);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        remoteViews.setImageViewResource(R.id.bottom_control, isPlaying ? R.drawable.bottom_control_pasue : R.drawable.bottom_control_play);
        remoteViews.setOnClickPendingIntent(R.id.iv_pauseOrPlay, pausePIntent);

        Intent nextIntent = new Intent(NEXT_ACTION);
        nextIntent.putExtra("FLAG", NEXT_FLAG);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPIntent);

        Intent preIntent = new Intent(PREVIOUS_ACTION);
        preIntent.putExtra("FLAG", STOP_FLAG);
        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_previous, prePIntent);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0,
//                new Intent(this.getApplicationContext(), PlayingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        final Intent nowPlayingIntent = new Intent();
        //nowPlayingIntent.setAction("com.example.yungui.music.LAUNCH_NOW_PLAYING_ACTION");
        nowPlayingIntent.setComponent(new ComponentName("com.example.yungui.music", "com.example.yungui.music.activity.PlayingActivity"));
        nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent click = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Bitmap bitmap = ImageUtils.getArtworkQuick(this, getAlbumId(), 160, 160);
        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.image, bitmap);
            // remoteViews.setImageViewUri(R.id.image, MusicUtils.getAlbumUri(this, getAudioId()));
            mNoBit = null;

//        } else if (!isTrackLocal()) {
//            if (mNoBit != null) {
//                remoteViews.setImageViewBitmap(R.id.image, mNoBit);
//                mNoBit = null;
//
//            } else {
//                Uri uri = null;
//                if (getAlbumPath() != null) {
//                    try {
//                        uri = Uri.parse(getAlbumPath());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (getAlbumPath() == null || uri == null) {
//                    mNoBit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
//                    updateNotification();
//                } else {
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(uri)
//                            .setProgressiveRenderingEnabled(true)
//                            .build();
//                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                    DataSource<CloseableReference<CloseableImage>>
//                            dataSource = imagePipeline.fetchDecodedImage(imageRequest, MediaService.this);
//
//                    dataSource.subscribe(new BaseBitmapDataSubscriber() {
//
//                                             @Override
//                                             public void onNewResultImpl(@Nullable Bitmap bitmap) {
//                                                 // You can use the bitmap in only limited ways
//                                                 // No need to do any cleanup.
//                                                 if (bitmap != null) {
//                                                     mNoBit = bitmap;
//                                                 }
//                                                 updateNotification();
//                                             }
//
//                                             @Override
//                                             public void onFailureImpl(DataSource dataSource) {
//                                                 // No cleanup required here.
//                                                 mNoBit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
//                                                 updateNotification();
//                                             }
//                                         },
//                            CallerThreadExecutor.getInstance());
//                }
//            }
//
        } else {
            remoteViews.setImageViewResource(R.id.image, R.drawable.ic_launcher_background);
        }


        if (mNotificationPostTime == 0) {
            mNotificationPostTime = System.currentTimeMillis();
        }
        if (mNotification == null) {
            NotificationCompat.Builder builder = new NotificationCompat
                    .Builder(this)
                    .setContent(remoteViews)
                    .setSmallIcon(R.drawable.audiotrack)
                    .setContentIntent(click)
                    .setWhen(mNotificationPostTime);
            if (CommonUtils.isJellyBeanMR1()) {
                builder.setShowWhen(false);
            }
            mNotification = builder.build();
        } else {
            mNotification.contentView = remoteViews;
        }
        return mNotification;
    }


    private final PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(this, MediaService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, 0, intent, 0);
    }

    /**
     * 存储信息。包括播放列表，内存卡id，播放的歌曲位置，播放位置，播放模式，循环模式
     * 播放列表的信息以json的格存储在缓存文件中，其他文件存放在sharedPreference中
     * @param full
     */
    private void saveQueue(final boolean full) {
        //列队无法存储
        if (!mQueueIsSaveable) {
            return;
        }

        final SharedPreferences.Editor editor = mPreferences.edit();
        if (full) {
            mPlaybackStateStore.saveState(mPlaylist, mShuffleMode != SHUFFLE_NONE ? mHistory : null);
            if (mPlaylistInfo.size() > 0) {
                String temp = MainApplication.getGsonInstance().toJson(mPlaylistInfo);
                try {
                    File file = new File(getCacheDir().getAbsolutePath() + "playlist");
                    RandomAccessFile ra = new RandomAccessFile(file, "rws");
                    ra.write(temp.getBytes());
                    ra.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            editor.putInt("cardid", mCardId);

        }
        editor.putInt("curpos", mPlayPos);
        if (mPlayer.isInitialized()) {
            editor.putLong("seekpos", mPlayer.position());
        }
        editor.putInt("repeatmode", mRepeatMode);
        editor.putInt("shufflemode", mShuffleMode);
        editor.apply();
    }

    /**
     * 获取权限之后加载列队
     */
    private void reloadQueueAfterPermissionCheck() {
        //6.0的运行时权限
        if (CommonUtils.isMarshmallow()) {
            if (PermissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                reloadQueue();
            }
        } else {
            reloadQueue();
        }
    }

    /**
     * 从SD卡中读取内容
     *
     * @param is
     * @return
     * @throws Exception
     */
    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer();
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * 清除播放信息
     */
    private void clearPlayInfos() {
        File file = new File(getCacheDir().getAbsolutePath() + "playlist");
        if (file.exists()) {
            file.delete();
        }
        MusicPlaybackState.getInstance(this).clearQueue();
    }

    /**
     * ===============加载播放列表=======================
     */
    private void reloadQueue() {
        int id = mCardId;
        //获取SD卡ID
        if (mPreferences.contains("cardid")) {
            id = mPreferences.getInt("cardid", ~mCardId);
        }
        //内存卡没有改变
        if (id == mCardId) {
            //从回放状态数据库中获取播放列表
            mPlaylist = mPlaybackStateStore.getQueue();
            try {
                //读取缓存中的播放信息
                FileInputStream in = new FileInputStream(new File(getCacheDir().getAbsolutePath() + "playlist"));
                String c = readTextFromSDcard(in);
                //从字符串中解析信息
                HashMap<Long, MusicInfo> play = MainApplication.getGsonInstance()
                        .fromJson(c, new TypeToken<HashMap<Long, MusicInfo>>() {
                        }.getType());

                if (play != null && play.size() > 0) {
                    mPlaylistInfo = play;
                    L.D(D, TAG, mPlaylistInfo.keySet().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if ((mPlaylist.size() == mPlaylistInfo.size()) && mPlaylist.size() > 0) {
            final int pos = mPreferences.getInt("curpos", 0);
            if (pos < 0 || pos >= mPlaylist.size()) {
                mPlaylist.clear();
                return;
            }
            mPlayPos = pos;
            updateCursor(mPlaylist.get(mPlayPos).mId);
            if (mCursor == null) {
                SystemClock.sleep(3000);
                updateCursor(mPlaylist.get(mPlayPos).mId);
            }
            synchronized (this) {
                closeCursor();
                mOpenFailedCounter = 20;
                openCurrentAndNext();
            }

//            if (!mPlayer.isInitialized() && isTrackLocal()) {
//                mPlaylist.clear();
//                return;
//            }
            final long seekpos = mPreferences.getLong("seekpos", 0);
            mLastSeekPos = seekpos;
            seek(seekpos >= 0 && seekpos < duration() ? seekpos : 0);

            if (D) {
                Log.d(TAG, "restored queue, currently at position "
                        + position() + "/" + duration()
                        + " (requested " + seekpos + ")");
            }

            int repmode = mPreferences.getInt("repeatmode", REPEAT_ALL);
            if (repmode != REPEAT_ALL && repmode != REPEAT_CURRENT) {
                repmode = REPEAT_NONE;
            }
            mRepeatMode = repmode;

            int shufmode = mPreferences.getInt("shufflemode", SHUFFLE_NONE);
            if (shufmode != SHUFFLE_AUTO && shufmode != SHUFFLE_NORMAL) {
                shufmode = SHUFFLE_NONE;
            }
            if (shufmode != SHUFFLE_NONE) {
                mHistory = mPlaybackStateStore.getHistory(mPlaylist.size());
            }
            if (shufmode == SHUFFLE_AUTO) {
                if (!makeAutoShuffleList()) {
                    shufmode = SHUFFLE_NONE;
                }
            }
            mShuffleMode = shufmode;
        } else {
            clearPlayInfos();
        }
        notifyChange(MUSIC_CHANGED);
    }

    /**
     * 打开指定路径的文件
     *
     * @param path
     * @return
     */
    public boolean openFile(final String path) {
        if (D) Log.d(TAG, "openFile: path = " + path);
        synchronized (this) {
            if (path == null) {
                return false;
            }

            if (mCursor == null) {
                Uri uri = Uri.parse(path);
                boolean shouldAddToPlaylist = true;
                long id = -1;
                try {
                    id = Long.valueOf(uri.getLastPathSegment());
                } catch (NumberFormatException ex) {
                    // Ignore
                }

                if (id != -1 && path.startsWith(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString())) {
                    updateCursor(uri);

                } else if (id != -1 && path.startsWith(
                        MediaStore.Files.getContentUri("external").toString())) {
                    updateCursor(id);

                } else if (path.startsWith("content://downloads/")) {

                    String mpUri = getValueForDownloadedFile(this, uri, "mediaprovider_uri");
                    if (D) Log.i(TAG, "Downloaded file's MP uri : " + mpUri);
                    if (!TextUtils.isEmpty(mpUri)) {
                        if (openFile(mpUri)) {
                            notifyChange(META_CHANGED);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        updateCursorForDownloadedFile(this, uri);
                        shouldAddToPlaylist = false;
                    }

                } else {
                    String where = MediaStore.Audio.Media.DATA + "=?";
                    String[] selectionArgs = new String[]{path};
                    updateCursor(where, selectionArgs);
                }
                try {
                    if (mCursor != null && shouldAddToPlaylist) {
                        mPlaylist.clear();
                        mPlaylist.add(new MusicTrack(
                                mCursor.getLong(IDCOLIDX), -1));
                        notifyChange(QUEUE_CHANGED);
                        mPlayPos = 0;
                        mHistory.clear();
                    }
                } catch (final UnsupportedOperationException ex) {
                    // Ignore
                }
            }

            mFileToPlay = path;
            mPlayer.setDataSource(mFileToPlay);
            if (mPlayer.isInitialized()) {
                mOpenFailedCounter = 0;
                return true;
            }

            String trackName = getTrackName();
            if (TextUtils.isEmpty(trackName)) {
                trackName = path;
            }
            sendErrorMessage(trackName);

            stop(true);
            return false;
        }
    }

    private void updateCursorForDownloadedFile(Context context, Uri uri) {
        synchronized (this) {
            closeCursor();
            MatrixCursor cursor = new MatrixCursor(PROJECTION_MATRIX);
            String title = getValueForDownloadedFile(this, uri, "title");
            cursor.addRow(new Object[]{
                    null,
                    null,
                    null,
                    title,
                    null,
                    null,
                    null,
                    null
            });
            mCursor = cursor;
            mCursor.moveToFirst();
        }
    }

    /**
     * 从下载目录中获取值
     *
     * @param context
     * @param uri     指定类型的资源
     * @param column  projection映射字符串
     * @return
     */
    private String getValueForDownloadedFile(Context context, Uri uri, String column) {

        Cursor cursor = null;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public int getAudioSessionId() {
        synchronized (this) {
            return mPlayer.getAudioSessionId();
        }
    }

    public int getMediaMountedCount() {
        return mMediaMountedCount;
    }

    public int getShuffleMode() {
        return mShuffleMode;
    }

    public void setShuffleMode(final int shufflemode) {
        synchronized (this) {
            if (mShuffleMode == shufflemode && mPlaylist.size() > 0) {
                return;
            }

            mShuffleMode = shufflemode;
            if (mShuffleMode == SHUFFLE_AUTO) {
                if (makeAutoShuffleList()) {
                    mPlaylist.clear();
                    doAutoShuffleUpdate();
                    mPlayPos = 0;
                    openCurrentAndNext();
                    play();
                    notifyChange(META_CHANGED);
                    return;
                } else {
                    mShuffleMode = SHUFFLE_NONE;
                }
            } else {
                setNextTrack();
            }
            saveQueue(false);
            notifyChange(SHUFFLEMODE_CHANGED);
        }
    }

    public int getRepeatMode() {
        return mRepeatMode;
    }

    public void setRepeatMode(final int repeatmode) {
        synchronized (this) {
            mRepeatMode = repeatmode;
            setNextTrack();
            saveQueue(false);
            notifyChange(REPEATMODE_CHANGED);
        }
    }

    /**
     *移除追踪
     * @param id 指定id
     * @return
     */
    public int removeTrack(final long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlaylist.size(); i++) {
                if (mPlaylist.get(i).mId == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }
            mPlaylistInfo.remove(id);
        }

        if (numremoved > 0) {
            //发送广播列队发生改变
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    /**
     * 移除指定id和位置的追踪
     * @param id
     * @param position
     * @return
     */
    public boolean removeTrackAtPosition(final long id, final int position) {
        synchronized (this) {
            if (position >= 0 &&
                    position < mPlaylist.size() &&
                    mPlaylist.get(position).mId == id) {
                mPlaylistInfo.remove(id);
                return removeTracks(position, position) > 0;
            }

        }
        return false;
    }

    public int removeTracks(final int first, final int last) {
        final int numremoved = removeTracksInternal(first, last);
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    public int getQueuePosition() {
        synchronized (this) {
            return mPlayPos;
        }
    }

    public void setQueuePosition(final int index) {
        synchronized (this) {
            stop(false);
            mPlayPos = index;
            openCurrentAndNext();
            play();
            notifyChange(META_CHANGED);
            if (mShuffleMode == SHUFFLE_AUTO) {
                doAutoShuffleUpdate();
            }
        }
    }

    public int getQueueHistorySize() {
        synchronized (this) {
            return mHistory.size();
        }
    }

    public int getQueueHistoryPosition(int position) {
        synchronized (this) {
            if (position >= 0 && position < mHistory.size()) {
                return mHistory.get(position);
            }
        }

        return -1;
    }

    public int[] getQueueHistoryList() {
        synchronized (this) {
            int[] history = new int[mHistory.size()];
            for (int i = 0; i < mHistory.size(); i++) {
                history[i] = mHistory.get(i);
            }

            return history;
        }
    }

    public String getPath() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.DATA));
        }
    }

    public String getAlbumName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ALBUM));
        }
    }

    public String getAlbumPath() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.MIME_TYPE));
        }
    }

    public String[] getAlbumPathAll() {
        synchronized (this) {
            try {
                int len = mPlaylistInfo.size();
                String[] albums = new String[len];
                long[] queue = getQueue();
                for (int i = 0; i < len; i++) {
                    albums[i] = mPlaylistInfo.get(queue[i]).albumData;
                }
                return albums;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String[]{};
        }
    }

    public String getTrackName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.TITLE));
        }
    }

    public boolean isTrackLocal() {
        synchronized (this) {
            MusicInfo info = mPlaylistInfo.get(getAudioId());
            if (info == null) {
                return true;
            }
            return info.islocal;
        }
    }

    public String getAlbumPath(long id) {
        synchronized (this) {
            try {
                String str = mPlaylistInfo.get(id).albumData;
                return str;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String getGenreName() {
        synchronized (this) {
            if (mCursor == null || mPlayPos < 0 || mPlayPos >= mPlaylist.size()) {
                return null;
            }
            String[] genreProjection = {MediaStore.Audio.Genres.NAME};
            Uri genreUri = MediaStore.Audio.Genres.getContentUriForAudioId("external",
                    (int) mPlaylist.get(mPlayPos).mId);
            Cursor genreCursor = getContentResolver().query(genreUri, genreProjection,
                    null, null, null);
            if (genreCursor != null) {
                try {
                    if (genreCursor.moveToFirst()) {
                        return genreCursor.getString(
                                genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));
                    }
                } finally {
                    genreCursor.close();
                }
            }
            return null;
        }
    }

    public String getArtistName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ARTIST));
        }
    }

    public String getAlbumArtistName() {
        synchronized (this) {
            if (mAlbumCursor == null) {
                return null;
            }
            return mAlbumCursor.getString(mAlbumCursor.getColumnIndexOrThrow(AlbumColumns.ARTIST));
        }
    }

    public long getAlbumId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID));
        }
    }

    public long getArtistId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID));
        }
    }

    public long getAudioId() {
        MusicTrack track = getCurrentTrack();
        if (track != null) {
            return track.mId;
        }

        return -1;
    }

    public MusicTrack getCurrentTrack() {
        return getTrack(mPlayPos);
    }

    public synchronized MusicTrack getTrack(int index) {
        if (index >= 0 && index < mPlaylist.size()) {
            return mPlaylist.get(index);
        }

        return null;
    }

    public long getNextAudioId() {
        synchronized (this) {
            if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size() && mPlayer.isInitialized()) {
                return mPlaylist.get(mNextPlayPos).mId;
            }
        }
        return -1;
    }

    public long getPreviousAudioId() {
        synchronized (this) {
            if (mPlayer.isInitialized()) {
                int pos = getPreviousPlayPosition(false);
                if (pos >= 0 && pos < mPlaylist.size()) {
                    return mPlaylist.get(pos).mId;
                }
            }
        }
        return -1;
    }

    public long seek(long position) {
        if (mPlayer.isInitialized()) {
            if (position < 0) {
                position = 0;
            } else if (position > mPlayer.duration()) {
                position = mPlayer.duration();
            }
            long result = mPlayer.seek(position);
            notifyChange(POSITION_CHANGED);
            return result;
        }
        return -1;
    }

    public void seekRelative(long deltaInMs) {
        synchronized (this) {
            if (mPlayer.isInitialized()) {
                final long newPos = position() + deltaInMs;
                final long duration = duration();
                if (newPos < 0) {
                    prev(true);
                    // seek to the new duration + the leftover position
                    seek(duration() + newPos);
                } else if (newPos >= duration) {
                    gotoNext(true);
                    // seek to the leftover duration
                    seek(newPos - duration);
                } else {
                    seek(newPos);
                }
            }
        }
    }

    /**
     * 获取播放位置
     *
     * @return
     */
    public long position() {
        if (mPlayer.isInitialized() && mPlayer.isTrackPrepared()) {
            try {
                return mPlayer.position();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public int getSecondPosition() {
        if (mPlayer.isInitialized()) {
            return mPlayer.sencondaryPosition;
        }
        return -1;
    }


    public long duration() {
        if (mPlayer.isInitialized() && mPlayer.isTrackPrepared()) {
            return mPlayer.duration();
        }
        return -1;
    }

    public HashMap<Long, MusicInfo> getPlayinfos() {
        synchronized (this) {
            return mPlaylistInfo;
        }
    }

    /**
     * 获取播放队列
     * @return
     */
    public long[] getQueue() {
        synchronized (this) {
            //播放列表的长度
            final int len = mPlaylist.size();
            //创建对应长度的数组
            final long[] list = new long[len];
            for (int i = 0; i < len; i++) {
                //获取id
                list[i] = mPlaylist.get(i).mId;
            }
            return list;
        }
    }

    public long getQueueItemAtPosition(int position) {
        synchronized (this) {
            if (position >= 0 && position < mPlaylist.size()) {
                return mPlaylist.get(position).mId;
            }
        }

        return -1;
    }

    public int getQueueSize() {
        synchronized (this) {
            return mPlaylist.size();
        }
    }

    public boolean isPlaying() {
        return mIsSupposedToBePlaying;
    }

    /**
     * 是否希望随时准备播放
     *
     * @param value
     * @param notify 是否通知播放状态改变
     */
    private void setIsSupposedToBePlaying(boolean value, boolean notify) {
        if (mIsSupposedToBePlaying != value) {
            //更新播放意愿
            mIsSupposedToBePlaying = value;
            ///不希望播放准备
            if (!mIsSupposedToBePlaying) {
                //延时关闭
                scheduleDelayedShutdown();
                //记录最后播放的时间
                mLastPlayedTime = System.currentTimeMillis();
            }
            if (notify) {
                notifyChange(PLAYSTATE_CHANGED);
            }
        }
    }

    private boolean recentlyPlayed() {
        return isPlaying() || System.currentTimeMillis() - mLastPlayedTime < IDLE_DELAY;
    }

    /**
     * @param infos    音乐信息
     * @param list     歌曲id数组
     * @param position 点击的位置
     */
    public void open(final HashMap<Long, MusicInfo> infos, final long[] list, final int position) {
        synchronized (this) {

            mPlaylistInfo = infos;
            L.D(D, TAG, mPlaylistInfo.toString());
            if (mShuffleMode == SHUFFLE_AUTO) {
                mShuffleMode = SHUFFLE_NORMAL;
            }
            final long oldId = getAudioId();
            final int listLength = list.length;
            boolean newList = true;
            if (mPlaylist.size() == listLength) {
                newList = false;
                for (int i = 0; i < listLength; i++) {
                    if (list[i] != mPlaylist.get(i).mId) {
                        newList = true;
                        break;
                    }
                }
            }
            if (newList) {
                addToPlayList(list, -1);
                //发送广播
                notifyChange(QUEUE_CHANGED);
            }
            if (position >= 0) {
                mPlayPos = position;
            } else {
                mPlayPos = mShuffler.nextInt(mPlaylist.size());
            }


            mHistory.clear();
            openCurrentAndNextPlay(true);
            if (oldId != getAudioId()) {
                notifyChange(META_CHANGED);
            }
        }
    }

    public void stop() {
        stop(true);
    }

    public void play() {
        play(true);
    }

    public void play(boolean createNewNextTrack) {
        int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (D) Log.d(TAG, "Starting playback: audio focus request status = " + status);

        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(intent);

        mAudioManager.registerMediaButtonEventReceiver(new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSession.setActive(true);
        if (createNewNextTrack) {
            setNextTrack();
        } else {
            setNextTrack(mNextPlayPos);
        }
        if (mPlayer.isTrackPrepared()) {
            final long duration = mPlayer.duration();
            if (mRepeatMode != REPEAT_CURRENT && duration > 2000
                    && mPlayer.position() >= duration - 2000) {
                gotoNext(true);
            }
        }
        mPlayer.start();
        mPlayerHandler.removeMessages(FADEDOWN);
        mPlayerHandler.sendEmptyMessage(FADEUP);
        setIsSupposedToBePlaying(true, true);
        cancelShutdown();
        updateNotification();
        notifyChange(META_CHANGED);
    }

    public void pause() {
        if (D) Log.d(TAG, "Pausing playback");
        synchronized (this) {
            mPlayerHandler.removeMessages(FADEUP);
            if (mIsSupposedToBePlaying) {
                final Intent intent = new Intent(
                        AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
                sendBroadcast(intent);
                mPlayer.pause();
                setIsSupposedToBePlaying(false, true);
                notifyChange(META_CHANGED);
            }
        }
    }

    public void gotoNext(final boolean force) {
        if (D) Log.d(TAG, "Going to next track");
        synchronized (this) {
            if (mPlaylist.size() <= 0) {
                if (D) Log.d(TAG, "No play queue");
                scheduleDelayedShutdown();
                return;
            }

            int pos = mNextPlayPos;
            if (pos < 0) {
                pos = getNextPosition(force);
            }

            if (pos < 0) {
                setIsSupposedToBePlaying(false, true);
                return;
            }

            stop(false);
            setAndRecordPlayPos(pos);
            openCurrentAndNext();
            play();
            notifyChange(META_CHANGED);
            notifyChange(MUSIC_CHANGED);
        }
    }

    /**
     * 记录播放的位置，并开始播放下一个
     *
     * @param nextPos
     */
    public void setAndRecordPlayPos(int nextPos) {
        synchronized (this) {
            //判断随机模式
            if (mShuffleMode != SHUFFLE_NONE) {
                mHistory.add(mPlayPos);
                //记录已满移除第一个
                if (mHistory.size() > MAX_HISTORY_SIZE) {
                    mHistory.remove(0);
                }
            }
            //更行当前的播放位置
            mPlayPos = nextPos;
        }
    }

    /**
     * 播放前一首
     *
     * @param forcePrevious
     */
    public void prev(boolean forcePrevious) {
        synchronized (this) {
            //判断是否可以播放前一首，不是单曲循环，
            boolean goPrevious = getRepeatMode() != REPEAT_CURRENT &&
                    (position() < REWIND_INSTEAD_PREVIOUS_THRESHOLD || forcePrevious);
            //可以播放下=前一首
            if (goPrevious) {
                if (D) Log.d(TAG, "Going to previous track");
                //获取前一首的 位置
                int pos = getPreviousPlayPosition(true);
                if (pos < 0) {
                    return;
                }

                //更新播放位置
                mNextPlayPos = mPlayPos;
                mPlayPos = pos;

                stop(false);
                openCurrent();
                play(false);
                notifyChange(META_CHANGED);
                notifyChange(MUSIC_CHANGED);
            } else {
                if (D) Log.d(TAG, "Going to beginning of track");
                seek(0);
                play(false);
            }
        }
    }

    /***
     *获取前一首的位置
     * @param removeFromHistory
     * @return
     */
    public int getPreviousPlayPosition(boolean removeFromHistory) {
        synchronized (this) {
            //判断循环模式，如果是随机模式则没有上一区，则从历史中获取
            if (mShuffleMode == SHUFFLE_NORMAL) {
                final int histsize = mHistory.size();
                if (histsize == 0) {
                    return -1;
                }
                final Integer pos = mHistory.get(histsize - 1);
                if (removeFromHistory) {
                    mHistory.remove(histsize - 1);
                }
                return pos.intValue();
            } else {
                if (mPlayPos > 0) {
                    return mPlayPos - 1;
                } else {
                    return mPlaylist.size() - 1;
                }
            }
        }
    }

    private void openCurrent() {
        openCurrentAndMaybeNext(false, false);
    }

    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
            if (index1 >= mPlaylist.size()) {
                index1 = mPlaylist.size() - 1;
            }
            if (index2 >= mPlaylist.size()) {
                index2 = mPlaylist.size() - 1;
            }

            if (index1 == index2) {
                return;
            }
            mPlaylistInfo.remove(mPlaylist.get(index1).mId);
            final MusicTrack track = mPlaylist.remove(index1);
            if (index1 < index2) {
                mPlaylist.add(index2, track);
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index1 && mPlayPos <= index2) {
                    mPlayPos--;
                }
            } else if (index2 < index1) {
                mPlaylist.add(index2, track);
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index2 && mPlayPos <= index1) {
                    mPlayPos++;
                }
            }
            notifyChange(QUEUE_CHANGED);
        }
    }

    public void enqueue(final long[] list, final HashMap<Long, MusicInfo> map, final int action) {
        synchronized (this) {
            mPlaylistInfo.putAll(map);
            if (action == NEXT && mPlayPos + 1 < mPlaylist.size()) {
                addToPlayList(list, mPlayPos + 1);
                mNextPlayPos = mPlayPos + 1;
                notifyChange(QUEUE_CHANGED);
            } else {
                addToPlayList(list, Integer.MAX_VALUE);
                notifyChange(QUEUE_CHANGED);
            }

            if (mPlayPos < 0) {
                mPlayPos = 0;
                openCurrentAndNext();
                play();
                notifyChange(META_CHANGED);
            }
        }
    }

    private void cycleRepeat() {
        if (mRepeatMode == REPEAT_NONE) {
            setRepeatMode(REPEAT_CURRENT);
            if (mShuffleMode != SHUFFLE_NONE) {
                setShuffleMode(SHUFFLE_NONE);
            }
        } else {
            setRepeatMode(REPEAT_NONE);
        }
    }

    private void cycleShuffle() {
        if (mShuffleMode == SHUFFLE_NONE) {
            setShuffleMode(SHUFFLE_NORMAL);
            if (mRepeatMode == REPEAT_CURRENT) {
                setRepeatMode(REPEAT_ALL);
            }
        } else if (mShuffleMode == SHUFFLE_NORMAL || mShuffleMode == SHUFFLE_AUTO) {
            setShuffleMode(SHUFFLE_NONE);
        }
    }

    /**
     * 刷新
     */
    public void refresh() {
        //调用方法，发送刷新广播
        notifyChange(REFRESH);
    }

    /**
     * 播放列表发生发改变
     */
    public void playlistChanged() {
        notifyChange(PLAYLIST_CHANGED);
    }

    /**
     * 正在加载资源
     *
     * @param l
     */
    public void loading(boolean l) {
        Intent intent = new Intent(MUSIC_LODING);
        intent.putExtra("isloading", l);
        sendBroadcast(intent);
    }

    /**
     * 是否可以设置锁屏
     *
     * @param enabled
     */
    public void setLockscreenAlbumArt(boolean enabled) {
        mShowAlbumArtOnLockscreen = enabled;
        //发送广播
        notifyChange(META_CHANGED);
    }

    /**
     * 倒计时，<h>time</h>时间之后关闭应用
     *
     * @param time
     */
    public void timing(int time) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0,
                new Intent(PAUSE_ACTION),
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent);

    }

    public interface TrackErrorExtra {
        String TRACK_NAME = "trackname";
    }

    /**
     * 处理MusicPlayer的发送过来的各种信息对应的事件
     */
    private static final class MusicPlayerHandler extends Handler {
        private final WeakReference<MediaService> mService;
        private float mCurrentVolume = 1.0f;

        //构造方法
        public MusicPlayerHandler(final MediaService service, final Looper looper) {
            super(looper);
            mService = new WeakReference<MediaService>(service);
        }

        @Override
        public void handleMessage(final Message msg) {
            final MediaService service = mService.get();
            if (service == null) {
                return;
            }
            //锁定service防止对service的并发操作
            synchronized (service) {
                switch (msg.what) {
                    //减小音量
                    case FADEDOWN:
                        mCurrentVolume -= .05f;
                        if (mCurrentVolume > .2f) {
                            sendEmptyMessageDelayed(FADEDOWN, 10);
                        } else {
                            mCurrentVolume = .2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;

                    //增加音量
                    case FADEUP:
                        mCurrentVolume += .01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(FADEUP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    //服务死亡
                    case SERVER_DIED:
                        if (service.isPlaying()) {
                            final TrackErrorInfo info = (TrackErrorInfo) msg.obj;
                            service.sendErrorMessage(info.mTrackName);


                            service.removeTrack(info.mId);
                        } else {
                            service.openCurrentAndNext();
                        }
                        break;
                    //追踪下一个
                    case TRACK_WENT_TO_NEXT:
                        service.setAndRecordPlayPos(service.mNextPlayPos);
                        service.setNextTrack();
                        if (service.mCursor != null) {
                            service.mCursor.close();
                            service.mCursor = null;
                        }

                        service.updateCursor(service.mPlaylist.get(service.mPlayPos).mId);
                        service.notifyChange(META_CHANGED);
                        service.notifyChange(MUSIC_CHANGED);
                        service.updateNotification();
                        break;

                    case TRACK_ENDED:
                        if (service.mRepeatMode == REPEAT_CURRENT) {
                            service.seek(0);
                            service.play();
                        } else {
                            if (D) Log.d(TAG, "Going to  of track");
                            service.gotoNext(false);
                        }
                        break;

                    //释放弱引用
                    case RELEASE_WAKELOCK:
                        service.mWakeLock.release();
                        break;

                    //焦点发生改变
                    case FOCUSCHANGE:
                        if (D) Log.d(TAG, "Received audio focus change event " + msg.arg1);
                        switch (msg.arg1) {
                            case AudioManager.AUDIOFOCUS_LOSS:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (service.isPlaying()) {
                                    service.mPausedByTransientLossOfFocus =
                                            msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
                                }
                                service.pause();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                removeMessages(FADEUP);
                                sendEmptyMessage(FADEDOWN);
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (!service.isPlaying()
                                        && service.mPausedByTransientLossOfFocus) {
                                    service.mPausedByTransientLossOfFocus = false;
                                    mCurrentVolume = 0f;
                                    service.mPlayer.setVolume(mCurrentVolume);
                                    service.play();
                                } else {
                                    removeMessages(FADEDOWN);
                                    sendEmptyMessage(FADEUP);
                                }
                                break;
                            default:
                        }
                        break;
                    //歌词下载
                    case LRC_DOWNLOADED:
                        service.notifyChange(LRC_UPDATED);
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 播放模式的操作类
     */
    private static final class Shuffler {

        private final LinkedList<Integer> mHistoryOfNumbers = new LinkedList<Integer>();

        private final TreeSet<Integer> mPreviousNumbers = new TreeSet<Integer>();

        private final Random mRandom = new Random();

        private int mPrevious;


        public Shuffler() {
            super();
        }


        public int nextInt(final int interval) {
            int next;
            do {
                next = mRandom.nextInt(interval);
            } while (next == mPrevious && interval > 1
                    && !mPreviousNumbers.contains(Integer.valueOf(next)));
            mPrevious = next;
            mHistoryOfNumbers.add(mPrevious);
            mPreviousNumbers.add(mPrevious);
            cleanUpHistory();
            return next;
        }


        private void cleanUpHistory() {
            if (!mHistoryOfNumbers.isEmpty() && mHistoryOfNumbers.size() >= MAX_HISTORY_SIZE) {
                for (int i = 0; i < Math.max(1, MAX_HISTORY_SIZE / 2); i++) {
                    mPreviousNumbers.remove(mHistoryOfNumbers.removeFirst());
                }
            }
        }
    }

    /**
     * 追踪错误信息实体类
     */
    private static final class TrackErrorInfo {
        public long mId;
        public String mTrackName;

        public TrackErrorInfo(long id, String trackName) {
            mId = id;
            mTrackName = trackName;
        }
    }

    /**
     * 正真的播放器，播放当前对象，并为下一个对象播放做准备，实现了播放错误和播放完成的监听
     */
    private static final class MultiPlayer implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {
        //对MediaService进行肉引用
        private final WeakReference<MediaService> mService;
        //当前媒体播放器
        private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();
        //下一个媒体播放器
        private MediaPlayer mNextMediaPlayer;

        private Handler mHandler;
        //是否初始化完成
        private boolean mIsInitialized = false;

        private String mNextMediaPath;

        private boolean isFirstLoad = true;


        private int sencondaryPosition = 0;

        private Handler handler = new Handler();

        //构造方法，持有服务自身
        public MultiPlayer(final MediaService service) {
            mService = new WeakReference<MediaService>(service);
            //设置媒体播放器的电量管理方式
            mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        }

        /**
         * 设置资源
         *
         * @param path
         */
        public void setDataSource(final String path) {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
            if (mIsInitialized) {
                setNextDataSource(null);
            }
        }

        /**
         * 设置下一个资源
         *
         * @param path
         */
        public void setNextDataSource(final String path) {
            mNextMediaPath = null;
            mIsNextInitialized = false;
            try {
                //当前媒体播放器播放完成之后，准备播放下一个
                mCurrentMediaPlayer.setNextMediaPlayer(null);
            } catch (IllegalArgumentException e) {
                Log.i(TAG, "Next media player is current one, continuing");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Media player not initialized!");
                return;
            }
            if (mNextMediaPlayer != null) {
                //释放资源
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            mNextMediaPlayer = new MediaPlayer();
            mNextMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());

            if (setNextDataSourceImpl(mNextMediaPlayer, path)) {
                mNextMediaPath = path;
                mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
                // mHandler.post(setNextMediaPlayerIfPrepared);

            } else {
                if (mNextMediaPlayer != null) {
                    mNextMediaPlayer.release();
                    mNextMediaPlayer = null;
                }
            }
        }

        boolean mIsTrackPrepared = false;
        boolean mIsTrackNet = false;
        boolean mIsNextTrackPrepared = false;
        boolean mIsNextInitialized = false;
        boolean mIllegalState = false;

        /**
         * 资源设置的具体实现方法
         *
         * @param player
         * @param path
         * @return
         */
        private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
            mIsTrackNet = false;
            mIsTrackPrepared = false;
            try {
                player.reset();
                //设置声音的类型
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);

                if (path.startsWith("content://")) {
                    player.setOnPreparedListener(null);
                    //正真的设置媒体播放器资源的方法
                    player.setDataSource(MainApplication.context, Uri.parse(path));
                    player.prepare();
                    //可以开始跟踪
                    mIsTrackPrepared = true;
                    //设置播放完成的回调
                    player.setOnCompletionListener(this);

                } else {
                    //不用解析直接设置资源
                    player.setDataSource(path);
                    //设置监听
                    player.setOnPreparedListener(preparedListener);
                    //异步准备
                    player.prepareAsync();
                    //跟踪网络
                    mIsTrackNet = true;
                }
                if (mIllegalState) {
                    //非法状态
                    mIllegalState = false;
                }

            } catch (final IOException todo) {

                return false;
            } catch (final IllegalArgumentException todo) {

                return false;
            } catch (final IllegalStateException todo) {
                todo.printStackTrace();
                //没有准备好再次准备
                if (!mIllegalState) {
                    L.E(D, TAG, "mcurrentmediaplayer invoke IllegalState");
                    mCurrentMediaPlayer = null;
                    mCurrentMediaPlayer = new MediaPlayer();
                    mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
                    mCurrentMediaPlayer.setAudioSessionId(getAudioSessionId());
                    setDataSourceImpl(mCurrentMediaPlayer, path);
                    mIllegalState = true;
                } else {
                    L.E(D, TAG, "mcurrentmediaplayer invoke IllegalState ,and try set again failed ,setnext");
                    mIllegalState = false;
                    return false;
                }
            }
            //错诶监听
            player.setOnErrorListener(this);
            //网络缓冲流状太监听
            player.setOnBufferingUpdateListener(bufferingUpdateListener);
            return true;
        }

        /**
         * 下一个资源的设置的具体实现
         *
         * @param player
         * @param path
         * @return
         */
        private boolean setNextDataSourceImpl(final MediaPlayer player, final String path) {
            //下一个可以跟踪
            mIsNextTrackPrepared = false;
            try {
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if (path.startsWith("content://")) {
                    player.setOnPreparedListener(preparedNextListener);
                    player.setDataSource(MainApplication.context, Uri.parse(path));
                    player.prepare();
                } else {
                    player.setDataSource(path);
                    player.setOnPreparedListener(preparedNextListener);
                    player.prepare();
                    mIsNextTrackPrepared = false;
                }

            } catch (final IOException todo) {

                return false;
            } catch (final IllegalArgumentException todo) {

                return false;
            }
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            //  player.setOnBufferingUpdateListener(this);
            return true;
        }

        /**
         * 设置handler
         *
         * @param handler
         */
        public void setHandler(final Handler handler) {
            mHandler = handler;
        }

        /**
         * 是否初始化完毕
         *
         * @return
         */
        public boolean isInitialized() {
            return mIsInitialized;
        }

        /**
         * 追踪准备完毕
         *
         * @return
         */
        public boolean isTrackPrepared() {
            return mIsTrackPrepared;
        }

        /**
         * 开始播放
         */
        public void start() {
            if (D) Log.d(TAG, "mIsTrackNet, " + mIsTrackNet);
            if (!mIsTrackNet) {
                //缓冲完成
                mService.get().sendUpdateBuffer(100);
                sencondaryPosition = 100;
                //开始播放
                mCurrentMediaPlayer.start();
            } else {
                //没有缓冲完成
                sencondaryPosition = 0;
                //重新加载
                mService.get().loading(true);
                //延时播放
                handler.postDelayed(startMediaPlayerIfPrepared, 50);
            }
            mService.get().notifyChange(MUSIC_CHANGED);
        }

        MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (isFirstLoad) {
                    long seekpos = mService.get().mLastSeekPos;
                    Log.e(TAG, "seekpos = " + seekpos);
                    seek(seekpos >= 0 ? seekpos : 0);
                    isFirstLoad = false;
                }
                // mService.get().notifyChange(TRACK_PREPARED);
                mService.get().notifyChange(META_CHANGED);
                mp.setOnCompletionListener(MultiPlayer.this);
                mIsTrackPrepared = true;
            }
        };
        /**
         * 媒体播放器的准备状态监听
         */
        MediaPlayer.OnPreparedListener preparedNextListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //准备完毕，可以跟踪下一个
                mIsNextTrackPrepared = true;
            }
        };
        /**
         * 网络流缓冲区的状态监听器
         */
        MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (sencondaryPosition != 100)
                    mService.get().sendUpdateBuffer(percent);
                sencondaryPosition = percent;
            }
        };

        Runnable setNextMediaPlayerIfPrepared = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                if (mIsNextTrackPrepared && mIsInitialized) {

//                    mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
                } else if (count < 60) {
                    handler.postDelayed(setNextMediaPlayerIfPrepared, 100);
                }
                count++;
            }
        };

        Runnable startMediaPlayerIfPrepared = new Runnable() {

            @Override
            public void run() {
                if (D) Log.d(TAG, "mIsTrackPrepared, " + mIsTrackPrepared);
                if (mIsTrackPrepared) {
                    mCurrentMediaPlayer.start();
                    final long duration = duration();
                    if (mService.get().mRepeatMode != REPEAT_CURRENT && duration > 2000
                            && position() >= duration - 2000) {
                        mService.get().gotoNext(true);
                        Log.e("play to go", "");
                    }
                    mService.get().loading(false);
                } else {
                    handler.postDelayed(startMediaPlayerIfPrepared, 700);
                }
            }
        };


        public void stop() {
            handler.removeCallbacks(setNextMediaPlayerIfPrepared);
            handler.removeCallbacks(startMediaPlayerIfPrepared);
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            mIsTrackPrepared = false;
        }


        public void release() {
            mCurrentMediaPlayer.release();
        }


        public void pause() {
            handler.removeCallbacks(startMediaPlayerIfPrepared);
            mCurrentMediaPlayer.pause();
        }


        public long duration() {
            if (mIsTrackPrepared) {
                return mCurrentMediaPlayer.getDuration();
            }
            return -1;
        }


        public long position() {
            if (mIsTrackPrepared) {
                try {
                    return mCurrentMediaPlayer.getCurrentPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return -1;
        }

        public long secondPosition() {
            if (mIsTrackPrepared) {
                return sencondaryPosition;
            }
            return -1;
        }


        public long seek(final long whereto) {
            mCurrentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }


        public void setVolume(final float vol) {
            try {
                mCurrentMediaPlayer.setVolume(vol, vol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
        }

        public void setAudioSessionId(final int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }


        @Override
        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
            Log.w(TAG, "Music Server Error what: " + what + " extra: " + extra);
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    final MediaService service = mService.get();
                    final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(),
                            service.getTrackName());

                    mIsInitialized = false;
                    mIsTrackPrepared = false;
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = new MediaPlayer();
                    mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                    Message msg = mHandler.obtainMessage(SERVER_DIED, errorInfo);
                    mHandler.sendMessageDelayed(msg, 2000);
                    return true;
                default:
                    break;
            }
            return false;
        }


        @Override
        public void onCompletion(final MediaPlayer mp) {

            Log.w(TAG, "completion");
            if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = mNextMediaPlayer;
                mNextMediaPath = null;
                mNextMediaPlayer = null;
                mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
            } else {
                mService.get().mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
            }
        }

    }

    /**
     * 实现了MediaAidlInterface.aidl 中定义的接口，跨进程调用！
     * 服务连接后返回的IBinder对象,
     * 相当于自定义IBinder类,
     * 开启服务的类可以通过该对象操作服务
     * Default implementation is a stub that returns false.
     * You will want to override this to do the appropriate unmarshalling of transactions
     */
    private static final class ServiceStub extends IMusicAidlInterface.Stub {

        private final WeakReference<MediaService> mService;

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            try {
                super.onTransact(code, data, reply, flags);
            } catch (final RuntimeException e) {
                Log.d(TAG, "onTransact error");
                e.printStackTrace();
                //获取缓存目录
                File file = new File(mService.get().getCacheDir().getAbsolutePath() + "/err/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                try {
                    //D打开服务的缓存目录，将错误信息写进缓存目录中
                    PrintWriter writer = new PrintWriter(mService.get().getCacheDir().getAbsolutePath()
                            + "/err/" + System.currentTimeMillis() + "_aidl.log");
                    e.printStackTrace(writer);
                    writer.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.sendTextMail("err aidl log from "
                                        + CommonUtils.getUniquePsuedoID(),
                                CommonUtils.getDeviceInfo() + Log.getStackTraceString(e));
                    }
                }).start();

                throw e;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }

        //私有的构造方法
        private ServiceStub(final MediaService service) {
            mService = new WeakReference<MediaService>(service);
        }

        @Override
        public void openFile(final String path) throws RemoteException {
            //调用service的方法
            mService.get().openFile(path);
        }

        @Override
        public void open(final Map infos, final long[] list, final int position)
                throws RemoteException {
            mService.get().open((HashMap<Long, MusicInfo>) infos, list, position);
        }

        @Override
        public void stop() throws RemoteException {
            mService.get().stop();
        }

        @Override
        public void pause() throws RemoteException {
            mService.get().pause();
        }


        @Override
        public void play() throws RemoteException {
            mService.get().play();
        }

        @Override
        public void prev(boolean forcePrevious) throws RemoteException {
            mService.get().prev(forcePrevious);
        }

        @Override
        public void next() throws RemoteException {
            mService.get().gotoNext(true);
        }

        @Override
        public void enqueue(final long[] list, final Map infos, final int action)
                throws RemoteException {
            mService.get().enqueue(list, (HashMap<Long, MusicInfo>) infos, action);
        }

        @Override
        public Map getPlayinfos() throws RemoteException {
            return mService.get().getPlayinfos();
        }

        @Override
        public void moveQueueItem(final int from, final int to) throws RemoteException {
            mService.get().moveQueueItem(from, to);
        }

        @Override
        public void refresh() throws RemoteException {
            mService.get().refresh();
        }

        @Override
        public void playlistChanged() throws RemoteException {
            mService.get().playlistChanged();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.get().isPlaying();
        }

        @Override
        public long[] getQueue() throws RemoteException {
            return mService.get().getQueue();
        }

        @Override
        public long getQueueItemAtPosition(int position) throws RemoteException {
            return mService.get().getQueueItemAtPosition(position);
        }

        @Override
        public int getQueueSize() throws RemoteException {
            return mService.get().getQueueSize();
        }

        @Override
        public int getQueueHistoryPosition(int position) throws RemoteException {
            return mService.get().getQueueHistoryPosition(position);
        }

        @Override
        public int getQueueHistorySize() throws RemoteException {
            return mService.get().getQueueHistorySize();
        }

        @Override
        public int[] getQueueHistoryList() throws RemoteException {
            return mService.get().getQueueHistoryList();
        }

        @Override
        public long duration() throws RemoteException {
            return mService.get().duration();
        }

        @Override
        public long position() throws RemoteException {
            return mService.get().position();
        }

        @Override
        public int secondPosition() throws RemoteException {
            return mService.get().getSecondPosition();
        }

        @Override
        public long seek(final long position) throws RemoteException {
            return mService.get().seek(position);
        }

        @Override
        public void seekRelative(final long deltaInMs) throws RemoteException {
            mService.get().seekRelative(deltaInMs);
        }

        @Override
        public long getAudioId() throws RemoteException {
            return mService.get().getAudioId();
        }

        @Override
        public MusicTrack getCurrentTrack() throws RemoteException {
            return mService.get().getCurrentTrack();
        }

        @Override
        public MusicTrack getTrack(int index) throws RemoteException {
            return mService.get().getTrack(index);
        }

        @Override
        public long getNextAudioId() throws RemoteException {
            return mService.get().getNextAudioId();
        }

        @Override
        public long getPreviousAudioId() throws RemoteException {
            return mService.get().getPreviousAudioId();
        }

        @Override
        public long getArtistId() throws RemoteException {
            return mService.get().getArtistId();
        }

        @Override
        public long getAlbumId() throws RemoteException {
            return mService.get().getAlbumId();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return mService.get().getArtistName();
        }

        @Override
        public String getTrackName() throws RemoteException {
            return mService.get().getTrackName();
        }

        @Override
        public boolean isTrackLocal() throws RemoteException {
            return mService.get().isTrackLocal();
        }

        @Override
        public String getAlbumName() throws RemoteException {
            return mService.get().getAlbumName();
        }

        @Override
        public String getAlbumPath() throws RemoteException {
            return mService.get().getAlbumPath();
        }

        @Override
        public String[] getAlbumPathtAll() throws RemoteException {
            return mService.get().getAlbumPathAll();
        }

        @Override
        public String getPath() throws RemoteException {
            return mService.get().getPath();
        }

        @Override
        public int getQueuePosition() throws RemoteException {
            return mService.get().getQueuePosition();
        }

        @Override
        public void setQueuePosition(final int index) throws RemoteException {
            mService.get().setQueuePosition(index);
        }

        @Override
        public int getShuffleMode() throws RemoteException {
            return mService.get().getShuffleMode();
        }

        @Override
        public void setShuffleMode(final int shufflemode) throws RemoteException {
            mService.get().setShuffleMode(shufflemode);
        }

        @Override
        public int getRepeatMode() throws RemoteException {
            return mService.get().getRepeatMode();
        }

        @Override
        public void setRepeatMode(final int repeatmode) throws RemoteException {
            mService.get().setRepeatMode(repeatmode);
        }

        @Override
        public int removeTracks(final int first, final int last) throws RemoteException {
            return mService.get().removeTracks(first, last);
        }


        @Override
        public int removeTrack(final long id) throws RemoteException {
            return mService.get().removeTrack(id);
        }


        @Override
        public boolean removeTrackAtPosition(final long id, final int position)
                throws RemoteException {
            return mService.get().removeTrackAtPosition(id, position);
        }


        @Override
        public int getMediaMountedCount() throws RemoteException {
            return mService.get().getMediaMountedCount();
        }


        @Override
        public int getAudioSessionId() throws RemoteException {
            return mService.get().getAudioSessionId();
        }


        @Override
        public void setLockscreenAlbumArt(boolean enabled) {
            mService.get().setLockscreenAlbumArt(enabled);
        }

        @Override
        public void exit() throws RemoteException {
            mService.get().exit();
        }

        @Override
        public void timing(int time) throws RemoteException {
            mService.get().timing(time);
        }

    }

    /**
     * 媒体观察者，监听媒体的内容变化，比如拷贝，下载了新的歌曲
     */
    private class MediaStoreObserver extends ContentObserver implements Runnable {

        private static final long REFRESH_DELAY = 500;
        private Handler mHandler;

        public MediaStoreObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            //首先移除原有的在列队中的Runnable
            mHandler.removeCallbacks(this);
            //将新的Runnable加入到二额queen中，指定时间之后运行
            mHandler.postDelayed(this, REFRESH_DELAY);
        }

        @Override
        public void run() {
            Log.e("ELEVEN", "calling refresh!");
            //通知更新播放列表的数量等信息
            refresh();
        }
    }


}