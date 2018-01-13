package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yungui.music.R;

import butterknife.ButterKnife;

/**
 * Created by yungui on 2017/12/27.
 */

public class PopupItemFragment extends Fragment {
    public static final String TAG = "tag";
    public static final String TITLE = "title";
    public static final String UPDATE = "升级音质";
    public static final String SCANNING = "扫描歌曲";
    public static final String DOWNLOAD_LRC = "一键下载词图";
    public static final String RECOVER = "本地歌曲恢复助手";

    public static final int TAG_UPDATE = 0;
    public static final int TAG_SCANNING = 1;
    public static final int TAG_DOWNLOAD_LRC = 2;
    public static final int TAG_RECOVER = 3;


    private int fromTag;
    private String title;
    private View rootView;


    public static PopupItemFragment newInstance(int fromTag, String title) {
        Bundle args = new Bundle();
        args.putInt(TAG, fromTag);
        args.putString(TITLE, title);
        PopupItemFragment fragment = new PopupItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromTag = getArguments().getInt(TAG);
        title = getArguments().getString(TITLE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        switch (fromTag) {


        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
