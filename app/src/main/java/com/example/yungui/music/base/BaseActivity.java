package com.example.yungui.music.base;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.example.yungui.music.IMusicAidlInterface;
import com.example.yungui.music.Interface.IConstants;
import com.example.yungui.music.Interface.MusicStateListener;
import com.example.yungui.music.R;
import com.example.yungui.music.fragment.BottomControlBarFragment;
import com.example.yungui.music.service.MediaService;
import com.example.yungui.music.service.MusicPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import static com.example.yungui.music.service.MusicPlayer.mService;

/**
 * Created by yungui on 2017/10/16.
 * 在这个类中完成底部控制栏的控制，实例化IMusicAidlInterface 并交给musicPlayer代理，
 * 同时内置广播接收器，用于接受播放信息：通知栏对播放的控制需要对controlBar进行同步，接受后台播放的进度，并更新控件
 */

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {
    public static final int requestCode = 1;
    private MusicPlayer.ServiceToken serviceToken;
    private PlayBackStatusReceiver playBackStatusReceiver;
    //与fragment通信的接口集合
    private ArrayList<MusicStateListener> musicStateListeners = new ArrayList<>();
    protected Toolbar mToolbar;
    private boolean isShowBottomControlBar=true;

    public static final String TAG = BaseActivity.class.getSimpleName();

    //获取布局
    protected abstract
    @LayoutRes
    int getLayoutID();

    protected abstract
    @MenuRes
    int getMenuID();
    //是否带有toolbar
    protected abstract boolean setToolBar();

    /*
    初始化布局,在activity创建时调用，将参数传出去
     */
    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void setTransition();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(Window.FEATURE_CONTENT_TRANSITIONS);
        //检查权限是否被允许
        if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //是否需要作出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //todo 解释

            } else {
                //不用解释直接请求权限
                ActivityCompat.requestPermissions(BaseActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);

            }
        }
        setContentView(getLayoutID());
        initToolBar(setToolBar());
        initView(savedInstanceState);
        setTransition();

        serviceToken = MusicPlayer.bindToService(this, this);
        //实例化广播接收器
        playBackStatusReceiver = new PlayBackStatusReceiver(new WeakReference<BaseActivity>(this));
        //定义接收器能够接受的信息过滤器，同时动态注册接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MediaService.QUEUE_CHANGED);//播放列改变
        intentFilter.addAction(MediaService.META_CHANGED);//元数据改变
        intentFilter.addAction(MediaService.PLAYLIST_CHANGED);//播放列表改变
        intentFilter.addAction(MediaService.MUSIC_CHANGED);//音乐改变
        intentFilter.addAction(MediaService.POSITION_CHANGED);//位置改变
        intentFilter.addAction(MediaService.SHUFFLEMODE_CHANGED);//播放模改版
        intentFilter.addAction(MediaService.REPEATMODE_CHANGED);//循环模式改变
        intentFilter.addAction(MediaService.PLAYSTATE_CHANGED);//播放状态改变
        intentFilter.addAction(IConstants.MUSIC_COUNT_CHANGED);//数量改变
        intentFilter.addAction(IConstants.PLAYLIST_COUNT_CHANGED);//列表改变
        intentFilter.addAction(IConstants.EMPTY_LIST);//空列表
        //注册
        registerReceiver(playBackStatusReceiver, intentFilter);

    }

    private void initToolBar(boolean enable) {
        if (enable) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //权限请求成功
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                //权限请求失败  ，直接退出
                this.finish();
            }
        }
    }

    public void setDisplayHomeAsUpEnabled(boolean enabled) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        }
    }

    /**
     * 更新列队
     */
    public void updateQueue() {
        Log.e(TAG, "============================updateQueue: ");

    }

    /**
     * 更新追踪信息
     */
    public void updateTrackInfo() {
        Log.e(TAG, "============================updateTrackInfo: " );
        for (MusicStateListener musicStateListener : musicStateListeners) {
            if (musicStateListener != null) {
                musicStateListener.updateTrackInfo();
            }
        }

    }

    /**
     * 刷新fragment UI
     */
    public void refreshUI() {
        Log.e(TAG, "============================refreshUI: ");
        for (MusicStateListener musicStateListener : musicStateListeners) {
            if (musicStateListener != null) {
                musicStateListener.updateAdapter();
            }
        }

    }

    /**
     * 通知个片段更新时间
     */
    public void updateTime() {
        Log.e(TAG, "============================updateTime: ");

    }

    /**
     * 通知更新主题
     */
    public void updateTheme() {
        Log.e(TAG, "============================updateTheme: ");
        for (MusicStateListener musicStateListener : musicStateListeners) {
            if (musicStateListener != null) {
                musicStateListener.updateTheme();
            }
        }
    }

    /**
     * 更新追踪
     */
    public void updateTrack() {
        Log.e(TAG, "============================updateTrack: ");

    }

    /**
     * 更新歌词
     */
    public void updateLrc() {
        Log.e(TAG, "============================updateLrc: ");

    }

    /**
     * 更新缓冲进度
     *
     * @param progress
     */
    public void updateBuffer(int progress) {
        Log.e(TAG, "============================updateBuffer: ");

    }

    /**
     * 加载
     *
     * @param loading
     */
    public void loading(boolean loading) {
        Log.e(TAG, "============================loading: ");

    }

    /**
     * 控制底播放控制栏的显影
     *
     * @param show
     */
    public void showControlBar(boolean show) {
        isShowBottomControlBar = show;
    }

    /**
     * 解绑服务，使用代理类解绑
     */
    public void unbindService() {
        if (serviceToken != null) {
            MusicPlayer.unbindFromService(serviceToken);
            serviceToken = null;
        }
    }

    /**
     * 设置音乐状态监听器,fragment 通过实现监听接口，然后通过getActivity().set设置监听接口,然后又activity集中处理监听，
     * 当状态改变时通过接口，通知fragment进行响应
     */
    public void setMusicStateListener(MusicStateListener musicStateListener) {
        if (musicStateListener == this) {
            throw new UnsupportedOperationException(" override this listener don't  implement");
        }
        if (musicStateListener != null) {
            musicStateListeners.add(musicStateListener);
        }

    }

    //移除监听器
    public void removeMusicStateListener(MusicStateListener musicStateListener) {
        if (musicStateListener != null) {
            musicStateListeners.remove(musicStateListener);
        }

    }

    /**
     * 实现ServiceConnection接口，实例化MusicPlayer中的service
     *
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //在activity中实例化mService，
        // 然后交给MusicPlayer这个代理类来处理，
        // activity通过MusicPlayer和service进行交互
        mService = IMusicAidlInterface.Stub.asInterface(service);
    }

    /**
     * 服务连接失败的回调
     *
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;

    }

    /**
     * 绑定失效  在这处理重连
     *
     * @param name
     */
    @Override
    public void onBindingDied(ComponentName name) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
        unregisterReceiver(playBackStatusReceiver);
        musicStateListeners.clear();
    }


    public class PlayBackStatusReceiver extends BroadcastReceiver {
        //弱引用
        private final WeakReference<BaseActivity> weakReference;

        public PlayBackStatusReceiver(WeakReference<BaseActivity> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            BaseActivity baseActivity = weakReference.get();
            //如果没有被GC回收
            if (baseActivity != null) {
                //数据源发生改变
                if (action == MediaService.META_CHANGED) {
                    baseActivity.updateTrackInfo();
                } else if (action.equals(MediaService.PLAYSTATE_CHANGED)) {

                } else if (action.equals(MediaService.TRACK_PREPARED)) {
                    baseActivity.updateTime();

                } else if (action.equals(MediaService.BUFFER_UP)) {

                    baseActivity.updateBuffer(intent.getIntExtra("progress", 0));
                } else if (action.equals(MediaService.MUSIC_LODING)) {

                    baseActivity.loading(intent.getBooleanExtra("isloading", false));
                } else if (action.equals(MediaService.REFRESH)) {

                } else if (action.equals(IConstants.MUSIC_COUNT_CHANGED)) {

                    baseActivity.refreshUI();
                } else if (action.equals(IConstants.PLAYLIST_COUNT_CHANGED)) {

                    baseActivity.refreshUI();
                } else if (action.equals(MediaService.QUEUE_CHANGED)) {

                    baseActivity.updateQueue();
                } else if (action.equals(MediaService.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.exit) +
                            intent.getStringExtra(MediaService.TrackErrorExtra.TRACK_NAME);
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                } else if (action.equals(MediaService.MUSIC_CHANGED)) {

                    baseActivity.updateTrack();
                } else if (action.equals(MediaService.LRC_UPDATED)) {

                    baseActivity.updateLrc();
                }
            }

        }
    }
}
