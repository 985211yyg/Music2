package com.example.yungui.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.R;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.widget.CircleImageView;

import java.util.List;

/**
 * Created by yungui on 2017/9/29.
 */

public class BottomBarAdapter extends BaseQuickAdapter<MusicInfo, BaseViewHolder> {

    private CircleImageView imageView;

    public BottomBarAdapter(int layoutResId, @Nullable List<MusicInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicInfo item) {

        imageView = helper.getView(R.id.circle_cd);
        imageView.setImageResource(R.mipmap.timg);
        helper.setText(R.id.bottom_bar_song_name, item.musicName);
        helper.setText(R.id.bottom_bar_singer, item.artist);
        if (MusicPlayer.getCurrentAudioId() == item.songId) {
            imageView.startRotate();
        } else {
            imageView.stopRotate();
        }
    }
}
