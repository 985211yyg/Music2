package com.example.yungui.music.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mpointindicator.PointIndicator;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.SectionsPagerAdapter;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.fragment.SongDetailAboutFragment;
import com.example.yungui.music.fragment.SongDetailAlbumFragment;
import com.example.yungui.music.fragment.SongDetailLrcFragment;
import com.example.yungui.music.pagerTransformer.QQMusicTransformer;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.widget.QQPlayButton;
import com.example.yungui.timertextview.TimerTextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * OVERLAY  todo 需要修复LyricView与 ViewPager滑动冲突导致进入页面显示指示器，以及左右又滑动显示指示器
 *          todo 修改TimerTextView 更新时duration值为负的计算错误
 *
 */

public class SongDetailActivity extends BaseActivity implements QQPlayButton.OnPlayOrPauseListener,
        SeekBar.OnSeekBarChangeListener, TimerTextView.OnTimingListener {


    private AppBarLayout head;
    private LinearLayout bottom;
    private SeekBar seekBar;
    private PointIndicator pointIndicator;
    private QQPlayButton qqPlayButton;
    private TextView totalTime;
    private TimerTextView timerTextView;
    private Timer timer;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager viewPager;
    private ViewGroup rootView;
    private Disposable disposable1, disposable2;


    @Override
    protected void setTransition() {
//        Transition transition = TransitionInflater
//                .from(SongDetailActivity.this)
//                .inflateTransition(R.transition.transition_enter);
////        transition.excludeTarget(android.R.id.statusBarBackground, true);
//
//        Slide slide = new Slide();
//        slide.setSlideEdge(Gravity.BOTTOM);
//        slide.setDuration(100);
//        slide.excludeTarget(android.R.id.statusBarBackground, true);
//
////        getWindow().setEnterTransition(transition);
//        getWindow().setEnterTransition(new Explode());
//        getWindow().setExitTransition(new Explode());
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_song_detail;
    }

    @Override
    protected int getMenuID() {
        return R.menu.menu_song_detail;
    }

    @Override
    protected boolean setToolBar() {
        return false;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        seekBar = findViewById(R.id.song_detail_seekBar);
        pointIndicator = findViewById(R.id.pointIndicator);
        qqPlayButton = findViewById(R.id.song_detail_QQPlayButton);
        totalTime = findViewById(R.id.total_time);
        timerTextView = findViewById(R.id.current_time);
        head = findViewById(R.id.appbar);
        bottom = findViewById(R.id.bottom_control);
        rootView = findViewById(R.id.main_content);
        qqPlayButton.setOnPlayAndPauseListener(this);
        viewPager = rootView.findViewById(R.id.song_detail_viewPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSectionsPagerAdapter.addFragments(SongDetailAboutFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailAlbumFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailLrcFragment.newInstance(0));

        viewPager.setAdapter(mSectionsPagerAdapter);
        pointIndicator.bindViewPager(viewPager);
        viewPager.setCurrentItem(1);//显示中间
        viewPager.setPageTransformer(false, new QQMusicTransformer());

        seekBar.setOnSeekBarChangeListener(this);
        totalTime.setText(calculateTime(MusicPlayer.duration()));
        if (MusicPlayer.isPlaying()) {
            qqPlayButton.UpdateStatue(true);
            timerTextView.timing(0,MusicPlayer.duration(), TimerTextView.TimeUnit.MilliSecond);
            timerTextView.start();
            updateTime();
        }

    }


    private String calculateTime(float time) {
        StringBuilder builder = new StringBuilder();
        int second = (int) (time / 1000 % 60);//化为秒，在化为分钟，不满一分钟的部分就是秒
        int minute = (int) (time / (1000 * 60) % 60);
        builder.append(0);
        builder.append(minute);
        builder.append(":");
        builder.append(second);
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_song_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicPlayer.isPlaying()) {
            updateTime();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * QQplayButton的状态回调
     */
    @Override
    public void play() {
        MusicPlayer.playOrPause();
        if (MusicPlayer.isPlaying()) {
            updateTime();
            timerTextView.timing(0,MusicPlayer.duration(), TimerTextView.TimeUnit.MilliSecond);
            timerTextView.start();
        }
    }

    @Override
    public void pause() {
        MusicPlayer.playOrPause();
    }

    public void updateTime() {
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Observable
                                .just(MusicPlayer.position())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        disposable1 = d;
                                    }

                                    @Override
                                    public void onNext(Long value) {
                                        Log.e(TAG, ">>>>>>>>onNext: "+value );
//                                        timerTextView.updateTime(value, TimerTextView.TimeUnit.MilliSecond);
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
                if (disposable1.isDisposed()) {
                    disposable1.dispose();
                    disposable1 = null;
                }

            }
        });
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //播放位置
//                long position = MusicPlayer.position();
//                //总时长
//                long duration = MusicPlayer.duration();
//                if (duration > 0 && position > 0) {
//                    seekBar.setProgress((int) (100 * position / duration));
//                }
//                if (duration == position) {
//                    timer.cancel();
//                }
//            }
//        }, 0, 500);


    }

    /**
     * seekBar状态监听回调
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (MusicPlayer.isPlaying()) {
            StringBuilder builder = new StringBuilder();
            builder.append(0);
            //当前时间
            int second = (int) (((MusicPlayer.duration() / 100 * progress)) / 1000 % 60);
            int minute = (int) ((MusicPlayer.duration() / 100 * progress) / (1000 * 60) % 60);
            builder.append(minute);
            builder.append(":");
            if (second < 10) {
                builder.append(0);
            }
            builder.append(second);
            timerTextView.setText(builder.toString());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 导航按钮被点击
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!bottom.isTransitionGroup()) {
            bottom.setTransitionGroup(true);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!disposable2.isDisposed()) {
            disposable2.dispose();
            disposable2 = null;
        }
    }

    /**
     * 计时TextView的回调
     */
    @Override
    public void timingCompleted() {

    }

    @Override
    public void timeUpdate(int i) {
        seekBar.setProgress((int) (100 * i / MusicPlayer.duration()));
    }
}
