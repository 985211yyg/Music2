package com.example.yungui.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.R;
import com.example.yungui.music.info.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/10/22.
 */

public class LocalRecyclerViewAdapter extends BaseQuickAdapter<MusicInfo, BaseViewHolder> {

    public LocalRecyclerViewAdapter(int layoutResId, @Nullable List<MusicInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicInfo item) {
        helper.setText(R.id.music_name, item.musicName);
        helper.setText(R.id.music_singer, item.artist);

    }
}
