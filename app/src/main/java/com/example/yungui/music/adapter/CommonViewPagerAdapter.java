package com.example.yungui.music.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.yungui.music.model.Music;

import java.util.List;

/**
 * Created by 22892 on 2018/2/1.
 */

public class CommonViewPagerAdapter<T> extends PagerAdapter {
    public static final int VIEW_TAG = 2;
    private List<ItemData> mData;
    private String[] mTitles;
    private ViewPagerHolderCreate mViewPagerHolderCreate;

    public CommonViewPagerAdapter(List<ItemData> data, ViewPagerHolderCreate viewPagerHolderCreate) {
        mData = data;
        mViewPagerHolderCreate = viewPagerHolderCreate;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View contentView = getView(position, null, container);
        container.addView(contentView);
        return contentView;
    }

    private View getView(int position, View view, ViewGroup container) {
        ViewPagerHolder viewPagerHolder = null;
        int viewType = -1;
        if (mData != null && mData.size() > 0) {
            viewType = getViewType(position);
            if (view == null) {
                //回调方法
                viewPagerHolder = mViewPagerHolderCreate.CreateViewHolder();
                view = viewPagerHolder.createView(container.getContext(), viewType);
                view.setTag(VIEW_TAG, viewPagerHolder);
            } else {
                viewPagerHolder = (ViewPagerHolder) view.getTag(VIEW_TAG);
            }
            //绑定数据
            if (viewPagerHolder != null) {
                viewPagerHolder.bindView(container.getContext(), position, mData.get(position));
            }
        }
        return view;
    }

    /**
     * 根据数据类型返回应该加载的布局的类型
     *
     * @param position
     * @return
     */
    private int getViewType(int position) {
        switch (((ItemData) mData.get(position)).getViewType()) {
            case ItemData.NORMAL_DATA:
                return ItemData.NORMAL_DATA;
            case ItemData.DIFF_DATA:
                return ItemData.DIFF_DATA;
            default:
                return -1;

        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.length == 0 ? "null" : mTitles[position];
    }


    public void addData(List<ItemData> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addTitles(String[] titles) {
        mTitles = titles;

    }

    public interface ViewPagerHolder<T> {
        /**
         * 创建view
         *
         * @param context
         * @return
         */
        View createView(Context context, int ViewType);

        /**
         * 绑定数据
         *
         * @param context
         * @param position
         * @param data
         */
        void bindView(Context context, int position, T data);
    }

    public interface ViewPagerHolderCreate<VH extends ViewPagerHolder> {
        /**
         * 创建viewHolder
         *
         * @return
         */
        public VH CreateViewHolder();
    }

    public class ItemData {
        public static final int NORMAL_DATA = 0;
        public static final int DIFF_DATA = 1;
        private int ViewType;
        private T mMusic;

        public ItemData() {
        }

        public int getViewType() {
            return ViewType;
        }

        public void setViewType(int viewType) {
            ViewType = viewType;
        }

        public T getData() {
            return mMusic;
        }

        public void setData(T data) {
            mMusic = data;
        }

    }


}
