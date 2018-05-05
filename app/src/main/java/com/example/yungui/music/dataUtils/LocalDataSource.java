package com.example.yungui.music.dataUtils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.yungui.music.Interface.IConstants;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.utils.AppExecutors;
import com.example.yungui.music.utils.MusicUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yungui on 2017/12/28.
 * 获取本地数据歌曲数据的类
 */

public class LocalDataSource {
    public static final String TAG = LocalDataSource.class.getSimpleName();
    public static final Object LOCK = new Object();
    private static LocalDataSource Instance;
    private MutableLiveData<List<MusicInfo>> localMusicInfos;
    private Context context;
    private AppExecutors appExecutors;

    //私有构造方法
    private LocalDataSource(Context context, AppExecutors appExecutors) {
        this.context = context;
        localMusicInfos = new MutableLiveData<>();
        this.appExecutors = appExecutors;
    }

    public static LocalDataSource getInstance(Context context, AppExecutors appExecutors) {
        Log.e(TAG, "getInstance: ");
        if (Instance == null) {
            synchronized (LOCK) {
                Instance = new LocalDataSource(context, appExecutors);
            }
        }
        return Instance;
    }

    //提供本地歌曲数据
    public LiveData<List<MusicInfo>> getLocalMusicInfos() {
        queryMusicInfo();
        return localMusicInfos;
    }

    ///查询数据
    public void queryMusicInfo() {
        Observable.just(MusicUtils.queryMusic(context, IConstants.START_FROM_LOCAL))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<List<MusicInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MusicInfo> musicInfos) {
                        localMusicInfos.postValue(musicInfos);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
