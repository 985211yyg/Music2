package com.example.yungui.music.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.model.Music;
import com.example.yungui.music.net.NetConstant;
import com.example.yungui.music.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 22892 on 2018/2/1.
 */

public class MusicHallRankingSongFragment extends BaseFragment {
    private static final String TAG = "MusicHallRankingSongFra";

    @BindView(R.id.ranking_song_Loading)
    ProgressBar mRankingSongLoading;
    @BindView(R.id.ranking_song_recyclerView)
    RecyclerView mRankingSongRecyclerView;

    private MediaControllerCompat mMediaControllerCompat;
    private MyMediaControllerCallback mMyMediaControllerCallback;


    private SongAdapter mSongAdapter;
    private List<Music> mMusics = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        mMediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
        mMyMediaControllerCallback = new MyMediaControllerCallback();
        mMediaControllerCompat.registerCallback(mMyMediaControllerCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMusics != null) {
            mMusics = null;
        }
        if (mMediaControllerCompat != null) {
            mMediaControllerCompat.unregisterCallback(mMyMediaControllerCallback);
            mMediaControllerCompat = null;
            mMyMediaControllerCallback = null;
        }
    }

    public static MusicHallRankingSongFragment newInstance() {
        Bundle args = new Bundle();
        MusicHallRankingSongFragment fragment = new MusicHallRankingSongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_music_hall_ranking_song;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mSongAdapter = new SongAdapter(R.layout.fragment_music_hall_ranking_song_item, null);
        mSongAdapter.addHeaderView(getLayoutInflater().inflate(R.layout.fragment_music_hall_ranking_header, null));
        mRankingSongRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRankingSongRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRankingSongRecyclerView.setAdapter(mSongAdapter);
        mRankingSongLoading.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRankingSongLoading.setVisibility(View.INVISIBLE);
                loadData();
            }
        }, 300);
        mSongAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
                builder.setDescription(((Music) adapter.getData().get(position)).getSong_name() +
                        ((Music) adapter.getData().get(position)).getSong_name());
                builder.setTitle(((Music) adapter.getData().get(position)).getSong_name());
                builder.setSubtitle(((Music) adapter.getData().get(position)).getAuthor());
                Log.e(TAG, "onItemClick: " + NetConstant.MP3_URL + ((Music) adapter.getData().get(position)).getSong_id() + ".mp3");
                Log.e(TAG, "onItemClick: "+builder.build().toString() );
//                mMediaControllerCompat.addQueueItem();
            }
        });

    }

    @Override
    protected void loadData() {
        mMusics = JsonUtils.paresMusicFromAssetsSource("musics.json");
        if (mMusics != null && mMusics.size() > 0) {
            mSongAdapter.addData(mMusics);
        }

    }

    public class SongAdapter extends BaseQuickAdapter<Music, BaseViewHolder> {
        public SongAdapter(int layoutResId, @Nullable List<Music> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Music item) {
            helper.setText(R.id.song_item_title, item.getSong_name());
            helper.setText(R.id.song_item_desc, item.getAuthor());
//            helper.setText(R.id.sort_number, helper.getAdapterPosition());
        }
    }

    public class MyMediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    }


}
