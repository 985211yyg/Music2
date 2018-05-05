package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.yungui.linelrcview.LineLrcView;
import com.example.yungui.linelrcview.LrcInfo;
import com.example.yungui.music.R;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.widget.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by yungui on 2017/11/12.
 */

public class SongDetailAlbumFragment extends BaseFragment {
    public static final String TAG = SongDetailAlbumFragment.class.getSimpleName();
    private static final String ROTATE = "rotate";
    @BindView(R.id.singer_linearLayout)
    LinearLayout singerLinearLayout;
    @BindView(R.id.song_info_linearLayout)
    LinearLayout songInfoLinearLayout;
    @BindView(R.id.circle_cd)
    CircleImageView circleCd;
    @BindView(R.id.lineLrcView)
    LineLrcView lineLrcView;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    Unbinder unbinder;
    private float rotate;
    private LrcInfo lrcInfo;

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

    public SongDetailAlbumFragment() {

    }

    public static SongDetailAlbumFragment newInstance(float rotate) {
        SongDetailAlbumFragment fragment = new SongDetailAlbumFragment();
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_song_detail_album;
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
