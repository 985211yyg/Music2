package com.example.yungui.music.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.LocalRecyclerViewAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.info.MusicInfo;
import com.example.yungui.music.model.viewmodel.MusicInfoViewModel;
import com.example.yungui.music.model.viewmodel.MusicInfoViewModelFactory;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.utils.InjectorUtils;
import com.example.yungui.music.utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yungui on 2017/10/22.
 */

public class MusicFragment extends BaseFragment {
    public static final String TAG = MusicFragment.class.getSimpleName();
    @BindView(R.id.tab_item_recyclerView)
    RecyclerView recyclerView;
    private LocalRecyclerViewAdapter localRecyclerViewAdapter;
    private List<MusicInfo> musics = new ArrayList<>();
    private PlayMusic playMusic;
    private Handler handler;
    private MusicInfoViewModel musicInfoViewModel;

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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册歌曲扫描监广播接收器  ，然后更新列表
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        handler = new Handler();

        MusicInfoViewModelFactory factory = InjectorUtils.provideMusicViewModelFactory(mContext);
        musicInfoViewModel = ViewModelProviders.of(this, factory).get(MusicInfoViewModel.class);
        musicInfoViewModel.getMusicInfo().observe(this, new Observer<List<MusicInfo>>() {
            @Override
            public void onChanged(@Nullable List<MusicInfo> musicInfos) {
                localRecyclerViewAdapter.addData(musicInfos);
                localRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_local_tab_item;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        localRecyclerViewAdapter = new LocalRecyclerViewAdapter(R.layout.music, null);
        recyclerView.setAdapter(localRecyclerViewAdapter);
        localRecyclerViewAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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
    }

    @Override
    protected void loadData() {
        Log.e(TAG, ">>>>>>>>>>>loadData: " + InjectorUtils.provideRepository(mContext).getAllMusicInfo());
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


