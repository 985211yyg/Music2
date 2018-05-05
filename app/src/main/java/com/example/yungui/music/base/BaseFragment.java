package com.example.yungui.music.base;


import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yungui.music.Interface.MediaBrowserChangeListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by yungui on 2017/10/16.
 */

public abstract class BaseFragment extends LifecycleFragment implements MediaBrowserChangeListener {

    protected Context mContext;
    protected View rootView;
    protected boolean isViewCreated;//视图是否已经创建完成
    protected boolean isViewVisible;//视图是否可见
    protected boolean isLoad = false;//是否已经加载过数据
    protected boolean isPlaying;
    protected Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutID(), container, false);
        mUnbinder=ButterKnife.bind(this, rootView);
        initView(savedInstanceState);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;//视图创建完毕
        lazyLoad();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * fragment可见是设置监听
     */
    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).addMediaBrowserChangeListener(this);
    }

    /**
     * 解除监听
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((BaseActivity) getActivity()).removeMediaBrowserChangeListener(this);
        mUnbinder.unbind();

    }

    //=========================来自阿曾提出的调用============================================
    @Override
    public void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat) {
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
        isPlaying = playbackStateCompat != null &&
                playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING;
    }

    @Override
    public void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems) {

    }
    //====================================================

    protected abstract int getLayoutID();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void loadData();

    //对用户是否可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isViewVisible = true;//视图对用户可见
            lazyLoad();
        } else {
            isViewVisible = false;
        }
    }

    private void lazyLoad() {
        if (isViewCreated && isViewVisible) {
            loadData();
            isViewVisible = false;
            isViewCreated = false;
        }
    }


}
