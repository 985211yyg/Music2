package com.example.yungui.music.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.example.yungui.music.R;
import com.example.yungui.music.model.PlayListCategory;

import java.util.List;

/**
 * Created by yungui on 2017/12/7.
 */

public class PlayListCategoryAdapter extends BaseMultiItemQuickAdapter<PlayListCategory, BaseViewHolder> {
    private SuperTextView superTextView;

    public PlayListCategoryAdapter(List<PlayListCategory> data) {
        super(data);
        addItemType(PlayListCategory.CategoryBig, R.layout.text_item_big);
        addItemType(PlayListCategory.CategoryNormal, R.layout.text_item_normal);
    }


    @Override
    protected void convert(BaseViewHolder helper, PlayListCategory item) {
        helper.setText(R.id.category, item.mCategory);
        superTextView = helper.getView(R.id.category);
        if (helper.getAdapterPosition() > 0 && helper.getAdapterPosition() < 3) {
            superTextView.setShowState(true);
        }
    }
}
