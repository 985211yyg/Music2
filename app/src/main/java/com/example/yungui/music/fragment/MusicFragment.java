package com.example.yungui.music.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yungui.music.Interface.IConstants;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.TabRecyclerViewAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.utils.MusicUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yungui on 2017/10/22.
 */

public class MusicFragment extends BaseFragment {
    public static final String TAG = MusicFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private TabRecyclerViewAdapter tabRecyclerViewAdapter;
    private ArrayList<MusicInfo> musics = new ArrayList<>();
    private PlayMusic playMusic;
    private Handler handler;

    public MusicFragment() {
    }

    public static MusicFragment newInstance() {
        MusicFragment musicFragment = new MusicFragment();
        Bundle arg = new Bundle();
        musicFragment.setArguments(arg);
        return musicFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toast.makeText(mContext, "onAttach", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_local_tab_item;
    }

    @Override
    protected void initView() {
        recyclerView = rootView.findViewById(R.id.tab_item_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        tabRecyclerViewAdapter = new TabRecyclerViewAdapter(R.layout.music, null);
        recyclerView.setAdapter(tabRecyclerViewAdapter);
        tabRecyclerViewAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                musics = (ArrayList<MusicInfo>) adapter.getData();
                playMusic = new PlayMusic(position);
                System.out.println(">>>>>>>>>>>>>>>>>>>>" + MusicPlayer.getCurrentAudioId());
                //判断点击的条目是否在播放
                if (MusicPlayer.getCurrentAudioId() != musics.get(position).songId) {
                    ((TextView) view.findViewById(R.id.music_name)).setTextColor(getResources().getColor(R.color.themeColor));
                    ((TextView) view.findViewById(R.id.music_singer)).setTextColor(getResources().getColor(R.color.themeColor));
                    view.findViewById(R.id.status_bar).setVisibility(View.VISIBLE);
                    handler.postDelayed(playMusic, 70);
                } else {
                    view.findViewById(R.id.status_bar).setVisibility(View.INVISIBLE);
                }
            }
        });
        loadData();
    }

    private void loadData() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> loadData");
        Observable.fromIterable(MusicUtils.queryMusic(mContext, IConstants.START_FROM_LOCAL))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MusicInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MusicInfo value) {
                        tabRecyclerViewAdapter.addData(value);
                        tabRecyclerViewAdapter.notifyDataSetChanged();
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
    public void updateAdapter() {
        super.updateAdapter();
        loadData();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 播放音乐的线程
     */
    class PlayMusic implements Runnable {

        public PlayMusic(int position) {
            this.position = position;
        }

        private int position;

        @Override
        public void run() {
            Log.e(TAG, "run: " + musics.get(position).data);
            //创建一个和音乐数量一致的数组,装载歌曲id
            long[] list = new long[musics.size()];
            //id和音乐信息类
            HashMap<Long, MusicInfo> infos = new HashMap<>();
            for (int i = 0; i < musics.size(); i++) {
                //音乐信息
                MusicInfo info = musics.get(i);
                //歌曲ID
                list[i] = info.songId;
                //是否是本地音乐
                info.islocal = true;
                info.albumData = MusicUtils.getAlbumArtUri(info.albumId) + "";
                infos.put(list[i], info);
            }
            MusicPlayer.playAll(infos, list, position, false);
            Log.e(TAG, "run: " + musics.size() + "点击位置" + position);
        }
    }

}

