package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.ImageView;

import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;

import java.util.List;

/**
 * Created by yungui on 2017/11/12.
 */

public class SongDetailAboutFragment extends BaseFragment {
    private static final String TAG = "SongDetailAboutFragment";

    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static SongDetailAboutFragment newInstance(int sectionNumber) {
        SongDetailAboutFragment fragment = new SongDetailAboutFragment();
        return fragment;
    }

    public SongDetailAboutFragment() {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_song_detail_about;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void loadData() {

    }
    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
        Log.e(TAG, "onMetadataChanged: ");

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
        Log.e(TAG, "onPlaybackStateChanged: ");

    }

    @Override
    public void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems) {
        Log.e(TAG, "onMediaItemsLoaded: ");

    }

}
