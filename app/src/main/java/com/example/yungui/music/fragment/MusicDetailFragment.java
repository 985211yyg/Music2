package com.example.yungui.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.CommonViewPagerAdapter;
import com.example.yungui.music.adapter.RankingViewPagerAdapter;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.model.Music;
import com.example.yungui.music.utils.JsonUtils;
import com.example.yungui.music.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MusicDetailFragment extends BaseFragment {
    public static String Fragment_Tag = "MusicDetailFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.music_detail_toolbar)
    Toolbar mMusicDetailToolbar;
    @BindView(R.id.music_detail_tabLayout)
    TabLayout mMusicDetailTabLayout;
    @BindView(R.id.music_detail_viewPager)
    ViewPager mMusicDetailViewPager;

    private RankingViewPagerAdapter mRankingViewPagerAdapter;
    private List<Music> mMusics;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public static MusicDetailFragment newInstance(String param1, String param2) {
        MusicDetailFragment fragment = new MusicDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutID() {
        return R.layout.fragment_music_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setSupportActionBar(mMusicDetailToolbar);
        ((MainActivity) getActivity()).setDisplayHomeAsUpEnabled(true);
        mMusicDetailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        mRankingViewPagerAdapter = new RankingViewPagerAdapter(getChildFragmentManager());
        mRankingViewPagerAdapter.addTitles(new String[]{"单曲", "详情"});
        mRankingViewPagerAdapter.addFragments(new MusicHallRankingSongFragment());
        mRankingViewPagerAdapter.addFragments(new MusicHallRankingDetailFragment());
        mMusicDetailViewPager.setAdapter(mRankingViewPagerAdapter);
        mMusicDetailTabLayout.setupWithViewPager(mMusicDetailViewPager);
    }

    @Override
    protected void loadData() {
        mMusics = JsonUtils.paresMusicFromAssetsSource("musics.json");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
