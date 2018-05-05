package com.example.yungui.music;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.example.yungui.music.permission.PermissionHelper;
import com.google.gson.Gson;

/**
 * Created by yungui on 2017/10/19.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    public static Context context;
    private static Gson gson;
    private Activity mActivity;
    private static MainApplication instance;

    public static Gson getGsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = MainApplication.this;
        initGlobeActivity();
        PermissionHelper.init(this);
        //初始化工具类
        Utils.init(this);
    }

    private void initGlobeActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mActivity = activity;


            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                mActivity = activity;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mActivity = activity;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                mActivity = activity;
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivity = activity;
            }
        });

    }

    public static MainApplication getInstance() {
        return instance;
    }

    public Activity getCurrentActivity() {
        Log.e(TAG, "getCurrentActivity: "+mActivity.getComponentName()  );
        return mActivity;
    }
}
