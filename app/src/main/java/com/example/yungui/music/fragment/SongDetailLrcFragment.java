package com.example.yungui.music.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.yungui.lrcview.bean.LrcInfo;
import com.example.yungui.lrcview.utils.LrcUtils;
import com.example.yungui.lrcview.view.LyricView;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.service.MusicPlayer;

import java.io.IOException;
import java.io.InputStream;
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
 * Created by yungui on 2017/11/12.
 */

public class SongDetailLrcFragment extends BaseFragment {
    public static final String TAG = SongDetailLrcFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";
    private LrcInfo lrcInfo;
    private LyricView lyricView;
    private Button lrcBtn;
    private long currentAudioId;
    private Timer timer;
    private int i = 0;
    private long oldTime;

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
    protected void initView() {
        lyricView = rootView.findViewById(R.id.lrcView);
        lrcBtn = rootView.findViewById(R.id.lrc_btn);
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

        Observable.interval(1, TimeUnit.SECONDS)
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
                        Log.e(TAG, "onNext: " + value);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        lyricView.setPlayerClickListener(new LyricView.onPlayerClickListener() {
            @Override
            public void onPlayerClick(long l, String s) {
                MusicPlayer.seek(l);
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
