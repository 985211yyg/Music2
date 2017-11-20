package com.example.yungui.music.fragment;

import android.os.Bundle;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

/**
 * Created by yungui on 2017/10/22.
 */

public class ArtistFragment extends BaseFragment {
    public ArtistFragment() {
    }
    public static ArtistFragment newInstance() {
        ArtistFragment artistFragment = new ArtistFragment();
        Bundle arg = new Bundle();
        artistFragment.setArguments(arg);
        return artistFragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_local_tab_item;
    }

    @Override
    protected void initView() {

    }
}
