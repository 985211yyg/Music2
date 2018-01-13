package com.example.yungui.music.model.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.example.yungui.music.dataUtils.MusicRepository;

/**
 * Created by yungui on 2017/12/29.
 * view Model提供者
 */

public class MusicInfoViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MusicRepository musicRepository;
    public MusicInfoViewModelFactory(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MusicInfoViewModel(musicRepository);
    }
}
