package com.example.yungui.music.model.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.yungui.music.dataUtils.MusicRepository;
import com.example.yungui.music.info.MusicInfo;

import java.util.List;

/**
 * Created by yungui on 2017/12/28.
 * <p>
 * viewModel  持有存储区库类
 * 负责数据的获取操作
 * 同时在设置改变时保存数据
 */

public class MusicInfoViewModel extends ViewModel {
    private LiveData<List<MusicInfo>> mMusicInfos;
    private MusicRepository mMusicRepository;

    public MusicInfoViewModel(MusicRepository musicRepository) {
        mMusicRepository = musicRepository;
        mMusicInfos = mMusicRepository.getAllMusicInfo();
    }

    /**
     * 对外提供数据
     *
     * @return
     */
    public LiveData<List<MusicInfo>> getMusicInfo() {
        return mMusicInfos;
    }

}
