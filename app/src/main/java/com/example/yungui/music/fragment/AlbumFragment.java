package com.example.yungui.music.fragment;

import android.os.Bundle;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

/**
 * Created by yungui on 2017/10/22.
 */

public class AlbumFragment extends BaseFragment {
    public AlbumFragment() {
    }

    public static AlbumFragment newInstance() {
        AlbumFragment albumFragment = new AlbumFragment();
        Bundle arg = new Bundle();
        albumFragment.setArguments(arg);
        return albumFragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_local_tab_item;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void loadData() {

    }


}
