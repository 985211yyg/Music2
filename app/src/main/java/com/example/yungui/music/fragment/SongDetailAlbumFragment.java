package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.util.Log;

import com.example.yungui.linelrcview.LineLrcView;
import com.example.yungui.linelrcview.LrcInfo;
import com.example.yungui.linelrcview.LrcUtils;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.widget.CircleImageView;

import java.io.IOException;
import java.io.InputStream;
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

public class SongDetailAlbumFragment extends BaseFragment {
    public static final String TAG = SongDetailAlbumFragment.class.getSimpleName();
    private static final String ROTATE = "rotate";
    private float rotate;
    private LrcInfo lrcInfo;

    private CircleImageView circleImageView;
    private LineLrcView lineLrcView;

    public SongDetailAlbumFragment() {

    }

    public static SongDetailAlbumFragment newInstance(float rotate) {
        SongDetailAlbumFragment fragment = new SongDetailAlbumFragment();
        Bundle args = new Bundle();
        args.putFloat(ROTATE, rotate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_song_detail_album;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        rotate = getArguments().getFloat(ROTATE);
        circleImageView = rootView.findViewById(R.id.circle_cd);
        circleImageView.setCurrentRotateValue(rotate);
        lineLrcView = rootView.findViewById(R.id.lineLrcView);
        loadLrc(lineLrcView);
        updateLrc(lineLrcView);

    }

    @Override
    protected void loadData() {

    }

    /**
     * 跟新歌词
     *
     * @param lineLrcView
     */
    private void updateLrc(final LineLrcView lineLrcView) {
        Observable.interval(100, TimeUnit.MILLISECONDS)
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
                                        lineLrcView.setCurrentTime(value);
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

    private void loadLrc(final LineLrcView lineLrcView) {
        new Thread() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = mContext.getResources().getAssets().open("田馥甄 - 魔鬼中的天使.lrc");
                    lrcInfo = LrcUtils.newInstance()
                            .setupLrcResource(inputStream, "UTF-8")
                            .getLrcInfo();
                    if (lrcInfo != null && lrcInfo.lineInfos != null && lrcInfo.lineInfos.size() > 0) {
                        lineLrcView.setLrcInfo(lrcInfo);
                    }
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
