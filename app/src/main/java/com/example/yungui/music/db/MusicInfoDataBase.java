package com.example.yungui.music.db;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.yungui.music.dao.MusicInfoDao;
import com.example.yungui.music.info.MusicInfo;

/**
 * Created by yungui on 2017/12/28.
 */

@Database(entities = {MusicInfo.class}, version = 3, exportSchema = true)
public abstract class MusicInfoDataBase extends RoomDatabase {
    //持有数据库访问对象类
    public abstract MusicInfoDao musicInfoDao();

    private static final String DATABASE_NAME = "MusicInfoDataBase";
    private static final Object lock = new Object();
    private static volatile MusicInfoDataBase Instance;

    //单例模式的数据库
    public static MusicInfoDataBase getInstance(Context context) {
        if (Instance == null) {
            synchronized (lock) {
                //建立数据库
                Instance = Room.databaseBuilder(context.getApplicationContext(),
                        MusicInfoDataBase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return Instance;
    }

}
