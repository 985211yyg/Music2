package com.example.yungui.music.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;

import java.io.IOException;

/**
 * Created by yungui on 2017/9/28.
 */

public class MusicService extends Service {
    private RemoteViews remoteViews;
    public static final String TAG = "MusicService";
    public static final String UPDATE = "com.example.yungui.music.UpdateStatus";
    public static final String STATUS = "status";
    public static final String PREVIOUS = "previous";
    public static final String PAUSE = "pause";
    public static final String NEXT = "next";
    public static final String CLOSE = "close";
    private MediaPlayer mediaPlayer;
    private MusicBinder musicBinder = new MusicBinder();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        mediaPlayer = new MediaPlayer();

        //将后台service变成前台服务
        //并且构架点击才通知栏打开主页的操作
        Intent intent = new Intent(this, MainActivity.class);
        Intent pause = new Intent(PAUSE);
        Intent next = new Intent(NEXT);
        Intent previous = new Intent(PREVIOUS);
        Intent close = new Intent(CLOSE);

        //启动主界面
        PendingIntent pendingIntent2Activity = PendingIntent.getActivity(this, 0, intent, 0);
        //发送广播
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this, 0, pause, 0);
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(this, 1, previous, 0);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, 2, next, 0);
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(this, 3, close, 0);


        Notification.Builder builder = new Notification.Builder(this);
        ///在锁定屏幕上控制音乐播放
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setUsesChronometer(false);
        //通知栏小图标
        builder.setSmallIcon(R.drawable.audiotrack);
        //通知栏详情大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
        builder.setContentTitle("你就不要想起我");
        builder.setContentText("田馥甄");
        //加载操作按钮
        builder.addAction(R.drawable.previous, "Previous", pendingIntentPrevious);
        builder.addAction(R.drawable.play_circle, "Pause", pendingIntentPause);
        builder.addAction(R.drawable.next, "Next", pendingIntentNext);
        builder.addAction(R.drawable.close, "close", pendingIntentClose);
        //应用媒体样式模板
        builder.setStyle(new Notification.MediaStyle());
        builder.setContentIntent(pendingIntent2Activity);


//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.id.notification);
//        remoteViews.setImageViewResource(R.id.imageView2, R.drawable.previous);
//        remoteViews.setImageViewResource(R.id.imageView3, R.drawable.pause_circle);
//        remoteViews.setImageViewResource(R.id.imageView4, R.drawable.next);
//        remoteViews.setImageViewResource(R.id.close, R.drawable.close);
//        remoteViews.setTextViewText(R.id.song_name,"你是神经吧");
//        remoteViews.setTextViewText(R.id.singer,"田馥甄");
//
        Notification notification = builder.build();
        //在前台运行并且在状态栏中显示
        startForeground(1, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return musicBinder;
    }

    public class MusicBinder extends Binder {

        public void play(String data) {
            MusicPlayer(data);
        }

        public void pause() {

        }

        public void next(int songID) {

        }

        public void previous(int songID) {

        }

    }

    //接收器用于响应通知栏的操作以及activity主界面的操作
    public static class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("previous")) {
                Log.e(TAG, "onReceive:前一首 ");


            } else if (action.equals("pause")) {
                Log.e(TAG, "onReceive:暂停/播放 ");


            } else if (action.equals("next")) {
                Log.e(TAG, "onReceive:下一首 ");


            } else if (action.equals("close")) {
                Log.e(TAG, "onReceive: 关闭");


            }

        }
    }

    public void MusicPlayer(String songPath) {
        try {
            //重置播放器
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
