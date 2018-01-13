package com.example.yungui.music.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yungui on 2017/12/29.
 */

public class AppExecutors {
    public static final Object LOCK = new Object();
    private static AppExecutors Instance;
    private final Executor LocalIO;
    private final Executor mainThread;
    private final Executor netWorkIO;

    private AppExecutors(Executor localIO, Executor mainThread, Executor netWorkIO) {
        LocalIO = localIO;
        this.mainThread = mainThread;
        this.netWorkIO = netWorkIO;
    }

    public static AppExecutors getInstance() {
        if (Instance == null) {
            synchronized (LOCK) {
                Instance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return Instance;
    }

    public Executor localIO() {
        return LocalIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor netWorkIO() {
        return netWorkIO;
    }

    private static class MainThreadExecutor implements Executor {
        //获取主线程handler
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            //发送到主线程中执行
            mainThreadHandler.post(command);
        }
    }
}
