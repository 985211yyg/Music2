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

public abstract class BaseActivity extends AppCompatActivity{
    public static final String TAG = BaseActivity.class.getSimpleName();
    public static final int requestCode = 1;
    protected Toolbar mToolbar;

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
