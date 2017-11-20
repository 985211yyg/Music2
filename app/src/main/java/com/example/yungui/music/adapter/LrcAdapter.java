package com.example.yungui.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.R;

import java.util.List;

/**
 * Created by yungui on 2017/11/16.
 */

public class LrcAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public LrcAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.lrc_tv, item);
    }
}
