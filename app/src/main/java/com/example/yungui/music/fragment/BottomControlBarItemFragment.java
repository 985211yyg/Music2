package com.example.yungui.music.fragment;


import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.transition.ChangeBounds;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.example.yungui.music.MainFragment;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.utils.FragmentUtils;
import com.example.yungui.music.widget.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BottomControlBarItemFragment extends BaseFragment {
    public static final String TAG = BottomControlBarItemFragment.class.getSimpleName();
    private static final String DATA = "data";
    @BindView(R.id.bottom_bar_item_song_name)
    TextView songName;
    @BindView(R.id.bottom_bar_item_singer)
    TextView singer;
    @BindView(R.id.circle_CD)
    CircleImageView circleImageView;

    private MediaControllerCompat mMediaControllerCompat;
    private MusicInfo musicInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //视图可见时回调
    @Override
    public void onResume() {
        super.onResume();
        mMediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaControllerCompat != null) {
            mMediaControllerCompat = null;
            circleImageView.disconnectController();
        }
        if (musicInfo != null) {
            musicInfo = null;
        }
    }

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
        circleImageView.setImageBitmap(ImageUtils.toRound(BitmapFactory
                .decodeResource(mContext.getResources(), R.mipmap.images)));
        if (musicInfo != null) {
            songName.setText(musicInfo.musicName);
            singer.setText(musicInfo.artist);
        }
    }

    @OnClick(R.id.song_item)
    public void SongItemClick(View view) {
        PlayDetailFragment playDetailFragment = PlayDetailFragment.newInstance();
        playDetailFragment.setSharedElementEnterTransition(new ChangeBounds());
        addToMainContent(playDetailFragment, PlayDetailFragment.Fragment_Tag, MainFragment.Fragment_Tag);

    }

    private void addToMainContent(Fragment showFragment, String showTag, String hideTag) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .hide(this)
                .hide(getActivity().getSupportFragmentManager().findFragmentByTag(BottomControlBarFragment.Fragment_Tag))
                .add(R.id.root_content, showFragment, showTag)
                .addSharedElement(circleImageView, "cd")
                .addToBackStack(hideTag)
                .commit();
    }

    @Override
    protected void loadData() {

    }

    private int maxDegree;

    public int getMaxDegree() {
        return maxDegree;
    }

    public void setMaxDegree(int maxDegree) {
        this.maxDegree = maxDegree;
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
        Log.e(TAG, "onMetadataChanged: ");
        if (mediaMetadataCompat != null) {
            circleImageView.setRotation(0);
            setMaxDegree((int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        }
    }

    private ValueAnimator mValueAnimator;

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
        Log.e(TAG, "onPlaybackStateChanged: ");
//        if (playbackStateCompat != null && playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//            restAnimator();
//            int degree = (int) playbackStateCompat.getPosition();
//            circleImageView.setRotation(degree);
//            Log.e(TAG, ">>>>>>>>>>>>>>>onPlaybackStateChanged: 设置动画");
//            int time = (getMaxDegree() - degree);
//            mValueAnimator = ValueAnimator.ofInt(0, 360).setDuration(8000);
//            mValueAnimator.setInterpolator(new LinearInterpolator());
//            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int degree = (int) animation.getAnimatedValue();
//                    circleImageView.setRotation(degree);
//                }
//            });
//            mValueAnimator.start();
//        } else if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
//            mValueAnimator.pause();
//        }

    }


    private void restAnimator() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }


}
