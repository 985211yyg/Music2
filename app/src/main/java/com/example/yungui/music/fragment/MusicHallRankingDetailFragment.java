package com.example.yungui.music.fragment;

import android.os.Bundle;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

/**
 * Created by 22892 on 2018/2/1.
 */

public class MusicHallRankingDetailFragment extends BaseFragment {

    public static MusicHallRankingDetailFragment newInstance() {
        Bundle args = new Bundle();
        MusicHallRankingDetailFragment fragment = new MusicHallRankingDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_music_hall_ranking_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void loadData() {

    }
}
