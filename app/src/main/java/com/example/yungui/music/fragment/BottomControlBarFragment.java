package com.example.yungui.music.fragment;


import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yungui.music.Interface.IConstants;
import com.example.yungui.music.R;
import com.example.yungui.music.activity.SongDetailActivity;
import com.example.yungui.music.adapter.BottomBarAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.modle.Song;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.utils.HorizontalPageLayoutManager;
import com.example.yungui.music.utils.MusicUtils;
import com.example.yungui.music.utils.PagingScrollHelper;
import com.example.yungui.music.widget.QQPlayButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
public class BottomControlBarFragment extends BaseFragment implements PagingScrollHelper.onPageChangeListener {
    private RecyclerView bottomBarRecycleView;
    private BottomBarAdapter bottomBarAdapter;
    private ImageView queue;
    private QQPlayButton qqPlayButton;
    private List<Song> songs = new ArrayList<>();
    private int lastIndex;
    public static final String TAG = BottomControlBarFragment.class.getSimpleName();
    private Disposable disposable;
    private Timer timer;

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            //播放位置
            long position = MusicPlayer.position();
            //总时长
            long duration = MusicPlayer.duration();

            //计算进度
            if (duration > 0) {
                float progress = position / duration * 100;
                qqPlayButton.setProgress(progress);
            }
        }
    };

    public BottomControlBarFragment() {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.bottom_control_bar;
    }

    @Override
    protected void initView() {
        qqPlayButton = rootView.findViewById(R.id.play_pause);
        queue = rootView.findViewById(R.id.music_queue);
        for (int i = 0; i < 15; i++) {
            songs.add(new Song());
        }
        bottomBarRecycleView = rootView.findViewById(R.id.bottom_bar_recycleView);
        bottomBarRecycleView.setLayoutManager(new HorizontalPageLayoutManager(1, 1));
        bottomBarAdapter = new BottomBarAdapter(R.layout.bottom_control_bar_content, null);
        bottomBarRecycleView.setAdapter(bottomBarAdapter);
        PagingScrollHelper scrollHelper = new PagingScrollHelper();
        scrollHelper.setUpRecycleView(bottomBarRecycleView);
        //设置页面滚动监听
        scrollHelper.setOnPageChangeListener(this);
        bottomBarAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, SongDetailActivity.class);
//                Pair first = new Pair<>(qqPlayButton,"song_detail_QQPlayButton");
                Pair second = new Pair<View, String>(rootView.findViewById(R.id.circle_cd), "shareView");
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),second);
//                startActivity(intent);
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((MainActivity) mContext, second).toBundle());
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

        qqPlayButton.setOnPlayAndPauseListener(new QQPlayButton.OnPlayOrPauseListener() {
            @Override
            public void play() {
                Toast.makeText(mContext, "正在播放", Toast.LENGTH_SHORT).show();
                if (MusicPlayer.getQueueSize() == 0) {
                    Toast.makeText(mContext, R.string.play_null, Toast.LENGTH_SHORT).show();
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.playOrPause();
                        }
                    }, 60);
//                    setProgress();//使用Timer计时器
                    UpdateProgress();//使用RxJava的计时器，定时轮询
                }
            }

            @Override
            public void pause() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                    }
                }, 60);
            }
        });

        queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
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

    private void setProgress() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //播放位置
                long position = MusicPlayer.position();
                //总时长
                long duration = MusicPlayer.duration();
                if (duration > 0 && position > 0) {
                    qqPlayButton.setProgress(100 * position / duration);
                }
                if (duration == position) {
                    timer.cancel();
                }
            }
        }, 0, 1000);

    }

    private void UpdateProgress() {
        qqPlayButton.setProgress(120);
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Float>() {
                    @Override
                    public Float apply(Long aLong) throws Exception {
                        Log.e(TAG, "apply: ");
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
                        Log.e(TAG, "onNext: " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");

                    }
                });
    }


    private void loadData() {
        Observable.fromIterable(MusicUtils.queryMusic(mContext, IConstants.START_FROM_LOCAL))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MusicInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MusicInfo value) {
                        bottomBarAdapter.addData(value);
                        bottomBarAdapter.notifyDataSetChanged();
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
    public void onPageChange(final int index) {
        Handler handler = new Handler();
        if (lastIndex != index) {
            qqPlayButton.UpdateStatue(false);
            //延时60ms
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.next();
                    lastIndex = index;
                    UpdateProgress();
                    qqPlayButton.UpdateStatue(true);
                }
            }, 60);
        }
    }

    @Override
    public void updateTrackInfo() {
        super.updateTrackInfo();
    }

    @Override
    public void updateTime() {
        super.updateTime();
    }


    @Override
    public void updateAdapter() {
        super.updateAdapter();
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
