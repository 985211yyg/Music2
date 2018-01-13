package com.example.yungui.music;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.example.yungui.music.permission.PermissionHelper;
import com.google.gson.Gson;

/**
 * Created by yungui on 2017/10/19.
 */

public class MainApplication extends Application {
    public static Context context;
    private static Gson gson;

    public static Gson getGsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = MainApplication.this;
        PermissionHelper.init(this);
        //初始化工具类
        Utils.init(this);
    }
}
