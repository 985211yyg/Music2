package com.example.yungui.music.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yungui.music.Interface.MusicStateListener;
import com.example.yungui.music.MainActivity;

/**
 * Created by yungui on 2017/10/16.
 */

public abstract class BaseFragment extends Fragment implements MusicStateListener {
    protected Context mContext;
    protected View rootView;

    protected abstract int getLayoutID();

    protected abstract void initView();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext =context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutID(), container, false);
        initView();
        return rootView;
    }

    /**
     * fragment可见是设置监听
     */
    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setMusicStateListener(this);
    }

    @Override
    public void updateTrackInfo() {

    }

    @Override
    public void updateTime() {

    }

    @Override
    public void updateTheme() {

    }

    @Override
    public void updateAdapter() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 解除监听
     */
    @Override
    public void onStop() {
        super.onStop();
        ((BaseActivity) getActivity()).removeMusicStateListener(this);

    }
}
