package com.example.yungui.music.adapter;

import android.support.annotation.Nullable;
import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.R;
import com.example.yungui.music.model.Music;

import java.util.List;

/**
 * Created by 22892 on 2018/1/29.
 */

public class MyRankingItemAdapter extends BaseQuickAdapter<Music, BaseViewHolder> {


    public MyRankingItemAdapter(int layoutResId, @Nullable List<Music> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Music item) {
        String first = "1 <font color='black'>" + item.getSong_name() + " - " + "</font>" +
                "<font color='gray'>" + item.getAuthor() + "</font>";
        helper.setText(R.id.ranging_item_first, Html.fromHtml(first));
        helper.setText(R.id.ranging_item_third, Html.fromHtml(first));
        helper.setText(R.id.ranking_item_second, Html.fromHtml(first));
    }
}
