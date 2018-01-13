package com.example.yungui.music.dataUtils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.yungui.music.dao.MusicInfoDao;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.utils.AppExecutors;

import java.util.List;

/**
 * Created by yungui on 2017/12/28.
 * 获取本地数据和网络数据的库类,
 * 同时在其中进行数库数据插入，查询删除等代理操作
 */

//存储区
public class MusicRepository {
    public static final String TAG = MusicRepository.class.getSimpleName();
    private static final Object lock = new Object();
    private static MusicRepository Instance;
    private LocalDataSource localDataSource;
    private MusicInfoDao musicInfoDao;
    private AppExecutors appExecutors;

    //数据是否初始化完毕的标记
    private boolean initialize;

    private MusicRepository(final MusicInfoDao musicInfoDao,
                            LocalDataSource localDataSource,
                            AppExecutors appExecutors) {
        this.musicInfoDao = musicInfoDao;
        this.localDataSource = localDataSource;
        this.appExecutors = appExecutors;

        /**
         * 取得数据并向Room表中插入数据
         */
        LiveData<List<MusicInfo>> localMusics = localDataSource.getLocalMusicInfos();
        //使用observeForever的原因是当数据改变时，出发数据库保存
        localMusics.observeForever(new Observer<List<MusicInfo>>() {
            @Override
            public void onChanged(@Nullable List<MusicInfo> musicInfos) {
                //新线程中进行数据库的操作
                appExecutors.localIO().execute(() -> {
                    musicInfoDao.bulkInsert(musicInfos);
                });
            }
        });


    }

    public synchronized static MusicRepository getInstance(MusicInfoDao musicInfoDao,
                                                           LocalDataSource localDataSource,
                                                           AppExecutors appExecutors) {
        if (Instance == null) {
            synchronized (lock) {
                Instance = new MusicRepository(musicInfoDao, localDataSource, appExecutors);
            }
        }
        return Instance;
    }

    public synchronized void initializeData() {
        Log.e(TAG, "initializeData: ");
        if (initialize) {
            return;
        }
        initialize = true;
        //调用获取数据的方法
        queryMusicInfo();

    }

    /**
     * 从数据库中获取数据
     *
     * @return
     */
    public LiveData<List<MusicInfo>> getAllMusicInfo() {
        Log.e(TAG, "-----------getAllMusicInfo:开始 ");
        //再次初始化数据
        initializeData();
        return musicInfoDao.getAll();
    }

    /**
     * 获取本地数据
     */
    public void queryMusicInfo() {
        appExecutors.localIO().execute(() -> {
            localDataSource.queryMusicInfo();
        });
    }

    /**
     * 插入数据
     */
    public void insert(MusicInfo musicInfo) {
        appExecutors.localIO().execute(()->{
            musicInfoDao.insert(musicInfo);
        });
    }


}


