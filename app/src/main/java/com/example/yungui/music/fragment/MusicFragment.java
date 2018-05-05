package com.example.yungui.music.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
    private MusicInfoViewModel musicInfoViewModel;
    private MediaControllerCompat mMediaControllerCompat;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onResume() {
        super.onResume();
        mMediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public MusicFragment() {

    }

    public static MusicFragment newInstance() {
        MusicFragment musicFragment = new MusicFragment();
        Bundle arg = new Bundle();
        musicFragment.setArguments(arg);
        return musicFragment;
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
                mMediaControllerCompat.getTransportControls().skipToQueueItem(position);
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat) {

    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {

    }

    @Override
    public void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems) {

    }

}


