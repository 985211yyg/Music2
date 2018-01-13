package com.example.yungui.music.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

/**
 * Created by yungui on 2017/11/28.
 */

public class SongDetailMoreFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private Button cancel;

    public static SongDetailMoreFragment newInstance() {
        Bundle args = new Bundle();
        SongDetailMoreFragment fragment = new SongDetailMoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_detail_more, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        cancel = rootView.findViewById(R.id.song_detail_more_cancel);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.song_detail_more_cancel:
                dismiss();
                break;
        }

    }
}
