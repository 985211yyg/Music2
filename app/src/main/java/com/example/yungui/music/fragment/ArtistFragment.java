package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

import java.util.List;

/**
 * Created by yungui on 2017/10/22.
 */

public class ArtistFragment extends BaseFragment {
    public ArtistFragment() {
    }
    public static ArtistFragment newInstance() {
        ArtistFragment artistFragment = new ArtistFragment();
        Bundle arg = new Bundle();
        artistFragment.setArguments(arg);
        return artistFragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_local_tab_item;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

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
