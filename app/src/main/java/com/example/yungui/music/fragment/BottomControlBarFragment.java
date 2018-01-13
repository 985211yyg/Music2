package com.example.yungui.music.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yungui.music.Interface.IConstants;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.BottomBarViewPagerAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.model.viewmodel.MusicInfoViewModel;
import com.example.yungui.music.model.viewmodel.MusicInfoViewModelFactory;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.utils.InjectorUtils;
import com.example.yungui.music.utils.MusicUtils;
import com.example.yungui.music.widget.QQPlayButton;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomControlBarFragment extends BaseFragment {
    @BindView(R.id.bottom_bar_viewPager)
    ViewPager viewPager;
    @BindView(R.id.music_queue)
    ImageView queue;
    @BindView(R.id.play_pause)
    QQPlayButton qqPlayButton;
    private BottomBarViewPagerAdapter bottomBarViewPagerAdapter;
    public static final String TAG = BottomControlBarFragment.class.getSimpleName();
    private Disposable disposable;
    private Timer timer;
    private int currentPosition = 0;
    private Handler handler = new Handler();
    private MusicInfoViewModel musicInfoViewModel;
    private MusicInfoViewModelFactory musicInfoViewModelFactory;


    public BottomControlBarFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicInfoViewModelFactory = InjectorUtils.provideMusicViewModelFactory(mContext);
        musicInfoViewModel = ViewModelProviders.of(this, musicInfoViewModelFactory).get(MusicInfoViewModel.class);
        musicInfoViewModel.getMusicInfo().observe(this, new android.arch.lifecycle.Observer<List<MusicInfo>>() {
            @Override
            public void onChanged(@Nullable List<MusicInfo> musicInfos) {
                bottomBarViewPagerAdapter.addData(musicInfos);
            }
        });
    }


    @Override
    protected int getLayoutID() {
        return R.layout.bottom_control_bar;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        bottomBarViewPagerAdapter = new BottomBarViewPagerAdapter(getFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(bottomBarViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "onPageSelected: " + position);
                if (position > currentPosition) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.next();
                        }
                    }, 300);
                } else if (position < currentPosition) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.previous(mContext, true);
                        }
                    }, 300);
                }
                currentPosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        QueueFragment queueFragment = new QueueFragment();
                        queueFragment.show(getChildFragmentManager(), "tag");
                    }
                }, 100);
            }
        });
        loadData();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void UpdateProgress() {
        Observable.interval(1, TimeUnit.SECONDS)
                .map(new Function<Long, Float>() {
                    @Override
                    public Float apply(Long aLong) throws Exception {
                        return (float) 100 * MusicPlayer.position() / MusicPlayer.duration();
                    }
                }).takeUntil(new Predicate<Float>() {
            @Override
            public boolean test(Float progress) throws Exception {
                return progress == 100;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Float>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Float value) {
                        qqPlayButton.setProgress(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void loadData() {

    }




    @Override
    public void onResume() {
        super.onResume();
        if (MusicPlayer.isPlaying()) {
            UpdateProgress();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }
}
