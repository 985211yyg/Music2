package com.example.yungui.music.fragment;


import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.yungui.music.R;
import com.example.yungui.music.adapter.BottomBarViewPagerAdapter;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.model.viewmodel.MusicInfoViewModel;
import com.example.yungui.music.model.viewmodel.MusicInfoViewModelFactory;
import com.example.yungui.music.utils.InjectorUtils;
import com.example.yungui.music.widget.QQPlayButton;

import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomControlBarFragment extends BaseFragment {
    public static final String TAG = BottomControlBarFragment.class.getSimpleName();
    public static final String Fragment_Tag = "BottomControlBarFragment";

    @BindView(R.id.bottom_bar_viewPager)
    ViewPager viewPager;
    @BindView(R.id.music_queue)
    ImageView queue;
    @BindView(R.id.play_pause)
    QQPlayButton qqPlayButton;

    private BottomBarViewPagerAdapter bottomBarViewPagerAdapter;
    private Disposable disposable;
    private int currentPosition = 0;
    private Handler handler = new Handler();
    private MusicInfoViewModel musicInfoViewModel;
    private MusicInfoViewModelFactory musicInfoViewModelFactory;
    private MediaControllerCompat mControllerCompat;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicInfoViewModelFactory = InjectorUtils.provideMusicViewModelFactory(mContext);
        musicInfoViewModel = ViewModelProviders.of(this, musicInfoViewModelFactory)
                .get(MusicInfoViewModel.class);
        musicInfoViewModel.getMusicInfo()
                .observe(this, new android.arch.lifecycle.Observer<List<MusicInfo>>() {
                    @Override
                    public void onChanged(@Nullable List<MusicInfo> musicInfos) {
                        bottomBarViewPagerAdapter.addData(musicInfos);
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        mControllerCompat = MediaControllerCompat.getMediaController(getActivity());
        qqPlayButton.setMediaController(mControllerCompat);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        qqPlayButton.disconnectController();
        if (mControllerCompat != null) {
            mControllerCompat = null;
        }

    }

    public static BottomControlBarFragment newInstance() {
        Bundle args = new Bundle();
        BottomControlBarFragment fragment = new BottomControlBarFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutID() {
        return R.layout.bottom_control_bar;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        bottomBarViewPagerAdapter = new BottomBarViewPagerAdapter(getFragmentManager());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(bottomBarViewPagerAdapter);
        viewPager.addOnPageChangeListener(new MyPageChangeListener());
        queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueueFragment queueFragment = new QueueFragment();
                queueFragment.show(getChildFragmentManager(), "tag");
            }
        });
    }


    @Override
    protected void loadData() {

    }


    @Override
    public void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat) {
        super.onPlayBackServiceConnected(mediaControllerCompat);
        Log.e(TAG, "onPlayBackServiceConnected: ");
        //这不能使用MediaControllerCompat.getMediaController(),以为该fragment在初始化的时候已经添加，
        //在连接服务成功后会立刻收到回调，而且setMediaController还没有你设置
        mControllerCompat = mediaControllerCompat;
        qqPlayButton.setMediaController(mediaControllerCompat);
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
        super.onMetadataChanged(mediaMetadataCompat);
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
        super.onPlaybackStateChanged(playbackStateCompat);
    }

    @Override
    public void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems) {
        super.onMediaItemsLoaded(mediaItems);
    }

    //===================viewpager页面滚动回调=================================
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (currentPosition <= position) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mControllerCompat.getTransportControls().skipToNext();
                    }
                }, 200);

            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mControllerCompat.getTransportControls().skipToPrevious();
                    }
                }, 200);
            }
            currentPosition = position;

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
