package com.example.yungui.music.base;


import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yungui.music.Interface.MusicStateListener;

import butterknife.ButterKnife;

/**
 * Created by yungui on 2017/10/16.
 */

public abstract class BaseFragment extends LifecycleFragment {
    protected Context mContext;
    protected View rootView;
    protected boolean isViewCreated;//视图是否已经创建完成
    protected boolean isViewVisible;//视图是否可见
    protected boolean isLoad = false;//是否已经加载过数据

    protected abstract int getLayoutID();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void loadData();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isViewCreated = true;//视图创建完毕
        lazyLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutID(), container, false);
        ButterKnife.bind(this, rootView);
        initView(savedInstanceState);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isViewVisible = true;//视图对用户可见
            lazyLoad();
        }
    }

    private void lazyLoad() {
        if (isViewCreated && isViewVisible && !isLoad) {
            loadData();
            isViewVisible = false;
            isViewCreated = false;
            isLoad = true;
        }
    }


    /**
     * fragment可见是设置监听
     */
    @Override
    public void onResume() {
        super.onResume();
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


    }
}
