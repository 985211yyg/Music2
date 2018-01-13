package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.LrcDialogRecyclerViewAdapter;
import com.example.yungui.music.model.LrcDialogData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/11/27.
 */

public class LrcDialog extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private Button cancel;
    private LrcDialogRecyclerViewAdapter lrcDialogRecyclerViewAdapter;
    private View rootView;
    private int[] imgID = new int[]{R.drawable.lrc_poster, R.drawable.lrc_front_size, R.drawable.lrc_progress, R.drawable.lrc_lrc,
            R.drawable.lrc_search, R.drawable.lrc_feedback};
    private String[] desc = new String[]{"歌词海报", "字体样式", "调整进度", "桌面歌词", "重新搜词", "反馈错误"};
    private List<LrcDialogData> lrcDialogDatas = new ArrayList<>();

    public static LrcDialog newInstance() {
        Bundle args = new Bundle();
        LrcDialog fragment = new LrcDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.lrc_dialog, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        recyclerView = rootView.findViewById(R.id.lrc_dialog_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        for (int i = 0; i < imgID.length; i++) {
            LrcDialogData dialogData = new LrcDialogData();
            dialogData.setImgID(imgID[i]);
            dialogData.setDesc(desc[i]);
            lrcDialogDatas.add(dialogData);
        }
        lrcDialogRecyclerViewAdapter = new LrcDialogRecyclerViewAdapter(R.layout.lrc_dialog_item, lrcDialogDatas);
        recyclerView.setAdapter(lrcDialogRecyclerViewAdapter);
        cancel = rootView.findViewById(R.id.lrc_dialog_cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LrcDialog.this.dismiss();
            }
        });
        lrcDialogRecyclerViewAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        dismiss();
                        LrcFrontStyleDialog.newInstance().show(getFragmentManager(),"front");
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;

                }
            }
        });
    }
}
