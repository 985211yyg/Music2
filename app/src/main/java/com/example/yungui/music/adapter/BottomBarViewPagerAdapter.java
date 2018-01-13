package com.example.yungui.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.yungui.music.fragment.BottomControlBarItemFragment;
import com.example.yungui.music.info.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/11/27.
 */

public class BottomBarViewPagerAdapter extends FragmentPagerAdapter {
    private List<MusicInfo> mMusicInfos = new ArrayList<>();

    public BottomBarViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return BottomControlBarItemFragment.newInstance(mMusicInfos.get(position));
    }

    @Override
    public int getCount() {
        return mMusicInfos.size();
    }

    public void addData(MusicInfo musicInfo) {
        mMusicInfos.add(musicInfo);
        notifyDataSetChanged();
    }

    public void addData(List<MusicInfo> musicInfo) {
        mMusicInfos = musicInfo;
        notifyDataSetChanged();
    }
}
