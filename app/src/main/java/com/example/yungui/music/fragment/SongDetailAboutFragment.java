package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

/**
 * Created by yungui on 2017/11/12.
 */

public class SongDetailAboutFragment extends BaseFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public SongDetailAboutFragment() {

    }

    public static SongDetailAboutFragment newInstance(int sectionNumber) {
        SongDetailAboutFragment fragment = new SongDetailAboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_song_detail_about;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void loadData() {

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
