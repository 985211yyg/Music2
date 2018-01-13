package com.example.yungui.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.yungui.music.R;
import com.example.yungui.music.activity.SongDetailActivity;
import com.example.yungui.music.event.FrontColorEvent;
import com.example.yungui.music.event.FrontSizeEvent;
import com.example.yungui.music.utils.PreferencesUtility;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by yungui on 2017/11/27.
 */

public class LrcFrontStyleDialog extends BottomSheetDialogFragment {

    private RadioGroup frontSize;
    private RadioGroup frontColor;
    private Button cancel;
    private SongDetailActivity songDetailActivity;
    private PreferencesUtility preferencesUtility;

    public static LrcFrontStyleDialog newInstance() {
        Bundle args = new Bundle();
        LrcFrontStyleDialog fragment = new LrcFrontStyleDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        songDetailActivity = (SongDetailActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lrc_front_style_dialog, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        preferencesUtility = PreferencesUtility.getInstance(getActivity());
        cancel = rootView.findViewById(R.id.lrc_frontSize_cancel);
        frontSize = rootView.findViewById(R.id.lrc_frontSize);
        frontColor = rootView.findViewById(R.id.lrc_frontColor);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        switch (preferencesUtility.getLrcFrontSize()) {
            case 12:
                frontSize.check(R.id.a12);
                break;
            case 14:
                frontSize.check(R.id.a14);
                break;
            case 16:
                frontSize.check(R.id.a16);
                break;
            case 18:
                frontSize.check(R.id.a18);
                break;
            case 20:
                frontSize.check(R.id.a20);
                break;
        }
        switch (preferencesUtility.getLrcFrontColor()) {
            case 1:
                frontColor.check(R.id.green);
                break;
            case 2:
                frontColor.check(R.id.blue);
                break;
            case 3:
                frontColor.check(R.id.yellow);
                break;
            case 4:
                frontColor.check(R.id.pink);
                break;
            case 5:
                frontColor.check(R.id.red);
                break;
        }
        frontSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FrontSizeEvent frontSizeEvent = new FrontSizeEvent();
                switch (checkedId) {
                    case R.id.a12:
                        frontSizeEvent.setFrontSize(12);
                        preferencesUtility.setLrcFrontSize(12);
                        break;
                    case R.id.a14:
                        frontSizeEvent.setFrontSize(14);
                        preferencesUtility.setLrcFrontSize(14);

                        break;
                    case R.id.a16:
                        frontSizeEvent.setFrontSize(16);
                        preferencesUtility.setLrcFrontSize(16);

                        break;
                    case R.id.a18:
                        frontSizeEvent.setFrontSize(18);
                        preferencesUtility.setLrcFrontSize(18);

                        break;
                    case R.id.a20:
                        frontSizeEvent.setFrontSize(20);
                        preferencesUtility.setLrcFrontSize(20);

                        break;
                }
                //发布事件
                EventBus.getDefault().post(frontSizeEvent);
            }
        });

        frontColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FrontColorEvent colorEvent = new FrontColorEvent();
                switch (checkedId) {
                    case R.id.green:
                        colorEvent.setFrontColor(R.color.green);
                        preferencesUtility.setLrcFrontColor(1);
                        break;
                    case R.id.blue:
                        colorEvent.setFrontColor(R.color.blue);
                        preferencesUtility.setLrcFrontColor(2);
                        break;
                    case R.id.yellow:
                        colorEvent.setFrontColor(R.color.yellow);
                        preferencesUtility.setLrcFrontColor(3);

                        break;
                    case R.id.pink:
                        colorEvent.setFrontColor(R.color.pink);
                        preferencesUtility.setLrcFrontColor(4);

                        break;
                    case R.id.red:
                        colorEvent.setFrontColor(R.color.red);
                        preferencesUtility.setLrcFrontColor(5);

                        break;
                }
                EventBus.getDefault().post(colorEvent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
