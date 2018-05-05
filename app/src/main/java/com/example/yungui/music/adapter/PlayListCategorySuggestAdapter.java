package com.example.yungui.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.R;
import com.example.yungui.music.model.PlayList;

import java.util.List;

/**
 * Created by 22892 on 2018/1/31.
 *
 *
 */

public class PlayListCategorySuggestAdapter extends BaseQuickAdapter<PlayList, BaseViewHolder> {
    public PlayListCategorySuggestAdapter(int layoutResId, @Nullable List<PlayList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayList item) {
        helper.setText(R.id.category_item_title, item.getTitle());

    }
}
