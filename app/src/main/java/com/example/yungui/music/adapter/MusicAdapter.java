package com.example.yungui.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.modle.Song;

import java.util.List;

/**
 * Created by yungui on 2017/9/29.
 */

public class MusicAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {

    public MusicAdapter(int layoutResId, @Nullable List<Song> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Song item) {

    }
}
