package com.example.yungui.music.fragment;

import android.os.Bundle;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;


/**
 * Created by yungui on 2017/11/12.
 */

public class SongDetailAlbumFragment extends BaseFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private boolean isScene2 = false;


    public SongDetailAlbumFragment() {

    }

    public static SongDetailAlbumFragment newInstance(int sectionNumber) {
        SongDetailAlbumFragment fragment = new SongDetailAlbumFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_song_detail_album;
    }


    @Override
    protected void initView() {
//
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
