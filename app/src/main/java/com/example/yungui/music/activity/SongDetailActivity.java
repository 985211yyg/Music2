package com.example.yungui.music.activity;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mpointindicator.PointIndicator;
import com.example.yungui.music.MainApplication;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.SectionsPagerAdapter;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.fragment.LrcDialog;
import com.example.yungui.music.fragment.SongDetailAboutFragment;
import com.example.yungui.music.fragment.SongDetailAlbumFragment;
import com.example.yungui.music.fragment.SongDetailLrcFragment;
import com.example.yungui.music.fragment.SongDetailMoreFragment;
import com.example.yungui.music.model.MusicLibrary;
import com.example.yungui.music.model.Song;
import com.example.yungui.music.pagerTransformer.QQMusicTransformer;
import com.example.yungui.music.service.MediaBrowserAdapter;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.widget.MediaSeekBar;
import com.example.yungui.music.widget.QQPlayButton;
import com.example.yungui.timertextview.TimerTextView;
import com.zhouwei.blurlibrary.EasyBlur;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SongDetailActivity extends BaseActivity implements SongDetailLrcFragment.OnFragmentInteractionListener {
    AppBarLayout appbar;
    ViewPager songDetailViewPager;
    MediaSeekBar seekBar;
    TextView totalTime, songName;
    LinearLayout bottomControl;
    CoordinatorLayout mainContent;
    private ImageView previous, next;
    private PointIndicator pointIndicator;
    private QQPlayButton qqPlayButton;
    private TimerTextView timerTextView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private float rotate;
    private boolean isPlaying;

    private MediaBrowserAdapter mediaBrowserAdapter;
    private MediaBrowserAdapter.MediaBrowserChangeForUIListener changeForUIListener;
    private MediaControllerCompat.TransportControls transportControls;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowserAdapter = new MediaBrowserAdapter(this);
        changeForUIListener = new MyMediaBrowserChangeForUIListener();
        mediaBrowserAdapter.addListener(changeForUIListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaBrowserAdapter.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaBrowserAdapter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaBrowserAdapter.removeListener(changeForUIListener);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rotate = getIntent().getFloatExtra("rotate", 0);
        appbar = findViewById(R.id.song_detail_appbar);
        songDetailViewPager = findViewById(R.id.song_detail_viewPager);
        seekBar = findViewById(R.id.song_detail_seekBar);
        totalTime = findViewById(R.id.song_detail_total_time);
        songName = findViewById(R.id.song_detail_SongName);
        pointIndicator = findViewById(R.id.pointIndicator);
        bottomControl = findViewById(R.id.bottom_control);
        mainContent = findViewById(R.id.main_content);
        qqPlayButton = findViewById(R.id.song_detail_QQPlayButton);
        timerTextView = findViewById(R.id.song_detail_current_time);
        qqPlayButton.setOnClickListener(new QQPlayButtonClickListener());
        next = findViewById(R.id.song_detail_next);
        previous = findViewById(R.id.song_detail_previous);
        next.setOnClickListener((v -> {
            mediaBrowserAdapter.getTransportControls().skipToNext();
        }));
        previous.setOnClickListener(v -> {
            mediaBrowserAdapter.getTransportControls().skipToPrevious();
        });
        mainContent.setBackground(new BitmapDrawable(EasyBlur.with(this)
                .bitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.images))
                .radius(25)
                .scale(8)
                .blur()));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragments(SongDetailAboutFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailAlbumFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailLrcFragment.newInstance(0));
        songDetailViewPager.setAdapter(mSectionsPagerAdapter);
        pointIndicator.bindViewPager(songDetailViewPager);
        songDetailViewPager.setCurrentItem(1);//显示中间
        songDetailViewPager.setOffscreenPageLimit(3);
        songDetailViewPager.setPageTransformer(false, new QQMusicTransformer());
        seekBar.setOnSeekBarChangeListener(new MySeekBarChangeListener());
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_song_detail;
    }

    @Override
    protected int getMenuID() {
        return 0;
    }

    @Override
    protected boolean setToolBar() {
        return true;
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


    public void updateTime() {
        Observable.interval(500, TimeUnit.MILLISECONDS).doOnNext(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Observable.just(MusicPlayer.position())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Long value) {
                                float progress = (float) value / MusicPlayer.duration() * 100;
                                seekBar.setProgress((int) progress);
                                timerTextView.updateTime(value, TimerTextView.TimeUnit.MilliSecond);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long value) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }


    //===============================自定义回调======================
    private class MyMediaBrowserChangeForUIListener extends MediaBrowserAdapter.MediaBrowserChangeForUIListener {
        @Override
        public void onConnected(@NonNull MediaControllerCompat mediaControllerCompat) {
            Log.e(TAG, "onConnected: ");
            seekBar.setMediaController(mediaControllerCompat);

        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            if (isPlaying) {
                songName.setText(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
//                totalTime.setText((int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
//                timerTextView.timing(0, mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
//                        TimerTextView.TimeUnit.MilliSecond);
//                timerTextView.startAndPause();
            }

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            Log.e(TAG, "onPlaybackStateChanged: ");
            isPlaying = playbackStateCompat != null &&
                    playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING;

        }
    }

    //==================播放按钮点击事件=====================
    private class QQPlayButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isPlaying) {
                mediaBrowserAdapter.getTransportControls().play();
            } else {
                mediaBrowserAdapter.getTransportControls().pause();
            }

        }
    }


    //================seekBar的改变事件===============
    private class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    //=========导航按钮被点击=========
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    //与fragment进行交互，歌词按钮被点击了
    @Override
    public void onFragmentInteraction(View view) {
        LrcDialog.newInstance().show(getSupportFragmentManager(), "lrcDialog");
    }


}
