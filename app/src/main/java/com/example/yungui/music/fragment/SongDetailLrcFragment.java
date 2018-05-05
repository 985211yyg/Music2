package com.example.yungui.music.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yungui.lrcview.view.LyricView;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.event.FrontColorEvent;
import com.example.yungui.music.event.FrontSizeEvent;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.utils.LrcUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yungui on 2017/11/12.
 */

public class SongDetailLrcFragment extends BaseFragment {
    public static final String TAG = SongDetailLrcFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";

    @BindView(R.id.lrcView)
    LyricView lrcView;
    @BindView(R.id.microphone_btn)
    Button microphoneBtn;
    @BindView(R.id.lrc_btn)
    Button lrcBtn;

    private MediaControllerCompat mMediaControllerCompat;
    private Disposable mDisposable, mDisposable1;
    private boolean update;//歌词界面是否对用户可见的标志


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            update = true;
        } else {
            update = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mDisposable1 != null) {
            mDisposable1 = null;
        }
        if (mDisposable != null) {
            mDisposable = null;
        }

    }

    public SongDetailLrcFragment() {

    }

    public static SongDetailLrcFragment newInstance(int sectionNumber) {
        SongDetailLrcFragment fragment = new SongDetailLrcFragment();
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_song_detail_lrc;
    }


    @SuppressLint("ResourceType")
    @Override
    protected void initView(Bundle savedInstanceState) {
        lrcView = rootView.findViewById(R.id.lrcView);
        lrcBtn = rootView.findViewById(R.id.lrc_btn);
        try {
            lrcView.setLrcInfo(com.example.yungui.lrcview.utils.LrcUtils.newInstance()
                    .setupLrcResource(mContext.getResources().getAssets().open("田馥甄 - 魔鬼中的天使.lrc"), "UTF-8").getLrcInfo());
        } catch (IOException e) {
            e.printStackTrace();
        }

        lrcView.setPlayerClickListener(new LyricView.onIndicatorPlayerClickListener() {
            @Override
            public void onIndicatorPlayerClick(long l, int currentLine) {

            }
        });
        if (mMediaControllerCompat != null) {
//            lrcView.setMediaController(mMediaControllerCompat);
            mDisposable = Observable.interval(1, TimeUnit.MILLISECONDS)
                    .doOnNext(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            if (update && mMediaControllerCompat.getPlaybackState() != null
                                    && mMediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {

                                Observable.just(mMediaControllerCompat.getPlaybackState().getPosition())
                                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Long>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                mDisposable1 = d;
                                            }

                                            @Override
                                            public void onNext(Long value) {
                                                Log.e(TAG, "onNext: " + value);
                                                lrcView.setCurrentTime(value);
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            }
                        }
                    }).subscribeOn(Schedulers.io())
                    .subscribe();
        }
        lrcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lrcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LrcFrontStyleDialog.newInstance().show(getFragmentManager(), "LrcFrontStyleDialog");
            }
        });
    }

    @Override
    protected void loadData() {

    }


    /**
     * 订阅字体大小改变的事件
     *
     * @param frontSizeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFrontSizeEvent(FrontSizeEvent frontSizeEvent) {
        lrcView.setTextSize(frontSizeEvent.getFrontSize());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFrontColorEvent(FrontColorEvent frontColorEvent) {
        lrcView.setHeightLightColor(frontColorEvent.getFrontColor());
    }


    @OnClick({R.id.lrcView, R.id.microphone_btn, R.id.lrc_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lrcView:
                break;
            case R.id.microphone_btn:
                break;
            case R.id.lrc_btn:
                break;
        }
    }
}
