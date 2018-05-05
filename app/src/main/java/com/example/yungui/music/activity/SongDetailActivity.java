package com.example.yungui.music.activity;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mpointindicator.PointIndicator;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.SectionsPagerAdapter;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.fragment.SongDetailAboutFragment;
import com.example.yungui.music.fragment.SongDetailAlbumFragment;
import com.example.yungui.music.fragment.SongDetailLrcFragment;
import com.example.yungui.music.fragment.SongDetailMoreFragment;
import com.example.yungui.music.pagerTransformer.QQMusicTransformer;
import com.example.yungui.music.service.MediaBrowserAdapter;
import com.example.yungui.music.widget.MediaSeekBar;
import com.example.yungui.music.widget.QQPlayButton;
import com.example.yungui.timertextview.TimerTextView;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongDetailActivity extends BaseActivity {

    @BindView(R.id.song_detail_SongName)
    TextView mSongDetailSongName;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.song_detail_appbar)
    AppBarLayout mSongDetailAppbar;
    @BindView(R.id.song_detail_viewPager)
    ViewPager mSongDetailViewPager;
    @BindView(R.id.pointIndicator)
    PointIndicator mPointIndicator;
    @BindView(R.id.song_detail_current_time)
    TimerTextView mSongDetailCurrentTime;
    @BindView(R.id.song_detail_seekBar)
    MediaSeekBar mSongDetailSeekBar;
    @BindView(R.id.song_detail_total_time)
    TextView mSongDetailTotalTime;
    @BindView(R.id.song_detail_previous)
    ImageView mSongDetailPreviou;
    @BindView(R.id.song_detail_QQPlayButton)
    QQPlayButton mSongDetailQQPlayButton;
    @BindView(R.id.song_detail_next)
    ImageView mSongDetailNext;
    @BindView(R.id.bottom_control)
    LinearLayout mBottomControl;
    @BindView(R.id.main_content)
    CoordinatorLayout mMainContent;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MediaBrowserAdapter.MediaBrowserChangeForUIListener changeForUIListener;


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mSongDetailNext = findViewById(R.id.song_detail_next);
        mSongDetailPreviou = findViewById(R.id.song_detail_previous);
        mSongDetailNext.setOnClickListener((v -> {
            mMediaBrowserAdapter.getTransportControls().skipToNext();
        }));
        mSongDetailPreviou.setOnClickListener(v -> {
            mMediaBrowserAdapter.getTransportControls().skipToPrevious();
        });
        mMainContent.setBackground(new BitmapDrawable(EasyBlur.with(this)
                .bitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.images))
                .radius(25)
                .scale(8)
                .blur()));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragments(SongDetailAboutFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailAlbumFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailLrcFragment.newInstance(0));
        mSongDetailViewPager.setAdapter(mSectionsPagerAdapter);
        mPointIndicator.bindViewPager(mSongDetailViewPager);
        mSongDetailViewPager.setCurrentItem(1);//显示中间
        mSongDetailViewPager.setOffscreenPageLimit(3);
        mSongDetailViewPager.setPageTransformer(false, new QQMusicTransformer());
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_song_detail;
    }


    @Override
    public boolean setToolBar() {
        return false;
    }

    @Override
    public void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat) {
        mSongDetailSeekBar.setMediaController(mediaControllerCompat);
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {

    }

    @Override
    public void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_song_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.song_detail_more:
                SongDetailMoreFragment.newInstance().show(getSupportFragmentManager(), "moreDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.song_detail_previous, R.id.song_detail_QQPlayButton, R.id.song_detail_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.song_detail_previous:
                break;
            case R.id.song_detail_QQPlayButton:
                break;
            case R.id.song_detail_next:
                break;
        }
    }


    //=========导航按钮被点击=========
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
