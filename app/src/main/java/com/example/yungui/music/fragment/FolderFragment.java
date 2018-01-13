package com.example.yungui.music.fragment;

import android.os.Bundle;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

/**
 * Created by yungui on 2017/10/22.
 */

public class FolderFragment extends BaseFragment {
    public FolderFragment() {
    }
    public static FolderFragment newInstance() {
        FolderFragment folderFragment = new FolderFragment();
        Bundle arg = new Bundle();
        folderFragment.setArguments(arg);
        return folderFragment;
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
