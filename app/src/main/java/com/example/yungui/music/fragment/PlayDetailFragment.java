package com.example.yungui.music.fragment;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mpointindicator.PointIndicator;
import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.SectionsPagerAdapter;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.pagerTransformer.QQMusicTransformer;
import com.example.yungui.music.widget.MediaSeekBar;
import com.example.yungui.music.widget.QQPlayButton;
import com.example.yungui.timertextview.TimerTextView;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayDetailFragment extends BaseFragment {
    public static final String TAG = "PlayDetailFragment";
    public static String Fragment_Tag = "PlayDetailFragment";
    @BindView(R.id.song_detail_SongName)
    TextView songName;
    @BindView(R.id.song_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.song_detail_appbar)
    AppBarLayout appbar;
    @BindView(R.id.song_detail_viewPager)
    ViewPager viewPager;
    @BindView(R.id.pointIndicator)
    PointIndicator pointIndicator;
    @BindView(R.id.song_detail_current_time)
    TimerTextView currentTime;
    @BindView(R.id.song_detail_seekBar)
    MediaSeekBar seekBar;
    @BindView(R.id.song_detail_total_time)
    TextView totalTime;
    @BindView(R.id.song_detail_previous)
    ImageView previous;
    @BindView(R.id.song_detail_QQPlayButton)
    QQPlayButton qqPlayButton;
    @BindView(R.id.song_detail_next)
    ImageView next;
    @BindView(R.id.bottom_control)
    LinearLayout bottomControl;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MediaControllerCompat mediaControllerCompat;
    private MediaControllerCompat.Callback mCallback;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
        mCallback = new MyMediaControllerCallback();
        mediaControllerCompat.registerCallback(mCallback);
        qqPlayButton.setMediaController(mediaControllerCompat);
        seekBar.setMediaController(mediaControllerCompat);
        if (mediaControllerCompat.getMetadata() != null) {
            songName.setText(mediaControllerCompat.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        seekBar.disconnectController();
        qqPlayButton.disconnectController();
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(mCallback);
            mediaControllerCompat = null;
            mCallback = null;
        }
    }

    public static PlayDetailFragment newInstance() {
        PlayDetailFragment fragment = new PlayDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_paly_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mSectionsPagerAdapter.addFragments(SongDetailAboutFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailAlbumFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailLrcFragment.newInstance(0));
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setCurrentItem(1);//显示中间
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(false, new QQMusicTransformer());
        pointIndicator.bindViewPager(viewPager);
        mainContent.setBackground(new BitmapDrawable(EasyBlur.with(mContext)
                .bitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.images))
                .radius(25)
                .scale(8)
                .blur()));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_song_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                break;
            case R.id.song_detail_more:
                SongDetailMoreFragment
                        .newInstance()
                        .show(getActivity().getSupportFragmentManager(), "moreDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void loadData() {

    }

    @OnClick({R.id.song_detail_previous, R.id.song_detail_next, R.id.song_detail_QQPlayButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.song_detail_previous:
                Log.e(TAG, "onViewClicked: 上一首");
                mediaControllerCompat.getTransportControls().skipToPrevious();
                break;
            case R.id.song_detail_next:
                Log.e(TAG, "onViewClicked: 下一首");
                mediaControllerCompat.getTransportControls().skipToNext();
                break;
        }
    }

    /**
     * 回调接口
     */
    private class MyMediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.e(TAG, "onSessionDestroyed: ");
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            Log.e(TAG, "onPlaybackStateChanged: ");
            try {
                Log.e(TAG, "播放位置: " + state.getPosition());
            } catch (RuntimeException e) {
                Log.e(TAG, "onPlaybackStateChanged: RuntimeException" + e.toString());

            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            Log.e(TAG, "onMetadataChanged: ");
            songName.setText(mediaControllerCompat.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        }
    }
}
