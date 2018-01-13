package com.example.yungui.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.R;
import com.example.yungui.music.model.LrcDialogData;

import java.util.List;

/**
 * Created by yungui on 2017/11/16.
 */

public class LrcDialogRecyclerViewAdapter extends BaseQuickAdapter<LrcDialogData, BaseViewHolder> {

    public LrcDialogRecyclerViewAdapter(int layoutResId, @Nullable List<LrcDialogData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LrcDialogData item) {
        helper.setText(R.id.lrc_dialog_item_tv, item.getDesc());
        helper.setImageResource(R.id.lrc_dialog_item_iv, item.getImgID());
    }
}
