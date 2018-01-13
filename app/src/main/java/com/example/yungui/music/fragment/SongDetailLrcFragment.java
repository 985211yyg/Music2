package com.example.yungui.music.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yungui.lrcview.bean.LrcInfo;
import com.example.yungui.lrcview.utils.LrcUtils;
import com.example.yungui.lrcview.view.LyricView;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.event.DialogEvent;
import com.example.yungui.music.event.FrontColorEvent;
import com.example.yungui.music.event.FrontSizeEvent;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.utils.PreferencesUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

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

    private OnFragmentInteractionListener listener;
    private LrcInfo lrcInfo;
    private LyricView lyricView;
    private Button lrcBtn;
    private long currentAudioId;
    private Timer timer;
    private int i = 0;
    private long oldTime;
    private int size = 16;


    public SongDetailLrcFragment() {

    }

    public static SongDetailLrcFragment newInstance(int sectionNumber) {
        SongDetailLrcFragment fragment = new SongDetailLrcFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_song_detail_lrc;
    }


    @SuppressLint("ResourceType")
    @Override
    protected void initView(Bundle savedInstanceState) {
        lyricView = rootView.findViewById(R.id.lrcView);
        lrcBtn = rootView.findViewById(R.id.lrc_btn);
        if (MusicPlayer.isPlaying()) {
            updateLrc(lyricView);
        }
        loadLrc(lyricView);

        lrcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFragmentInteraction(v);
            }
        });
        lyricView.setPlayerClickListener(new LyricView.onIndicatorPlayerClickListener() {
            @Override
            public void onIndicatorPlayerClick(long l, int currentLine) {
                MusicPlayer.seek(l);
                lyricView.setCurrentPlayLine(currentLine);
            }
        });
        lyricView.setOnClickListener(new LyricView.OnClickListener() {
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
        lyricView.setTextSize(frontSizeEvent.getFrontSize());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFrontColorEvent(FrontColorEvent frontColorEvent) {
        lyricView.setHeightLightColor(frontColorEvent.getFrontColor());
    }

    private void updateLrc(final LyricView lyricView) {
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .doOnNext(new Consumer<Long>() {
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
                                        lyricView.setCurrentTime(value);
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }
                })
                .subscribe(new Observer<Long>() {
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

    private void loadLrc(final LyricView lyricView) {
        new Thread() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = mContext.getResources().getAssets().open("田馥甄 - 魔鬼中的天使.lrc");
                    lrcInfo = LrcUtils.newInstance()
                            .setupLrcResource(inputStream, "UTF-8")
                            .getLrcInfo();
                    if (lrcInfo != null && lrcInfo.lineInfos != null && lrcInfo.lineInfos.size() > 0) {
                        lyricView.setLrcInfo(lrcInfo);
                    }
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
        if (MusicPlayer.isPlaying()) {
            updateLrc(lyricView);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 与activity的交互接口
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(View view);
    }

}
