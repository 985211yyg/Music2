package com.example.yungui.music.fragment;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.example.yungui.music.R;
import com.example.yungui.music.activity.SongDetailActivity;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.widget.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;

public class BottomControlBarItemFragment extends BaseFragment {
    public static final String TAG = BottomControlBarItemFragment.class.getSimpleName();
    private static final String DATA = "data";
    @BindView(R.id.bottom_bar_item_song_name)
    TextView songName;
    @BindView(R.id.bottom_bar_item_singer)
    TextView singer;
    CircleImageView circleImageView;
    private MusicInfo musicInfo;



    public BottomControlBarItemFragment() {
    }

    public static BottomControlBarItemFragment newInstance(MusicInfo musicInfo) {
        BottomControlBarItemFragment fragment = new BottomControlBarItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, (Parcelable) musicInfo);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_bottom_control_bar_item;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        musicInfo = getArguments().getParcelable(DATA);
        circleImageView = rootView.findViewById(R.id.circle_CD);
        circleImageView.setImageBitmap(ImageUtils.toRound(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.images)));
        if (musicInfo != null) {
            songName.setText(musicInfo.musicName);
            singer.setText(musicInfo.artist);
        }

    }

    @OnClick(R.id.song_item)
    public void SongItemClick(View view) {
        Intent intent = new Intent(mContext, SongDetailActivity.class);
        intent.putExtra("rotate", circleImageView.getCurrentRotateValue());
        Pair second = new Pair<View, String>(rootView.findViewById(R.id.circle_CD), "shareView");
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), second);
        startActivity(intent, activityOptionsCompat.toBundle());

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
