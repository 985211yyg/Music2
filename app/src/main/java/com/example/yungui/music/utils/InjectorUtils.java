package com.example.yungui.music.utils;

import android.content.Context;

import com.example.yungui.music.dataUtils.LocalDataSource;
import com.example.yungui.music.dataUtils.MusicRepository;
import com.example.yungui.music.db.MusicInfoDataBase;
import com.example.yungui.music.model.viewmodel.MusicInfoViewModelFactory;

/**
 * Created by yungui on 2017/12/29.
 * 提供依赖注入的静态函数。
 * 依赖注入的思路是，将必备组件提供给类使用，而不是在类自身内部创建这些组件
 */

public class InjectorUtils {
    //提供存储区库类 分别持有数据库和数据源
    public static MusicRepository provideRepository(Context context) {
        //实例化数据库
        MusicInfoDataBase musicInfoDataBase = MusicInfoDataBase.getInstance(context.getApplicationContext());
        AppExecutors appExecutors = AppExecutors.getInstance();
        LocalDataSource localDataSource = LocalDataSource.getInstance(context.getApplicationContext(), appExecutors);
        return MusicRepository.getInstance(musicInfoDataBase.musicInfoDao(),
                localDataSource,
                appExecutors);
    }

    //提供MusicInfoViewModelFactory,持有存储区库类
    public static MusicInfoViewModelFactory provideMusicViewModelFactory(Context context) {
        return new MusicInfoViewModelFactory(provideRepository(context));
    }

    public static MusicInfoDataBase provideMusicInfoDataBase(Context context) {
        return MusicInfoDataBase.getInstance(context);
    }
}
