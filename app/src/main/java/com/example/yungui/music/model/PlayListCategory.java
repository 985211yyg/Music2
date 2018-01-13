package com.example.yungui.music.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by yungui on 2017/12/7.
 */

public class PlayListCategory implements MultiItemEntity{
    public static final int CategoryBig = 1;
    public static final int CategoryNormal = 2;
    public String mCategory;
    public int mItemType;

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public void setItemType(int mItemType) {
        this.mItemType = mItemType;
    }

    @Override
    public int getItemType() {
        return mItemType;
    }
}
